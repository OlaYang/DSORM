package com.meiqi.openservice.commons.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.util.LogUtil;

/**
 * 
* @ClassName: SMSChannelConstants 
* @Description: TODO(短信模板返回信息提示) 
* @author fangqi
* @date 2015年9月25日 下午4:31:54 
*
 */
public class SMSCodeUtil {
    
    //助通
    private static Map<String,String> ZTCode = new HashMap<String,String>();
    //拓鹏
    private static Map<String,String> TPCode = new HashMap<String,String>();
    //创蓝
    private static Map<String,String> CLCode = new HashMap<String,String>();
    
    //好莱客
    private static Map<String,String> HLKCode = new HashMap<String,String>();
    static{
        //助通
        ZTCode.put("-1", "用户名或者密码不正确或用户禁用");
        ZTCode.put("1", "发送短信成功");
        ZTCode.put("0", "发送短信失败");
        ZTCode.put("2", "余额不够或扣费错误");
        ZTCode.put("3", "扣费失败异常（请联系客服）");
        ZTCode.put("5", "短信定时成功");
        ZTCode.put("6", "有效号码为空");
        ZTCode.put("7", "短信内容为空");
        ZTCode.put("8", "无签名，必须，格式：【签名】");
        ZTCode.put("9", "没有Url提交权限");
        ZTCode.put("10", "发送号码过多,最多支持200个号码");
        ZTCode.put("11", "产品ID异常或产品禁用");
        ZTCode.put("12", "参数异常");
        ZTCode.put("13", "12小时重复提交");
        ZTCode.put("14", "用户名或密码不正确，产品余额为0，禁止提交，联系客服");
        ZTCode.put("15", "Ip验证失败");
        ZTCode.put("19", "短信内容过长，最多支持500个");
        ZTCode.put("20", "定时时间不正确：格式：20130202120212(14位数字)");
       
        //拓鹏
        TPCode.put("-1", "提交接口错误");
        TPCode.put("-3", "用户名或密码错误");
        TPCode.put("-4", "短信内容和备案的模板不一样");
        TPCode.put("-5", "签名不正确（格式为：短信内容.....【签名内容】）签名一定要放在短信最后");
        TPCode.put("-7", "余额不足");
        TPCode.put("-8", "通道错误");
        TPCode.put("-9", "无效号码");
        TPCode.put("-10", "签名内容不符合长度");
        TPCode.put("-11", "用户有效期过期");
        TPCode.put("-12", "黑名单");
        TPCode.put("-13", "语音验证码的Amount参数必须是整形字符串");
        TPCode.put("-14", "语音验证码的内容只能为数字");
        TPCode.put("-15", "语音验证码的内容最长为6位");
        TPCode.put("-16", "余额请求过于频繁，5秒才能取余额一次");
        TPCode.put("-17", "非法IP");
        
        //创蓝
        CLCode.put("0", "提交成功");
        CLCode.put("101", "提交失败，无此用户");
        CLCode.put("102", "密码错误");
        CLCode.put("103", "提交过快（提交速度超过流速限制）");
        CLCode.put("104", "系统忙（因平台侧原因，暂时无法处理提交的短信）");
        CLCode.put("105", "敏感短信（短信内容包含敏感词）");
        CLCode.put("106", "消息长度错（>536或<=0）");
        CLCode.put("107", "包含错误的手机号码");
        CLCode.put("108", "手机号码个数错（群发>50000或<=0;单发>200或<=0）");
        CLCode.put("109", "无发送额度（该用户可用短信数已使用完）");
        CLCode.put("110", "不在发送时间内");
        CLCode.put("111", "超出该账户当月发送额度限制");
        CLCode.put("112", "无此产品，用户没有订购该产品");
        CLCode.put("113", "extno格式错（非数字或者长度不对）");
        CLCode.put("115", "自动审核驳回");
        CLCode.put("116", "签名不合法，未带签名（用户必须带签名的前提下）");
        CLCode.put("117", "IP地址认证错,请求调用的IP地址不是系统登记的IP地址");
        CLCode.put("118", "用户没有相应的发送权限");
        CLCode.put("119", "用户已过期");
        CLCode.put("120", "注册模板和发送模板不一致");
        
        //好莱客
        HLKCode.put("000", "发送成功！");
        HLKCode.put("-01", "当前账号余额不足！");
        HLKCode.put("-02", "当前用户ID错误！");
        HLKCode.put("-03", "当前密码错误！");
        HLKCode.put("-04", "参数不够或参数内容的类型错误！");
        HLKCode.put("-05", "手机号码格式不对！");
        HLKCode.put("-06", "短信内容编码不对！");
        HLKCode.put("-07", "短信内容含有敏感字符！");
        HLKCode.put("-08", "无接收数据");
        HLKCode.put("-09", "系统维护中..");
        HLKCode.put("-10", "手机号码数量超长！（100个/次 超100个请自行做循环）");
        HLKCode.put("-11", "短信内容超长！（70个字符）");
        HLKCode.put("-12", "其它错误");
        
    }
    
