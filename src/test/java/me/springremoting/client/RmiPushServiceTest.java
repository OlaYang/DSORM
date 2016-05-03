package me.springremoting.client;

import me.springremoting.rmi.IRmiPushService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class RmiPushServiceTest {
	@RunWith(SpringJUnit4ClassRunner.class)
	@ContextConfiguration(locations ={"classpath:DataServiceTest-context.xml"})
	public class RmiDataServiceTest{

		@Autowired
		private IRmiPushService rmiPushService;

		@Test
		public void testGetDate() throws Exception {
			rmiPushService.deleteService("");
		}
	}
}
