package me.springremoting.client;

import me.springremoting.rmi.IRmiPreviewService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:PreviewServiceTest-context.xml"})
public class RmiPreviewServiceTest {
	
	@Autowired
	private IRmiPreviewService rmiPreviewService;
	
	@Test
	public void getPreview(){
		System.out.println(rmiPreviewService.getPreview("{\"param\":{\"order_id\":\"2\"},\"previewCount\":10,\"previewType\":1,\"serviceID\":22430}"));
	}
}
