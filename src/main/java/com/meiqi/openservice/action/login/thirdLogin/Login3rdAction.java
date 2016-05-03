package com.meiqi.openservice.action.login.thirdLogin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.action.login.LoginAction;
import com.meiqi.openservice.action.login.thirdLogin.impl.AccountService;
import com.meiqi.openservice.action.login.thirdLogin.impl.LoginWithQQActionImpl;
import com.meiqi.openservice.bean.LoginResponseInfo;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.bean.ThirdLoginInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.config.Constants.LOGIN_3RD;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.util.LogUtil;
import com.meiqi.wechat.tools.OpenWeChat;
import com.qq.connect.QQConnectException;

@Service
public class Login3rdAction extends LoginAction {
	@Autowired
	private OpenWeChat openWeChat;
	@Autowired
	private AccountService accountService;
	@Autowired
	private IMemcacheAction memcacheService;
	@Autowired
	private LoginWithQQActionImpl loginWithQQActionImpl;
	@Autowired
	private LoginAction loginAction;
	
	
	//移动端登陆
	public String appLogin(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		ResponseInfo respInfo = new ResponseInfo();
		String paramString = repInfo.getParam();
		Map<String, Object> paramMap = DataUtil.parse(paramString);
		
		if (!paramMap.containsKey("type")) {
			respInfo.setCode("1");
			respInfo.setDescription("登录失败，缺少必要参数type！");
			return JSON.toJSONString(respInfo);
		}
		if (!paramMap.containsKey("sourceFrom")) {
			respInfo.setCode("1");
			respInfo.setDescription("登录失败，缺少必要参数type！");
			return JSON.toJSONString(respInfo);
		}

		String type = String.valueOf(paramMap.get("type"));
		String sourceFrom = String.valueOf(paramMap.get("sourceFrom"));
		
		
		respInfo=accountService.IPAD_HSV1_wxLogin(paramMap,type,sourceFrom);
		if(paramMap.containsKey("from_user_id ")){ //有登录前id，进行购物车合并
			if(respInfo.getRows().size()>0){
				Map<String, String> map=respInfo.getRows().get(0);
				if(map.containsKey("user_id")){
					String userID=map.get("user_id");
					String from_user_id=map.get("from_user_id");
					String sessionId = request.getSession().getId();
					mergeCart(userID, sessionId, from_user_id, type, sourceFrom);
				}
			}
		}
		return JSON.toJSONString(respInfo);
	}
	
	//pc端登录
	@SuppressWarnings("unused")
	public String login(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo) {
		LoginResponseInfo loginResponseInfo = new LoginResponseInfo();
		ResponseInfo respInfo = new ResponseInfo();
		String result="";
		try {
			String param = repInfo.getParam();
			Map<String, Object> paramMap = DataUtil.parse(param);

			LogUtil.info("accept third login param="+param);
			// 以上获取登录的参数 三方code、state等
			// 获取登录类型，调用指定类型处理业务
			String loginType = String.valueOf(paramMap
					.get(Constants.LOGIN_TYPE));
			Map<String,String> userInfo=null;
			if (LOGIN_3RD.LOGIN_TYPE_WECHAT.equalsIgnoreCase(loginType)) {
				userInfo=wechatLogin(paramMap);
			} else if (LOGIN_3RD.LOGIN_TYPE_QQ.equalsIgnoreCase(loginType)) {
				userInfo=qqLogin(request, paramMap);
			}
			LogUtil.info("accept third login userInfo="+userInfo);
			if (null==userInfo||!userInfo.containsKey("user_id")) {
				respInfo.setCode("1");
				respInfo.setDescription("登录失败，无法得到授权！");
				respInfo.setObject("{\"code\": \"1\",\"login\": \"0\",\"description\":\"登录失败，无法得到授权！\"}");
            } else{
            	String type="1";
//            	String thirdState=(String) paramMap.get("state");
//            	if(thirdState.contains("aiuw")){
//            		type="2";
//            	}
            	//换成验证登录的用户信息，
            	String sessionId=userInfo.get("sessionId");
            	userInfo=keepCacheMsgByData(userInfo.get("user_name"));
            	userInfo.put("login", "1");
            	if(null==sessionId){
            		//传sessionid  -1进去，防止null错误
            		keepUserCookieSession(request, response, userInfo, type, "1","-1");
            	}else{
            		keepUserCookieSession(request, response, userInfo, type, "1",sessionId);
            	}
            	
            	String userId=userInfo.get("userID");
            	String userName=userInfo.get("userName");
            	JSONObject jsonObject=new JSONObject();
//                userInfo.put("code", "0");
//                userInfo.put("login", "1");
                respInfo.setCode("0");
                respInfo.setDescription("成功");
                Map<String,String> userMap=new HashMap<String, String>();
                userMap.put("uid", userId);
                userMap.put("userName", userName);
                respInfo.setObject(userMap);
            }
		} catch (Exception e) {
			e.printStackTrace();
			respInfo.setCode("1");
			String message=e.getMessage();
			respInfo.setDescription(message);
			respInfo.setObject("{\"code\": \"1\",\"login\": \"0\",\"description\":"+message+"}");
		}
		
        result = JSON.toJSONString(respInfo);
		return result;
	}

