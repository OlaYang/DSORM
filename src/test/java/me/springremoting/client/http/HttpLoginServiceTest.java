package me.springremoting.client.http;

import me.springremoting.http.IHttpLoginService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:HttpServiceTest-context.xml"})
public class HttpLoginServiceTest {
	
	@Autowired
	private IHttpLoginService remoteHttpLoginService;
	
	@Test
	public void testLogin(){
		System.out.println("--------------------------------------");
//		 String urlStr = "http://" + DataConfig.getProperty(DataConfig.Names.HOST);
//	        if (DataConfig.getProperty(DataConfig.Names.PORT) != null) {
//	            urlStr = urlStr + ":" + DataConfig.getProperty(DataConfig.Names.PORT);
//	        }
//	        urlStr = urlStr + uri;
//	        URL url = new URL(urlStr);
//	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//	        connection.setRequestMethod("POST");
//	        connection.setDoInput(true);
//	        connection.setDoOutput(true);
//	        connection.setConnectTimeout(connectimeout);
//	        connection.setReadTimeout(readTimeout);
//
//	        byte[] buff = new byte[1024];
//	        int count;
//	        while ((count = json.read(buff)) != -1) {
//	            connection.getOutputStream().write(buff, 0, count);
//	        }
//
//	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	        baos.reset();
//	        
//	        while ((count = connection.getInputStream().read(buff)) != -1) {
//	            baos.write(buff, 0, count);
//	        }
//
//	        String jsonFromData = new String(baos.toByteArray());
//	        baos.close();
		System.out.println(remoteHttpLoginService.login("{\"username\":\"rule_test\",\"password\":\"123456\"}"));
	}
}
