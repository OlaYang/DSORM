package com.meiqi.openservice.action.javabin.campaign;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.bean.UtmInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * 百度推广相关业务处理
 * 
 * @author: luzicong
 * @version: 1.0, 2015年7月13日
 * @version: 1.1 2015年8月4日 修改 getPromotionInfo，对推广链接的结构进行调整
 */

@Service
public class PromotionAction extends BaseAction {
    private static final Logger LOG           = Logger.getLogger(PromotionAction.class);

    private static final String JSONPATH      = AppSysConfig.getValue("jsonPath");

    private static final String UTM_KEYWORDID = "&adp={adposition}&pa={pagenum}&pl={placement}&kid={keywordid}&cre={creative}";

    private static final String UTM_TERM      = "&k={keyword}&pl={placement}&kid={keywordid}&cre={creative}";

    private enum UtmCell {
        utmCampaign, utmContent, utmTerm, utmPattern, utmBid, landingpage, mobileLandingpage, utmStatu, maxCell
    }



    /**
     * 
     * @Description:获取推广基础信息
     * @param @return
     * @return String
     * @throws
     */
    public String getPromotionInfo(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.info("Function:getPromotionInfo.Start.");
        String allCategoryJson = JsonUtils.readJsonFile(basePath + JSONPATH + "PromotionInfo.json");
        LOG.info("Function:getPromotionInfo.End.");
        return allCategoryJson;
    }



    /**
     * 
     * @Description: 上传Excel 文件，完成跟踪代码后台制作，然后返回下载链接
     * @param request
     * @param response
     * @param repInfo
     * @return String
     */
    public String batchUpdateUrls(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        LOG.debug("Function:batchUpdateUrls.Start.");
        ResponseInfo resp = new ResponseInfo(DsResponseCodeData.ERROR.code, "");

        Map<String, String> map = DataUtil.parse(repInfo.getParam(), Map.class);
        String utmMode = map.get("utmMode");
        if (StringUtils.isBlank(utmMode)) {
            utmMode = "paid";
        }

        String utmSource = map.get("utmSource");
        if (StringUtils.isBlank(utmSource)) {
            resp.setDescription("请求参数 utmSource 不能为空");
            return JSON.toJSONString(resp);
        }

        String utmAccount = map.get("utmAccount");
        if (StringUtils.isBlank(utmAccount)) {
            resp.setDescription("请求参数 utmAccount 不能为空");
            return JSON.toJSONString(resp);
        }

        // 转型为MultipartHttpRequest：
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        // 获得文件
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null) {
            resp.setDescription("找不到文件");
            return JSON.toJSONString(resp);
        }

        // 获得文件名：
        String tmpDirectory = SysConfig.getValue("localTmpDirectory");
        String path = System.getProperty("user.dir") + File.separator + tmpDirectory + File.separator + "promotion" + File.separator;
//        String path = request.getContextPath() + File.separator + "upload" + File.separator + "promotion" + File.separator;
        String fileName = System.currentTimeMillis()
                + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fulPath = path + fileName;
        try {
            fulPath = URLDecoder.decode(fulPath, "utf-8"); // 防止服务器路径中包含空格等问题
        } catch (UnsupportedEncodingException e) {
            resp.setDescription(e.getMessage());
            return JSON.toJSONString(resp);
        }