	/**
	 * qq登录业务处理
	 * 
	 * @param paramMap
	 * @throws QQConnectException
	 */
	public Map<String, String> qqLogin(HttpServletRequest request,
			Map<String, Object> paramMap) throws Exception {
		ThirdLoginInfo thirdLoginInfo = new ThirdLoginInfo();
		// 获取必要参数code state
		String code = paramMap.get("code").toString();
		thirdLoginInfo.setCode(code);
		String state = paramMap.get("state").toString();
		// 如果没有state参数那么设置空字符串，防止后续操作出现空指针操作
		if (null == state) {
			state = "";
		}
		String sessionId=null;
		// 检查是否有参数
		if (state.contains(":")) {
			// 切割参数 state=from:youjiagou_userid:443
			String[] states = state.split("_");
			// 检查切割后是否有参数
			if (0 < states.length) {
				for (String params : states) {
					String[] paramsArray = params.split(":");
					// 参数为键值对，因此切割后数组长度应该为2
					if (2 == paramsArray.length) {
						String paramsKey = paramsArray[0];
						String paramsValue = paramsArray[1];
						if ("from".equals(paramsKey)) {
							thirdLoginInfo.setFrom(paramsValue);
						} else if ("userid".equals(paramsKey)) {
							thirdLoginInfo.setUserId(Long
									.parseLong(paramsValue));
						}else if("sessionId".equals(paramsKey)){
							sessionId=paramsValue;
						}
					}
				}
			}
		}
		long userId = thirdLoginInfo.getUserId();
		// 调用qq sdk获取access
		request.setAttribute("code", code);
		JSONObject accessToken = loginWithQQActionImpl.getAccessToken(code,thirdLoginInfo.getFrom());
		// 必须取得授权access才进行业务
		if (null!=accessToken.getString("access_token")&&!"".equals(accessToken.getString("access_token"))) {
			String accessTokenString = accessToken.getString("access_token");
			// 得到openid
			JSONObject openJson=loginWithQQActionImpl.getOpenId(accessTokenString);
			String openID=openJson.getString("openid");
			// 调用公共三方登录帐号处理接口
			Map<String, String> userMap = accountService.loginUser(openID,
					LOGIN_3RD.LOGIN_TYPE_QQ, userId);
			if (null == userMap) {
				// 数据库不存在该qq用户
				// 获取qq用户信息 //不使用qq提供的sdk 因为qq sdk没有更新了，qq接口新增了城市获取，sdk无法获取城市
				JSONObject jsonUser = loginWithQQActionImpl.getQQUser(
						accessTokenString, openID,thirdLoginInfo.getFrom());
				if (thirdLoginInfo.getFrom().contains("aiuw")) {
					jsonUser.put("from", 1);// 设置来源aiuw
				}
				jsonUser.put("open_id", openID);
				// 调用统一添加用户方法，新增用户
				accountService.addUser(jsonUser, LOGIN_3RD.LOGIN_TYPE_QQ);
				// 添加后，再次调用登录帐号处理接口，登录用户
				userMap = accountService.loginUser(openID,
						LOGIN_3RD.LOGIN_TYPE_QQ, userId);
			}
			
			if(null!=sessionId){
				userMap.put("sessionId", sessionId);
			}
			return userMap;
		}
		return null;
	}

