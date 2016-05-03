package com.meiqi.data.engine.functions.third;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.user.handler.service.ServiceRespInfo;
import com.meiqi.data.util.ConfigUtil;
import com.meiqi.data.util.HttpClientUtil;
import com.meiqi.data.util.LogUtil;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-8-22
 * Time: 下午4:50
 * To change this template use File | Settings | File Templates.
 */
public class _T_SOLR extends Function {
    public static final String NAME = _T_SOLR.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        /*args:
        *  指定搜索关键词- 返回字段名称(若为map则依次取key值，遇到是数组则返回)-参数分隔符(|)-升序/降序-返回条数-是否去重-结果集拼接符-可选参数值-可选参数名
        */
        try {
            if (args.length < 6) {
                throw new ArgsCountError(NAME);
            }
            String keywords = DataUtil.getStringValue(args[0]);

            String baseURL = "";

            int flag = -1;
            List<Map<String, String>> rows = IKDictionary.getSolrKeywords();

            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    Map<String, String> row = rows.get(i);
                    if (keywords.equals(row.get("关键字")) || keywords.equals(row.get("商品拼音"))) {
                        flag = 0; // 满足关键词搜索
                        baseURL = "/keywords/" + row.get("商品拼音") + "/";
                        break;
                    }
                }
            }


            // 父类子类搜索
            // param.put("serviceName", "MLL_BUV1_GoodsCat");
            List<Map<String, String>> rows4parent = IKDictionary.getSolrGoodsCats();
            if (rows4parent != null) {
                for (int i = 0; i < rows4parent.size(); i++) {
                    Map<String, String> row = rows4parent.get(i);
                    if (keywords.equals(row.get("父分类拼音")) || keywords.equals(row.get("父分类名称"))) {
                        flag = 1; // 满足父类搜索
                        baseURL = row.get("父分类URL");
                        break;
                    } else if (keywords.equals(row.get("分类拼音")) || keywords.equals(row.get("分类名称"))) {
                        flag = 2; //满足子类搜索
                        baseURL = row.get("分类URL");
                    }
                }
            }

            List<String> returnKeys = new ArrayList<String>();
            int argSplitIndex = 1;
            for (int i = 1; i < args.length; i++) {
                String returnKey = DataUtil.getStringValue(args[i]);
                if ("|".equals(returnKey)) {
                    argSplitIndex = i;
                    break;
                }
                returnKeys.add(returnKey);
            }
            if (argSplitIndex < 2 || argSplitIndex + 1 + 4 > args.length) {
                if (argSplitIndex == 1) {
                    returnKeys.add("goods_list");
                    returnKeys.add("id");
                } else {
                    throw new RengineException(calInfo.getServiceName(), NAME + "输入参数个数不匹配");
                }
            }

            String orderType = DataUtil.getStringValue(args[argSplitIndex + 1]);
            if (!"asc".equals(orderType.toLowerCase()) && !"desc".equals(orderType.toLowerCase())) {
                throw new RengineException(calInfo.getServiceName(), NAME + "排序方式输入不正确,升序为asc,降序为desc");
            }

            String returnNumStr = DataUtil.getStringValue(args[argSplitIndex + 2]);
            int returnNum = 0;
            try {
                returnNum = Integer.parseInt(returnNumStr);
                if (returnNum <= 0) {
                    throw new RengineException(calInfo.getServiceName(), NAME + "返回条数应为正整数");
                }
            } catch (Exception e) {
                throw new RengineException(calInfo.getServiceName(), NAME + "返回条数格式错误");
            }

            boolean needFilter = false;
            if (args[argSplitIndex + 3] instanceof Boolean) {
                needFilter = (Boolean) args[argSplitIndex + 3];
            } else {
                throw new RengineException(calInfo.getServiceName(), NAME + "是否去重格式输入错误");
            }

            String joinSign = DataUtil.getStringValue(args[argSplitIndex + 4]); // 结果集拼接符


            Map<String, Object> currentParam = new HashMap<String, Object>();
            if (args.length > argSplitIndex + 1 + 4) {
                currentParam = getParam(args, argSplitIndex + 1 + 4, calInfo.getParam(), true);
            }
            currentParam.put("keywords", keywords);
            String solrURL = DataUtil.createHttpURL(ConfigUtil.getSolr_host(), makeSolrURI(currentParam, flag, baseURL));
            String solrRespStr = null;
            try {
                solrRespStr = HttpClientUtil.get(solrURL);
            } catch (Exception e) {
                LogUtil.error("HttpClient error:" + e.getMessage());
            }


            if (solrRespStr == null) {
                return StringPool.EMPTY;
            } else {
                LinkedHashMap<String, Object> solrResp = JSON.parseObject(solrRespStr, LinkedHashMap.class);
                Object pageCount = solrResp.get("page_count");
                if (pageCount == null) {
                    return StringPool.EMPTY;
                }
                int page_count = Integer.parseInt(String.valueOf(pageCount));
                // Map<String, Object> tempSolrResp = solrResp;


                if (!"goods_list".equals(returnKeys.get(0))) {
                    throw new RengineException(calInfo.getServiceName(), NAME + "暂未支持的返回字段值:" + returnKeys.get(0));
                } else {
                    if (returnKeys.size() != 2) {
                        throw new RengineException(calInfo.getServiceName(), NAME + "solr字段匹配未成功");
                    }
                    Object goodsObj = solrResp.get("goods_list");
                    if (goodsObj == null) {
                        return StringPool.EMPTY;
                    }
                    List<Map<String, Object>> goods = (List<Map<String, Object>>) solrResp.get("goods_list");
                    if (goods.size() == 0) {
                        return StringPool.EMPTY;
                    }
                    int currentPage = 1;
                    List<Object> result = new ArrayList<Object>();

                    if ("asc".equals(orderType)) {   // 升序
                        for (int i = 0; i < goods.size(); i++) {
                            result.add(goods.get(i).get(returnKeys.get(1)));

                        }

                        if (goods.size() < returnNum) { // 第一页数目不够，需再请求一页

                            for (currentPage = 2; currentPage <= page_count; currentPage++) {
                                if (result.size() < returnNum) {
                                    if (page_count >= currentPage) {


                                        currentParam.put("page", currentPage);
                                        String solrURLByPage = DataUtil.createHttpURL(ConfigUtil.getSolr_host(), makeSolrURI(currentParam, flag, baseURL));
                                        String solrRespStrByPage = null;
                                        try {
                                            solrRespStrByPage = HttpClientUtil.get(solrURLByPage);
                                        } catch (Exception e) {
                                            LogUtil.error("HttpClient error:" + e.getMessage());
                                        }


                                        LinkedHashMap<String, Object> solrRespByPage = JSON.parseObject(solrRespStrByPage, LinkedHashMap.class);
                                        List<Map<String, Object>> goodsListByPage = (List<Map<String, Object>>) solrRespByPage.get("goods_list");
                                        List<Object> addGoods = new ArrayList<Object>();
                                        for (int k = 0; k < goodsListByPage.size(); k++) {
                                            addGoods.add(goodsListByPage.get(k).get(returnKeys.get(1)));

                                        }
                                        result.addAll(addGoods);

                                    } else {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    } else {  //降序
                        for (currentPage = page_count; currentPage >= 1; currentPage--) {
                            if (result.size() < returnNum) {
                                if (page_count >= currentPage) {

                                    currentParam.put("page", currentPage);
                                    String solrURLByPage = DataUtil.createHttpURL(ConfigUtil.getSolr_host(), makeSolrURI(currentParam, flag, baseURL));
                                    String solrRespStrByPage = null;
                                    try {
                                        solrRespStrByPage = HttpClientUtil.get(solrURLByPage);
                                    } catch (Exception e) {
                                        LogUtil.error("HttpClient error");
                                    }


                                    LinkedHashMap<String, Object> solrRespByPage = JSON.parseObject(solrRespStrByPage, LinkedHashMap.class);
                                    List<Map<String, Object>> goodsListByPage = (List<Map<String, Object>>) solrRespByPage.get("goods_list");
                                    for (int k = goodsListByPage.size() - 1; k >= 0; k--) {
                                        Map<String, Object> one = goodsListByPage.get(k);
                                        result.add(one.get(returnKeys.get(1)));
                                    }

                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }

                    if (needFilter) {
                        Set<Object> set = new LinkedHashSet<Object>();
                        set.addAll(result);
                        result.clear();
                        result.addAll(set);
                    }

                    if (result.size() > returnNum) {
                        result = result.subList(0, returnNum);
                    }

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < result.size(); i++) {
                        sb.append(result.get(i));
                        sb.append(joinSign);
                    }
                    String resultByJoin = sb.toString();


                    return resultByJoin.substring(0, resultByJoin.length() - joinSign.length());

                }

            }

        } catch (Exception e) {
            LogUtil.error("HttpClient error:" + e.getMessage());
            return StringPool.EMPTY;
        }

    }

    public static String makeSolrURI(Map<String, Object> param, int flag, String baseURI) {
        StringBuffer solrURI = new StringBuffer();
        solrURI.append("/solr_api/json");
        if (flag == -1) {
            solrURI.append("/category-9999/");
        } else {
            solrURI.append(baseURI);
        }
        solrURI.append("mcat");
        appendURI(param, "mcat", solrURI, "0");
        solrURI.append("-scat");
        appendURI(param, "scat", solrURI, "0");
        solrURI.append("-b");
        appendURI(param, "b", solrURI, "0");
        solrURI.append("-max");
        appendURI(param, "max", solrURI, "0");
        solrURI.append("-min");
        appendURI(param, "min", solrURI, "0");
        solrURI.append("-attr-page-");
        appendURI(param, "page", solrURI, "1");
        solrURI.append("-sort-sort_order-order-asc");
        if (checkParam(param, "dt")) {  // 配送方式：0表示全部，1表示 美乐乐配送 2表示第三方配送
            solrURI.append("-dt");
            appendURI(param, "dt", solrURI, "0");
        }
        solrURI.append(".html");
        if (flag == -1) {
            solrURI.append("?keywords=");
            String keywords = String.valueOf(param.get("keywords"));
            param.put("keywords", URLEncoder.encode(keywords));
            appendURI(param, "keywords", solrURI, "0");
            solrURI.append("&fl=q");
            param.put("keywords",keywords);
        }
        return solrURI.toString();
    }


    public static boolean checkParam(Map<String, Object> param, String key) {
        if (param.get(key) == null) {
            return false;
        }
        return true;
    }

    public static StringBuffer appendURI(Map<String, Object> param, String key, StringBuffer solrURI, String defaultStr) {
        if (checkParam(param, key)) {
            solrURI.append(param.get(key));
        } else {
            solrURI.append(defaultStr);
        }
        return solrURI;
    }

}
