package com.meiqi.openservice.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.util.Tool;
import com.meiqi.util.LogUtil;

@Component
public class VerifyCodeAction extends BaseAction
{
    
    // 验证码图片的宽度。
    private int width = 60;
    
    // 验证码图片的高度。
    private int height = 20;
    
    // 验证码字符个数
    private int codeCount = 4;
    
    private int x = 0;
    
    // 字体高度
    private int fontHeight;
    
    private int codeY;
    
    char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S',
        'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    /**
     * 初始化验证图片属性
     */
    public void initxuan()
    {
        // 从web.xml中获取初始信息
        // 宽度
        String strWidth = "80";
        // 高度
        String strHeight = "30";
        // 字符个数
        String strCodeCount = "4";
        // 将配置的信息转换成数值
        try
        {
            if (StringUtils.isNotEmpty(strWidth))
            {
                width = Integer.parseInt(strWidth);
            }
            if (StringUtils.isNotEmpty(strHeight))
            {
                height = Integer.parseInt(strHeight);
            }
            if (StringUtils.isNotEmpty(strCodeCount))
            {
                codeCount = Integer.parseInt(strCodeCount);
            }
        }
        catch (NumberFormatException e)
        {
            LogUtil.error(e);
        }
        x = width / (codeCount + 1);
        fontHeight = height - 2;
        codeY = height - 4;
    }
    
    public void getVerifyCode(HttpServletRequest req, HttpServletResponse resp,RepInfo repInfo)
    {
        //LogUtil.info("getVerifyCode begin");
        String param = repInfo.getParam();
        Map<String, Object> paramMap = DataUtil.parse(param);
        String codeType = String.valueOf(paramMap.get(Constants.CODE_TYPE));
        initxuan();
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
        // 设置字体。
        g.setFont(font);
        // 画边框。
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        // 随机产生160条干扰线，使图象中的认证码不易被其它程序探测到。
        g.setColor(Color.BLACK);
        for (int i = 0; i < 10; i++)
        {
            int x = random.nextInt(width / 8);
            int y = random.nextInt(height / 8);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++)
        {
            // 得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            // 用随机产生的颜色将验证码绘制到图像中。
            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, (i + 1) * x, codeY);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        //LogUtil.info("set VerifyCode code:"+randomCode.toString()+",sessionId:"+ req.getSession().getId()+",codeType:"+codeType);
        req.getSession().setAttribute(Constants.NormalVerifyCodeType.get(codeType), randomCode.toString());
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/jpeg");
        // 将图像输出到Servlet输出流中。
        ServletOutputStream output = null;
        try
        {
            output = resp.getOutputStream();
            ImageIO.write(buffImg, "jpeg", output);
        }
        catch (IOException e)
        {
            LogUtil.error(e);
        }
        finally
        {
            try
            {
                if (output != null)
                {
                    output.close();
                }
            }
            catch (IOException e)
            {
                LogUtil.error(e);
            }
        }
    }
    
  public Object verifyNormalCode(HttpServletRequest request, HttpServletResponse resp,RepInfo repInfo){
        
        ResponseInfo respInfo=new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        String normalVerifyCode=map.get("normalVerifyCode");
        String codeType=map.get("codeType");
        //验证输入的注册码的正确性
        boolean r=Tool.verifyCode(request, normalVerifyCode,codeType,false);
        if(!r){
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
        }
        return respInfo;
    }
}
