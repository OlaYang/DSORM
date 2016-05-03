package me.springremoting.client;

import me.springremoting.rmi.IRmiDataService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:DataServiceTest-context.xml"})
public class RmiDataServiceTest{

	@Autowired
	private IRmiDataService rmiDataService;

	@Test
	public void testGetDate() throws Exception {
//		String n="{\"serviceName\": \"拼接\",\"needAll\": \"1\"}";
		String n="{\"serviceName\": \"javajavajava\"}";
//		String t="{\"dbLang\":\"en\",\"format\":\"json\",\"needAll\":\"0\",\"order\":\"asc\",\"param\":{\"index_name\":\"java_city_channel\",\"index_schema_rule\":\"xml_schema_test\",\"thread_num\":\"1\"},\"serviceName\":\"solr_index_info\"}";
//		String name=rmiDataService.getData("{\"serviceName\": \"回收站测试lejj\",\"format\": \"json\",\"needAll\": \"1\",\"dbLang\": \"en\",\"param\": {\"user_id\": \"4062\"}}");
		System.out.println(rmiDataService.getData(n));
	}
}
