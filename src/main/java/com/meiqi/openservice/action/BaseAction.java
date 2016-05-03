package com.meiqi.openservice.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.EncodeAndDecodeUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.service.ShopConfigService;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;

public abstract class BaseAction {
    public static final Logger         LOG = Logger.getLogger(BaseAction.class);

    private static Map<String, String> shopConfigMap;

    @Autowired
    private ShopConfigService          shopConfigService;



    public Object execute(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        Method m;
        try {
            m = this.getClass().getDeclaredMethod(repInfo.getMethod(), HttpServletRequest.class,
                    HttpServletResponse.class, RepInfo.class);
            try {
                Object forward = m.invoke(this, request, response, repInfo);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    };



    public Map<String, String> getShopConfigMap() {
        return shopConfigMap;
    }



    public void setShopConfigMap(Map<String, String> shopConfigMap) {
        this.shopConfigMap = shopConfigMap;
    }



    /**
     * 
     * @Title: setShopConfig
     * @Description:
     * @param
     * @return void
     * @throws
     */
    public void setShopConfig() {
        shopConfigMap = shopConfigService.getAppShopConfig();
    }



    /**
     * 
     * @Title: getShopConfig
     * @Description:获取配置
     * @param @param key
     * @param @return
     * @return String
     * @throws
     */
    public String getShopConfig(String key) {
        if (CollectionsUtils.isNull(shopConfigMap)) {
            setShopConfig();
        }
        return shopConfigMap.get(key);
    }

    public static String basePath = "/usr";
    static {
        String classPath = null;
        String rootPath = "";
        // windows下
        if ("\\".equals(File.separator)) {
            classPath = BaseAction.class.getClassLoader().getResource("/").getPath();
            rootPath = classPath.substring(1, classPath.length() - 1);
        }
        // linux下
        if ("/".equals(File.separator)) {
            rootPath = "/usr";
        }
        basePath = rootPath;
    }



    public static Map<String, Object> getHeader(HttpServletRequest request) {
        Map<String, Object> header = new HashMap<String, Object>();
        String auth = request.getHeader(ContentUtils.AUTHORIZATION);
        if (StringUtils.isBlank(auth)) {
            auth = "";
        }
        header.put(ContentUtils.AUTHORIZATION, request.getHeader(ContentUtils.AUTHORIZATION));
        header.put("platInt", getPlatInt(request));
        header.put("plat", getPlatString(request));
        header.put("deviceId", request.getHeader("deviceId"));
        header.put("User-Agent", request.getHeader("User-Agent"));
        header.put("userRole", request.getHeader("userRole"));
        header.put("userId", validationAuthorization(request));
        return header;
    }



    /**
     * 
     * @Title: getPlat
     * @Description:获取请求来源
     * @param @param request
     * @param @return
     * @return int
     * @throws
     */
    public static int getPlatInt(HttpServletRequest request) {
        int plat = 1;
        String userAgent = request.getHeader(ContentUtils.REQUEST_HEADER_USER_AGENT);
        if (!StringUtils.isBlank(userAgent)) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.indexOf(ContentUtils.PLAT_ANDROID) != -1) {
                plat = 1;
            } else if (userAgent.indexOf(ContentUtils.PLAT_IPHONE) != -1) {
                plat = 2;
            } else if (userAgent.indexOf(ContentUtils.PLAT_IPAD) != -1) {
                plat = 3;
            }
        }

        return plat;
    }



    /**
     * 
     * @Title: getPlatString
     * @Description:获取请求来源
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public static String getPlatString(HttpServletRequest request) {
        String plat = ContentUtils.PLAT_ANDROID;
        String userAgent = request.getHeader(ContentUtils.REQUEST_HEADER_USER_AGENT);
        if (!StringUtils.isBlank(userAgent)) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.indexOf(ContentUtils.PLAT_ANDROID) != -1) {
                plat = ContentUtils.PLAT_ANDROID;
            } else if (userAgent.indexOf(ContentUtils.PLAT_IPHONE) != -1) {
                plat = ContentUtils.PLAT_IPHONE;
            } else if (userAgent.indexOf(ContentUtils.PLAT_IPAD) != -1) {
                plat = ContentUtils.PLAT_IPAD;
            }
        }
        return plat;
    }



    /**
     * 
     * @Title: validationAuthorization
     * @Description:验证请求授权信息
     * @param @param request
     * @param @return
     * @return String[]
     * @throws
     */
    public static long validationAuthorization(HttpServletRequest request) {
        try {
            // 获取授权证书
            String authorization = request.getHeader(ContentUtils.AUTHORIZATION);
            if (!StringUtils.isBlank(authorization)) {
                authorization = EncodeAndDecodeUtils.decodeStrBase64(authorization);
                String[] authorizationArray = authorization.split(ContentUtils.UNDERLINE);
                if (!CollectionsUtils.isNull(authorizationArray) && authorizationArray.length == 3) {
                    return Long.parseLong(authorizationArray[1]);
                }
            }
        } catch (Exception e) {
            LOG.error("请求授权验证失败！");
            return 0;
        }
        return 0;
    }



    /**
     * 
     * @Title: getIp
     * @Description:获取访问者IP 
     *                      在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效
     *                      。
     *                      本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP
     *                      (用,分割)， 如果还不存在则调用Request .getRemoteAddr()。
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader(ContentUtils.X_REAL_IP);
        if (!StringUtils.isBlank(ip) && !ContentUtils.UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader(ContentUtils.X_FORWARDED_FOR);
        if (!StringUtils.isBlank(ip) && !ContentUtils.UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(ContentUtils.COMMA);
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }



    /**
     * 
     * 直接使用HttpServletResponse write结果 并设置ContentType
     *
     * @param response
     * @param json
     */
    public static void putJsonToResponse(HttpServletResponse response, String json) {
        if (StringUtils.isBlank(json)) {
            return;
        }
        PrintWriter out = null;
        response.setContentType(ContentUtils.CONTENTTYPE_JSON);
        try {
            out = response.getWriter();
            out.write(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }



    /**
     * 
     * @Title: isFromApp
     * @Description:判断请求来源是不是ipad
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public static boolean isFromApp(HttpServletRequest request) {
        boolean isFromApp = false;
        String userAgent = request.getHeader(ContentUtils.REQUEST_HEADER_USER_AGENT);
        if (!StringUtils.isBlank(userAgent)) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.indexOf(ContentUtils.PLAT_ANDROID) != -1) {
                isFromApp = true;
            } else if (userAgent.indexOf(ContentUtils.PLAT_IPHONE) != -1) {
                isFromApp = true;
            } else if (userAgent.indexOf(ContentUtils.PLAT_IPAD) != -1) {
                isFromApp = true;
            }
        }

        return isFromApp;
    }
    
    public static Object appResultWraper(Object respObj) {
        if (!(respObj instanceof ResponseInfo)) {
            return respObj;
        }

        ResponseInfo respInfo = (ResponseInfo) respObj;
        JSONObject json = (JSONObject) JSONObject.toJSON(respInfo);
        if (json.containsKey("object")) {
            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            jsonArray.add(json.get("object"));
            json.put("rows", jsonArray);
            json.remove("object");
        }
        return json.toJSONString();
    }
}
