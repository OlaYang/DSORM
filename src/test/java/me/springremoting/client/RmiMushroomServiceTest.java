package me.springremoting.client;

import me.springremoting.rmi.IRmiMushroomService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:DateServiceTest-context.xml"})
public class RmiMushroomServiceTest{

	@Autowired
	private IRmiMushroomService rmiMushroomService;

	@Test
	public void testGetDate() throws Exception {
//		String name=rmiMushroomService.start("{\"serviceName\": \"MUSH_Start\",\"param\": {\"serviceName\":\"sms_template\",\"transactionTimeout\": 10},\"format\": \"json\"}");
//		String name=rmiMushroomService.commit("{\"serviceName\":\"MUSH_Commit\",\"param\":{\"transactionNum\":\"T-C1C-1434176388252-9\"},\"format\":\"json\"}");
//		String name=rmiMushroomService.rollback("{\"serviceName\":\"Rollback\",\"param\":{\"transactionNum\":\"T-C1C-1434176388252-9\"},\"format\":\"json\"}");
		String json="{\"serviceName\": \"MUSH_Offer\",\"param\": {\"actions\": [{\"type\": \"C\",\"serviceName\": \"t_mushroom_service\",\"set\": {\"name\": \"java_test\",\"desc\": \"java_test\",\"scope\":\"CUD\"},\"where\": {\"prepend\": \"and\",\"conditions\": [{\"key\": \"sid\",\"value\": \"711\",\"op\": \"=\"}]}}],\"transaction\": 1},\"format\": \"json\"}";
		String name=rmiMushroomService.offer(json);
		System.out.println(name);
	}
	
	
}
