package me.springremoting.client;

import me.springremoting.rmi.IRmiLoginService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:LoginServiceTest-context.xml"})
public class RmiLoginServiceTest {
	
	@Autowired
	private IRmiLoginService rmiLoginService;
	
	@Test
	public void testLogin(){
		System.out.println(rmiLoginService.login("{\"username\":\"rule_test\",\"password\":\"123456\"}"));
	}
}