        File uploadFile = new File(fulPath);
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.mkdirs();
        }

        // 写入文件
        try {
            file.transferTo(uploadFile);
        } catch (IllegalStateException e) {
            resp.setDescription(e.getMessage());
            return JSON.toJSONString(resp);
        } catch (IOException e) {
            resp.setDescription(e.getMessage());
            return JSON.toJSONString(resp);
        }

        // 处理文件, 向文件中添加访问url和移动访问url
        ServletOutputStream out = null;
        FileInputStream fis = null;
        try {
            String urlCommonParams = "#" + encode(utmMode) + "&se=" + encode(utmSource) + "&z=" + encode(utmAccount);

            String msg = addUrlsToExcel(uploadFile, urlCommonParams, utmSource);
            if (!StringUtils.isBlank(msg)) {
                if (msg.length() > 1024) {
                    msg = msg.substring(0, 1024) + "...";
                }
                LOG.warn(msg);
            }

            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);// 设定输出文件头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// 定义输出类型

            response.addHeader("description", encode(msg));

            out = response.getOutputStream();

            fis = new java.io.FileInputStream(uploadFile);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(4096);

            byte[] cache = new byte[4096];
            for (int offset = fis.read(cache); offset != -1; offset = fis.read(cache)) {
                byteOutputStream.write(cache, 0, offset);
            }

            byte[] bt = null;
            bt = byteOutputStream.toByteArray();

            out.write(bt);
            out.flush();

            resp.setCode(DsResponseCodeData.SUCCESS.code);

        } catch (Exception e) {
            resp.setCode(DsResponseCodeData.ERROR.code);
            resp.setDescription(e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (uploadFile.exists()) {
                    uploadFile.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        }

        LOG.debug("Function:batchUpdateUrls.End.");
        return JSON.toJSONString(resp);
    }



    private String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str.trim(), "UTF-8");
    }



    private String addUrlsToExcel(File uploadFile, String url, String utmSource) throws Exception {
        StringBuffer errorMsg = new StringBuffer();

        if (!uploadFile.exists()) {
            throw new Exception("文件上传失败");
        }

        String fulePath = uploadFile.getAbsolutePath();
        String suffix = fulePath.substring(fulePath.lastIndexOf(".")); // 文件后辍.

        int sheets = 0;

        Workbook workBook = null;
        InputStream is = new FileInputStream(uploadFile);
        FileOutputStream fileOut = null;
        try {
            if (".xls".equals(suffix)) { // 97-03 workBook = new
                workBook = new HSSFWorkbook(is);
            } else if (".xlsx".equals(suffix)) { // 2007
                workBook = new XSSFWorkbook(is);
            } else {
                throw new Exception("不支持的文件类型!");
            }

            sheets = null != workBook ? workBook.getNumberOfSheets() : 0;
            if (sheets <= 0) {
                throw new Exception("没有sheet!");
            }

            for (int i = 0; i < sheets; i++) {
                Sheet sheet = workBook.getSheetAt(i); // 读取第一个sheet

                int rows = sheet.getPhysicalNumberOfRows(); // 获得行数
                int lastRowNum = sheet.getLastRowNum();

                if (rows <= 1) { // 第一行默认为标题
                    if (i == 0) {
                        errorMsg.append("第" + (i + 1) + "个sheet中数据行数<=1!\n");
                    }
                    continue;
                }

                for (int j = 1; j <= lastRowNum; j++) {
                    Row row = sheet.getRow(j);
                    if (null == row) {
                        errorMsg.append("第" + (i + 1) + "个sheet,第" + (j + 1) + "行数据没有列数为空!\n");
                        continue;
                    }

                    int cellNum = row.getLastCellNum();// 获得列数
                    if (cellNum < UtmCell.maxCell.ordinal()) {
                        errorMsg.append("第" + (i + 1) + "个sheet,第" + (j + 1) + "行数据列数不正确!\n");
                        continue;
                    } else if (cellNum > UtmCell.maxCell.ordinal()) {
                        errorMsg.append("第" + (i + 1) + "个sheet,第" + (j + 1) + "行数据列数超过了最大储存的个数"
                                + UtmCell.maxCell.ordinal() + "，忽略多余的列!\n");
                    }

                    UtmInfo utmInfo = new UtmInfo();

                    String utmCampaign = getCellStringValue(row, UtmCell.utmCampaign);
                    if (StringUtils.isBlank(utmCampaign)) {
                        errorMsg.append("第" + (i + 1) + "个sheet,第" + (j + 1) + "行，utmCampaign列数据为空!\n");
                        continue;
                    }
                    utmInfo.setUtmCampaign(utmCampaign);
                    utmInfo.setUtmContent(getCellStringValue(row, UtmCell.utmContent));
                    utmInfo.setUtmTerm(getCellStringValue(row, UtmCell.utmTerm));
                    utmInfo.setUtmPattern(getCellStringValue(row, UtmCell.utmPattern));
                    utmInfo.setUtmBid(getCellStringValue(row, UtmCell.utmBid));
                    utmInfo.setLandingpage(getCellStringValue(row, UtmCell.landingpage));
                    utmInfo.setMobileLandingpage(getCellStringValue(row, UtmCell.mobileLandingpage));
                    utmInfo.setUtmStatu(getCellStringValue(row, UtmCell.utmStatu));

                    // 拼接 utmLandingpage 并更新Cell值
                    if (row.getCell(UtmCell.landingpage.ordinal()) != null) {
                        String landingpage = getLandingPage(utmInfo, utmInfo.getLandingpage(), url, utmSource);
                        utmInfo.setLandingpage(landingpage);
                        row.getCell(UtmCell.landingpage.ordinal()).setCellValue(landingpage);
                    }

                    // 拼接 utmMobileLandingpage
                    if (row.getCell(UtmCell.mobileLandingpage.ordinal()) != null) {
                        String mobileLandingpage = getLandingPage(utmInfo, utmInfo.getMobileLandingpage(), url,
                                utmSource);
                        utmInfo.setLandingpage(mobileLandingpage);
                        row.getCell(UtmCell.mobileLandingpage.ordinal()).setCellValue(mobileLandingpage);
                    }
                }
            }

            // 写入文件
            fileOut = new FileOutputStream(fulePath);
            workBook.write(fileOut);
            fileOut.flush();
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("解析xls文件出错!");
        } finally {
            try {
                is.close();

                if (fileOut != null) {
                    fileOut.close();
                }

                if (workBook != null) {
                    workBook.close();
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }

        return errorMsg.toString();
    }



    private String getCellStringValue(Row row, UtmCell index) {
        Cell cell = row.getCell(index.ordinal());
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }



    private String getLandingPage(UtmInfo utmInfo, String ladingPage, String url, String utmSource)
            throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isBlank(ladingPage)) {
            return sb.toString();
        }

        sb.append(ladingPage);
        sb.append(url);

        sb.append("&j=").append(encode(utmInfo.getUtmCampaign()));
        if (!StringUtils.isBlank(utmInfo.getUtmContent())) {
            sb.append("&d=").append(encode(utmInfo.getUtmContent()));
        }
        if (!StringUtils.isBlank(utmInfo.getUtmTerm()) && !utmSource.equals("show-bdwm")) {
            sb.append("&k=").append(encode(utmInfo.getUtmTerm()));
        }

        if (utmSource.equals("search-bdss")) {
            sb.append(UTM_KEYWORDID);
        }

        if (utmSource.equals("show-bdwm")) {
            sb.append(UTM_TERM);
        }

        return sb.toString();
    }
}
