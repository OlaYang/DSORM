package me.springremoting.client;

import java.text.SimpleDateFormat;

import me.springremoting.rmi.RmiDateService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:DateServiceTest-context.xml"})
public class RmiDateServiceTest{

	@Autowired
	private RmiDateService rmiDateService;

	@Test
	public void testGetDate() throws Exception {
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rmiDateService.getDate()));
	}
}