	/**
	 * 微信登录业务处理
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public Map<String, String> wechatLogin(Map<String, Object> paramMap)
			throws UnsupportedEncodingException {
		ThirdLoginInfo thirdLoginInfo = new ThirdLoginInfo();
		// 获取必要参数code state
		String redirect=null;
		String sessionId=null;
		String code = paramMap.get("code").toString();
		thirdLoginInfo.setCode(code);
		String state = paramMap.get("state").toString();
		// 如果没有state参数那么设置空字符串，防止后续操作出现空指针操作
		if (null == state) {
			state = "";
		}
		// 检查是否有参数
		if (state.contains(":")) {
			// 切割参数 state=from:youjiagou_userid:443
			String[] states = state.split("_");
			// 检查切割后是否有参数
			if (0 < states.length) {
				for (String params : states) {
					String[] paramsArray = params.split(":");
					// 参数为键值对，因此切割后数组长度应该为2
					if (2 == paramsArray.length) {
						String paramsKey = paramsArray[0];
						String paramsValue = paramsArray[1];
						if ("from".equals(paramsKey)) {
							thirdLoginInfo.setFrom(paramsValue);
						} else if ("userid".equals(paramsKey)) {
							thirdLoginInfo.setUserId(Long
									.parseLong(paramsValue));
						} else if("redirect".equals(paramsKey)){
							redirect=paramsValue;
						} else if("sessionId".equals(paramsKey)){
							sessionId=paramsValue;
						}
					}
				}
			}
		}
		long userId = thirdLoginInfo.getUserId();
		// 检测是否授权
		if (null != thirdLoginInfo.getCode()
				&& !"".equals(thirdLoginInfo.getCode())) {
			// 已经授权成功,获取code
			// 调用微信平台获取token和unionid
//			JSONObject accessTokenInfo=new JSONObject();
//			accessTokenInfo.put("access_token", "OezXcEiiBSKSxW0eoylIeNTtPUf-d3BL8baIrnXroo3seoAX1BizigHinkm37w9okriPlpha_4yyhFwBkODlFOnbNAXytB14kE5HCTSyKTRWM5n952dWpceB6VD05e2RfBBJ_RT2Y_q6oVciZxFYrg");
//			accessTokenInfo.put("expires_in", "7200");
//			accessTokenInfo.put("openid", "oMNJrwXcjgTf42OwdHchAn8N1eCs");
//			accessTokenInfo.put("unionid", "o4XC4t2s_JVZNUPythwY-gkzleXk");
			JSONObject accessTokenInfo = openWeChat
					.getOpenAccessToken(thirdLoginInfo.getCode(),thirdLoginInfo.getFrom());
			LogUtil.info("accept third login wechat accessTokenInfo="+accessTokenInfo.toJSONString());
			// 检测是否取到unionid
			if (null != accessTokenInfo
					&& accessTokenInfo.containsKey("unionid")) {
				String unionid = accessTokenInfo.getString("unionid");
				// 调用公共三方登录帐号处理接口
				Map<String, String> userMap = accountService.loginUser(unionid,
						LOGIN_3RD.LOGIN_TYPE_WECHAT, userId);
				// 三方登录null,表示不存在该用户
				if (null == userMap) {
					JSONObject jsonUser = openWeChat
							.getOpenUser(accessTokenInfo);
					LogUtil.info("accept third login wechat jsonUser="+jsonUser.toJSONString());
					if (thirdLoginInfo.getFrom().contains("aiuw")) {
						jsonUser.put("from", 1);// 设置来源aiuw
					}
					userMap=accountService.getUserByUserName(unionid);
					if(null==userMap){
						// 调用统一添加用户方法，新增用户
						accountService.addUser(jsonUser,
								LOGIN_3RD.LOGIN_TYPE_WECHAT);
					}else{
						accountService.updateUser(jsonUser, LOGIN_3RD.LOGIN_TYPE_WECHAT, userMap.get("user_id"));
					}
					// 添加后，再次调用登录帐号处理接口，登录用户
					userMap = accountService.loginUser(unionid,
							LOGIN_3RD.LOGIN_TYPE_WECHAT, userId);
				}
				if(null!=redirect){
					userMap.put("redirect", redirect);
				}
				if(null!=sessionId){
					userMap.put("sessionId", sessionId);
				}
				return userMap;
			}
		}
		return null;
	}

	//用户登录后sesiion cookie处理
	public String keepUserCookieSession(HttpServletRequest request,
			HttpServletResponse response, Map<String, String> userInfo,
			String loginFrom, String isAutoLogin,String sessionId) {
		String userId = userInfo.get("userID");
		String userName = userInfo.get("userName");
		String type = loginFrom;
		String key = "islogin_" + userId + "_" + type;
		long time = 0;
		try {
    		if ("0".equals(isAutoLogin)) {
    			// 是非自动登录，缓存时间设置为30分钟
    			time = 30 * 60 * 1000;
    			Cookie uid = new Cookie("uid", userId);
    			uid.setPath("/");
    			uid.setMaxAge(1800);// 半小时
    			Cookie name = new Cookie("userName",URLEncoder.encode(userName,"UTF-8"));
    	        name.setPath("/");
    			name.setMaxAge(1800);//半小时
    			response.addCookie(uid);
    			response.addCookie(name);
    		} else {
    			// 是自动登录，缓存时间设置为半年
    			time = 6 * 24 * 60 * 60 * 1000;
    			Cookie uid = new Cookie("uid", userId);
    			uid.setPath("/");
    			uid.setMaxAge(6 * 30 * 24 * 3600);
    			Cookie name = new Cookie("userName",URLEncoder.encode(userName,"UTF-8"));
                name.setPath("/");
                name.setMaxAge(6 * 30 * 24 * 3600);//半小时
                response.addCookie(uid);
                response.addCookie(name);
    		}
    		String isAutoLoginKey = "isAutologin_" + userId + "_" + type;
    		boolean r1 = memcacheService.putCache(isAutoLoginKey, isAutoLogin);
    		boolean r2 = memcacheService.putCache(key,
    				JSONObject.toJSONString(userInfo), time);
    
    		String sessionIdLoginKey = "login_" + sessionId;
    		boolean r3 = memcacheService.putCache(sessionIdLoginKey, userId + "_"
    				+ type);
    
    		HttpSession session = request.getSession();
    		session.setAttribute(key, userInfo);
    		if (!((r1 && r2 && r3) || session.getAttribute(key) != null)) {
    			// LogUtil.info("LoginAction login fail repInfo:" +
    			// repInfo.getParam());
    			return DsResponseCodeData.ERROR.code;
    		}
    
    		// 通过sessionId更新购物车的记录里面的userId
    		if (com.meiqi.openservice.commons.config.Constants.UserType.HEMEIJU_USER
    				.getIndex() == Integer.parseInt(type)) {
    			LogUtil.info("third login mergeCart:sessionId="+sessionId+",userId="+userId);
    			// 合并购物车
    			SetServiceResponseData actionResponse = mergeCart(userId, sessionId,"","1",Constants.SourceFrom.PC);
    
    			// actionResponse ==null (没有需要更新的cart);code=0 为更新成功
    			if (null == actionResponse
    					|| com.meiqi.app.common.config.Constants.SetResponseCode.SUCCESS
    							.equals(actionResponse.getCode())) {
    				return DsResponseCodeData.SUCCESS.code;
    			} else {
    				return DsResponseCodeData.ERROR.code;
    			}
    		}
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtil.error("Login3rdAction:"+e.getMessage());
        }
		return DsResponseCodeData.ERROR.code;
	}

	/**
	 * 从规则获取保存到缓存中的用户信息，方便前端验证用户登录
	 * @return
	 */
	private Map<String,String> keepCacheMsgByData(String userName){
		return loginAction.getUserInfo(userName, "1");
	}
	
	
}