    /**
     * 
    * @Title: analysisSendResult 
    * @Description: TODO(匹配短信错误消息) 
    * @param @param flag
    * @param @param result
    * @param @return  参数说明 
    * @return BaseRespInfo    返回类型 
    * @throws
     */
    public static BaseRespInfo analysisSendResult(String flag, String result){
        BaseRespInfo respInfo = new BaseRespInfo();
        respInfo.setCode("1");
        respInfo.setDescription(result);
        try{
            if(flag.equals("2")){//拓鹏
                if(result.indexOf("xml")!=-1){
                    SAXReader reader = new SAXReader();
                    StringReader sr = new StringReader(result);
                    InputSource is = new InputSource(sr);
                    Document document = reader.read(is);
                    Element e = (Element) document.selectNodes("string").get(0);
                    String code = e.getText();
                    respInfo.setCode(code);
                    respInfo.setDescription(TPCode.get(code));
                    LogUtil.info("SMSCodeUtil_TP:拓鹏短信发送["+DataUtil.toJSONString(respInfo)+"]");
                }else{
                    respInfo.setDescription(result);
                    LogUtil.info("SMSCodeUtil_TP:拓鹏短信发送["+DataUtil.toJSONString(respInfo)+"]");
                }
            }
            if(flag.equals("1")){//助通
                String[] arr = result.split(",");
                if(arr.length>1){
                    respInfo.setCode(arr[0].equals("1")?"0":"1");
                    respInfo.setDescription("编号为==>"+arr[1]+" "+ZTCode.get(arr[0]));
                    LogUtil.info("SMSCodeUtil_ZT:助通短信发送["+respInfo.getDescription()+"]");
                }else{
                    respInfo.setCode("1");
                    respInfo.setDescription(ZTCode.get(arr[0]));
                    LogUtil.info("SMSCodeUtil_ZT:助通短信发送["+DataUtil.toJSONString(respInfo)+"]");
                }
            }
            if(flag.equals("3")){//创蓝
                String[] str = result.split("\n");
                String[] arr = str[0].split(",");
                respInfo.setCode(arr[1].equals("0")?"0":"1");
                respInfo.setDescription("请求时间==>"+arr[0]+","+CLCode.get(arr[1])+(arr[1].equals("0")?("，消息编号==>["+str[1]+"]"):""));
                LogUtil.info("SMSCodeUtil_CL:创蓝短信发送["+DataUtil.toJSONString(respInfo)+"]");
            }
            if(flag.equals("4")){//好莱客
                String str = result;
                respInfo.setCode("000".equals(str)?"0":"1");
                respInfo.setDescription("返回状态==>["+HLKCode.get(str)+"]");
                LogUtil.info("SMSCodeUtil_CL:好莱客短信发送["+DataUtil.toJSONString(respInfo)+"]");
            }
        } catch (DocumentException e) {
            respInfo.setCode("1");
            respInfo.setDescription("短信发送失败，["+e.getMessage()+"]");
            LogUtil.error("SMSCodeUtil_analysisSendResult_error:消息处理异常["+e.getMessage()+"]");
        }
        return respInfo;
    }
    
}
