package me.springremoting.client;

import me.springremoting.rmi.IRmiUserListService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:UserServiceTest-context.xml"})
public class RmiUserListServiceTest {
	
	@Autowired
	private IRmiUserListService rmiUserListService;
	
	@Test
	public void getUserList(){
		System.out.println("--------------------------");
		String str = rmiUserListService.getUserList("{\"userName\":\"j\"}");
		System.out.println(str);
	}
}
