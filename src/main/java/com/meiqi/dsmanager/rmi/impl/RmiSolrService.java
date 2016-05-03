package com.meiqi.dsmanager.rmi.impl;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.rmi.IRmiSolrService;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.HttpExecutor;
import com.meiqi.dsmanager.util.SftpUtil;
import com.meiqi.dsmanager.util.SolrClientUtil;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.dsmanager.util.XmlUtil;
import com.meiqi.thread.ThreadCallback;
import com.meiqi.thread.ThreadHelper;

@RemoteService(serviceInterface = IRmiSolrService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiSolrService implements IRmiSolrService {
	
	private static final Logger LOG               = Logger.getLogger(RmiSolrService.class);
	
	@Autowired
	private ThreadHelper  indexTheadHelper;
	
	@Autowired
	private IDataAction dataAction;
	
	@Autowired
	private ISolrAction solrAction;
	
	private static final String FILE_SEPARATOR = "/";

    /*
    * Title: query
    * Description: 
    * @param content
    * @return 
    * @see com.meiqi.dsmanager.rmi.IRmiSolrService#query(java.lang.String) 
    */
    @Override
    public String query(String content)
    {
        String decodeContent = CommonUtil.getDecodeContent(content);
        DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
        String resultData = "";
        resultData = solrAction.query(dsReqInfo);
        return resultData;
    }
	/*
	* Title: build
	* Description: 
	* @param buildSchemaXmlReq
	* @param buildIndexReq
	* @param threadNum
	* @return 
	* @see com.meiqi.dsmanager.rmi.IRmiSolrService#build(java.lang.String, java.lang.String, int) 
	*/
	@Override
	public int build(String buildSchemaXmlReq, String buildIndexReq,int threadNum) {
		String decodeContent = CommonUtil.getDecodeContent(buildSchemaXmlReq);
		DsManageReqInfo reqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		DataSources dataSource = dataAction.getDataSource(reqInfo.getServiceName(), reqInfo.getStyleSn());
		RuleServiceResponseData result = null;
		String localSchemaDir = "";
		try {
			  result = dataAction.getData(reqInfo, dataSource);
			//for test
			//String content = "{\"code\":\"5\",\"description\":\"成功\",\"rows\":[{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"sort_order\",\"DATA_TYPE\":\"float\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"shop_price\",\"DATA_TYPE\":\"float\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"market_price\",\"DATA_TYPE\":\"float\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"total_sold_yes_count\",\"DATA_TYPE\":\"int\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"click_count\",\"DATA_TYPE\":\"int\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"new_goods_name\",\"DATA_TYPE\":\"varchar\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"new_goods_keywords\",\"DATA_TYPE\":\"varchar\"},{\"SCHEMA_NAME\":\"java_city_channel\",\"COLUMN_NAME\":\"goods_id\",\"DATA_TYPE\":\"mediumint\"}]}";
			//result=DataUtil.parse(content,RuleServiceResponseData.class);
			
			// get rows
			List<Map<String, String>> rows = result.getRows();
			if (rows == null) {
				rows = Collections.emptyList();
			}

			// get schemaName
			String schemaName = null;
			Iterator<Map<String, String>> it = rows.iterator();  
			if (it.hasNext()) {
				Map<String, String> row = it.next();
	        	schemaName = row.get("SCHEMA_NAME");
			}
	        if (null == schemaName || "".equals(schemaName)) {
	        	throw new Exception("Can not find schemaName");
	        }
	        
	        // 生成远端需要的目录
			String host = SysConfig.getValue("solr.host");
			String port = SysConfig.getValue("solr.port");
			String username = SysConfig.getValue("solr.username");  
		    String password = SysConfig.getValue("solr.password");
			String solrHome = SysConfig.getValue("solr.home");
	        ChannelSftp sftp = SftpUtil.connect(host, Integer.parseInt(port), username, password);
	        
	        String confDir = schemaName + FILE_SEPARATOR + "conf";
	        mkdirs(solrHome,confDir, sftp);
	        
	        String dataDir = schemaName + FILE_SEPARATOR + "data";
	        mkdirs(solrHome,dataDir + FILE_SEPARATOR + "index", sftp);
	        mkdirs(solrHome,dataDir + FILE_SEPARATOR + "tlog", sftp);


	        // 本地生成需要的目录、文件
	        String tmpDirectory = SysConfig.getValue("solr.localTmpDirectory");
	        localSchemaDir = System.getProperty("user.dir") + FILE_SEPARATOR + tmpDirectory + FILE_SEPARATOR + schemaName;
	        String localConfPath = localSchemaDir + FILE_SEPARATOR + "conf";
			
	        String localSolrconfigXml = localConfPath + FILE_SEPARATOR + "solrconfig.xml";
	        //生成solrconfig.xml文件
			generateSolrconfigXml(localSolrconfigXml);
			
			String localSchemaXml = localConfPath + FILE_SEPARATOR + "schema.xml";
			//生成schema.xml文件
			generateSchemaXml(localSchemaXml, rows);
			
			// 上传生成的文件到远端(索引服务器)
			SftpUtil.upload(solrHome+ FILE_SEPARATOR +confDir, localSolrconfigXml, sftp);
			SftpUtil.upload(solrHome+ FILE_SEPARATOR +confDir, localSchemaXml, sftp);
			
			//断开ftp连接
			sftp.disconnect();
			
			// 如果所有服务器上已经有生成好的core那么重启core否则创建core
			loadSchema(schemaName);
			
			//生成索引
			//createIndex(buildIndexReq, threadNum,schemaName);
	        
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		} finally {
			// 删除本地生成的临时文件
			deldir(localSchemaDir);
		}
		return 1;
	}

	private void mkdirs(String parentDir,String dirStr, ChannelSftp sftp) throws SftpException {
		String parent=parentDir;
		String[] array=dirStr.split(FILE_SEPARATOR);
		for(String dir:array){
			SftpUtil.cd(parent, sftp);
//			if(SftpUtil.get(dir, sftp)==null){
//				SftpUtil.mkdir(dir, sftp);
//			}
			SftpUtil.mkdir(dir, sftp);
			parent=dir;
		}
	}
	
	private void deldir(String dir){
		File file = new File(dir);
		if(file.exists()){
			if(file.isDirectory()){
				File[] files = file.listFiles();
				for(int i=0;i<files.length;i++){
					deldir(files[i].getAbsolutePath());
				}
			}
			file.delete();
		}
	}
	
	private void createIndex(String buildIndexReq, int threadNum,String collectionName) {
		//生成索引
		String decodeContentIndex = CommonUtil.getDecodeContent(buildIndexReq);
		DsManageReqInfo reqInfoindex = DataUtil.parse(decodeContentIndex, DsManageReqInfo.class);
		DataSources dataSourceindex = dataAction.getDataSource(reqInfoindex.getServiceName(), reqInfoindex.getStyleSn());
		try {
			RuleServiceResponseData result = dataAction.getData(reqInfoindex,dataSourceindex);
			//String json="{\"code\":\"5\",\"description\":\"成功\",\"rows\":[{\"sort_order\":\"0.2\",\"shop_price\":\"17\",\"market_price\":\"0\",\"total_sold_yes_count\":\"0\",\"click_count\":\"0\",\"new_goods_name\":\"MY-610 团购补差价\",\"new_goods_keywords\":\"补差价\",\"goods_id\":\"148234\"}]}";
			//RuleServiceResponseData result=DataUtil.parse(json,RuleServiceResponseData.class);
			List<Map<String,String>> rows=result.getRows();
			List<SolrInputDocument> docs=new ArrayList<SolrInputDocument>();
			if(threadNum==0){
				threadNum= Integer.parseInt(SysConfig.getValue("solr.defaultThreadNum"));
			}
			int total=rows.size();
			int size=total/threadNum;
			 for(int i=0;i<total;i++){
				 SolrInputDocument doc=new SolrInputDocument();
				 Map<String,String> row=rows.get(i);
				 for(String key:row.keySet()){
					 doc.addField(key, row.get(key));
				 }
				 docs.add(doc);
				 if(i!=0&&((i+1)%size==0)){
					 //使用多线程做业务
					 BatBuildIndexThread thread=new BatBuildIndexThread(docs,collectionName);
					 indexTheadHelper.execute(thread);
					 docs=new ArrayList<SolrInputDocument>();
				 }
				 if(i==total-1){
					 //使用多线程做业务
					 BatBuildIndexThread thread=new BatBuildIndexThread(docs,collectionName);
					 indexTheadHelper.execute(thread);
					 docs=new ArrayList<SolrInputDocument>();
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info(e.getMessage());
		}
		
	}
	
	/** 生成文件 solrconfig.xml
	 * 
	 */
	private void generateSolrconfigXml(String localSolrconfigXml) throws Exception {
		FileWriter fw = null;
		try {
			String separator = System.getProperty("line.separator");
			
			File file = new File(localSolrconfigXml); 
			//判断目标文件所在的目录是否存在  
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
			// 生成 solrconfig.xml
            fw = new FileWriter(localSolrconfigXml);
            
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + separator);
    		fw.write("<config>" + separator);
    		fw.write("  <luceneMatchVersion>4.7</luceneMatchVersion>" + separator);
    		fw.write("  <directoryFactory name=\"DirectoryFactory\" class=\"${solr.directoryFactory:solr.StandardDirectoryFactory}\"/>" + separator);
    		fw.write("  <dataDir>${solr.core0.data.dir:}</dataDir>" + separator);
    		fw.write("  <schemaFactory class=\"ClassicIndexSchemaFactory\"/>" + separator);
    		fw.write("  <updateHandler class=\"solr.DirectUpdateHandler2\">" + separator);
    		fw.write("    <updateLog>" + separator);
    		fw.write("      <str name=\"dir\">${solr.core0.data.dir:}</str>" + separator);
    		fw.write("    </updateLog>" + separator);
    		fw.write("  </updateHandler>" + separator);
    		fw.write("  <requestHandler name=\"/get\" class=\"solr.RealTimeGetHandler\">" + separator);
    		fw.write("    <lst name=\"defaults\">" + separator);
    		fw.write("      <str name=\"omitHeader\">true</str>" + separator);
    		fw.write("    </lst>" + separator);
    		fw.write("  </requestHandler>" + separator);
    		fw.write("  <requestHandler name=\"/replication\" class=\"solr.ReplicationHandler\" startup=\"lazy\" />" + separator);
    		fw.write("  <requestDispatcher handleSelect=\"true\" >" + separator);
    		fw.write("    <requestParsers enableRemoteStreaming=\"false\" multipartUploadLimitInKB=\"2048\" formdataUploadLimitInKB=\"2048\" />" + separator);
    		fw.write("  </requestDispatcher>" + separator);
    		fw.write("  <requestHandler name=\"standard\" class=\"solr.StandardRequestHandler\" default=\"true\" />" + separator);
    		fw.write("  <requestHandler name=\"/analysis/field\" startup=\"lazy\" class=\"solr.FieldAnalysisRequestHandler\" />" + separator);
    		fw.write("  <requestHandler name=\"/update\" class=\"solr.UpdateRequestHandler\"  />" + separator);
    		fw.write("  <requestHandler name=\"/admin/\" class=\"org.apache.solr.handler.admin.AdminHandlers\" />" + separator);
    		fw.write("  <requestHandler name=\"/admin/ping\" class=\"solr.PingRequestHandler\">" + separator);
    		fw.write("    <lst name=\"invariants\">" + separator);
    		fw.write("      <str name=\"q\">solrpingquery</str>" + separator);
    		fw.write("    </lst>" + separator);
    		fw.write("    <lst name=\"defaults\">" + separator);
    		fw.write("      <str name=\"echoParams\">all</str>" + separator);
    		fw.write("    </lst>" + separator);
    		fw.write("  </requestHandler>" + separator);
    		fw.write("  <admin>" + separator);
    		fw.write("    <defaultQuery>solr</defaultQuery>" + separator);
    		fw.write("  </admin>" + separator);
    		fw.write("</config>" + separator);
            
            fw.close();
        } catch (Exception e) {
        	e.printStackTrace();
        	LOG.error(e.getMessage());
            throw e;
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }
	}
	
	/** 生成文件 schemaXml.xml
	 * 
	 */
	private void generateSchemaXml(String localSchemaXml, List<Map<String, String>> rows) throws Exception {
		FileWriter fw = null;
		try {
			String separator = System.getProperty("line.separator");
			
			File file = new File(localSchemaXml); 
			//判断目标文件所在的目录是否存在  
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
            // 生成 schema.xml
            fw = new FileWriter(localSchemaXml);

            fw.write("<?xml version=\"1.0\"?>" + separator);
            fw.write("<!-- Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. -->" + separator); 
            fw.write("<schema name=\"example core one\" version=\"1.1\">" + separator);
    		fw.write("  <types>" + separator);
    		fw.write("   <fieldtype name=\"string\"  class=\"solr.StrField\" sortMissingLast=\"true\" omitNorms=\"true\"/>" + separator);
    		fw.write("   <fieldType name=\"long\" class=\"solr.TrieLongField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>" + separator);
    		fw.write("   <fieldType name=\"int\" class=\"solr.TrieIntField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>" + separator);
    		fw.write("   <fieldType name=\"date\" class=\"solr.TrieDateField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>" + separator);
    		fw.write("   <fieldType name=\"boolean\" class=\"solr.BoolField\"  sortMissingLast=\"true\"/>" + separator);
    		fw.write("   <fieldType name=\"float\" class=\"solr.TrieFloatField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>" + separator);
    		fw.write("   <fieldType name=\"double\" class=\"solr.TrieDoubleField\" precisionStep=\"0\" positionIncrementGap=\"0\"/>" + separator);
    		fw.write("  </types>" + separator);
            fw.write(separator);
    		fw.write(" <fields>" + separator);
    		fw.write("  <!-- general -->" + separator);
    		
    		String indexed = "true";
        	String stored = "true";
        	String multiValued = "false";
            		 
            Iterator<Map<String, String>> it = rows.iterator();  
            while (it.hasNext()) {
            	Map<String, String> row = it.next();
            	fw.write("  <field name=\"" + row.get("COLUMN_NAME") 
            			+ "\" type=\"" + row.get("DATA_TYPE") 
            			+ "\" indexed=\"" + indexed 
            			+ "\" stored=\"" + stored 
            			+ "\" multiValued=\"" + multiValued + "\" />" + separator);
            }
            
	   		fw.write(" </fields>" + separator);
	        fw.write(separator);
	   		fw.write(" <!-- field to use to determine and enforce document uniqueness. -->" + separator);
	   		fw.write(" <uniqueKey>id</uniqueKey>" + separator);
	        fw.write(separator);
	   		fw.write(" <!-- field for the QueryParser to use when an explicit fieldname is absent -->" + separator);
	   		fw.write(" <defaultSearchField>album</defaultSearchField>" + separator);
	        fw.write(separator);
	   		fw.write(" <!-- SolrQueryParser configuration: defaultOperator=\"AND|OR\" -->" + separator);
	   		fw.write(" <solrQueryParser defaultOperator=\"OR\"/>" + separator);
	   		fw.write("</schema>" + separator);
            
            fw.close();
            
        } catch (Exception e) {
        	e.printStackTrace();
        	LOG.error(e.getMessage());
            throw e;
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }
	}
	
	private void loadSchema(String schemaName) throws Exception {
		String solrServerUrl = SysConfig.getValue("solr.solrServerUrl");
		String solrHome = SysConfig.getValue("solr.home");
		String url = solrServerUrl + "/admin/cores?action=CREATE&name=" + schemaName + "&instanceDir=" + schemaName + "&dataDir=" + solrHome + "/" + schemaName + "/data";
		String retXml = HttpExecutor.get(url);
		// 返回结果中status为0表示成功，如果返回结果中status为500，且errorMsg为指定格式则发送RELOAD请求
		String status = XmlUtil.getValueFromXml("/response/lst/int", retXml);
		String errorMsg = XmlUtil.getValueFromXml("/response/lst/str", retXml);
		if ("0".equals(status)) {
			return;
		}
		else if ("500".equals(status) && errorMsg != null && ("Core with name '" + schemaName + "' already exists.").equals(errorMsg)) {
			//当前的core已经存在，那么重启（reload）
			reLoadSchema(schemaName);
		} else {
			throw new Exception(errorMsg);
		}
	}
	
	private void reLoadSchema(String schemaName) throws Exception {
		String solrServerUrl = SysConfig.getValue("solr.solrServerUrl");
		String url = solrServerUrl + "/admin/cores?action=RELOAD&core=" + schemaName;
		String retXml = HttpExecutor.get(url);
		// 返回结果中status为0表示成功
		//TODO
		String status = XmlUtil.getValueFromXml("/response/lst/int", retXml);
		if ("0".equals(status)) {
			return;
		} else {
			String errorMsg = XmlUtil.getValueFromXml("/response/lst/str", retXml);
			throw new Exception(errorMsg);
		}
	}

	class BatBuildIndexThread implements ThreadCallback {
		   private List<SolrInputDocument> docs;
		   private String collectionName;
	       public BatBuildIndexThread(List<SolrInputDocument> docs,String collectionName) {
	            super();
	            this.docs=docs;
	            this.collectionName=collectionName;
	       }

	        @Override
	        public void run() {
	        	String url = SysConfig.getValue("solr.solrServerUrl") + "/" + collectionName;
	            HttpSolrServer client = SolrClientUtil.getInstance(url);
	        	SolrClientUtil.addDocs(client, docs);
	        	docs=null;
	        }
	 }
	
}
