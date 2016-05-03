package me.springremoting.client;

import me.springremoting.rmi.IRmiSolrService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:DateServiceTest-context.xml"})
public class RmiSolrServiceTest{

	@Autowired
	private  IRmiSolrService rmiSolrService;

	@Test
	public void testGetDate() throws Exception {
		String buildSchemaXmlReq="{\"serviceName\":\"xml_schema_test\",\"param\":{\"start\":\"0\",\"rows\":\"10\"}}";
		String buildIndexReq="{\"serviceName\":\"solr_java_city_channel\",\"param\":{\"start\":\"0\",\"rows\":\"10\"}}";
		int threadNum=1;
		System.out.println(rmiSolrService.build(buildSchemaXmlReq, buildIndexReq, threadNum));
	}
}