package com.kaixin1928.aop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @description: ${description}
 * @author: weikailong on 2018-12-01 10:09
 **/
public class CustomerServiceTest {

	@Autowired
	CustomerService customerService;
	
	@Test
	public void testAOP(){
		customerService.doSomething1();
	}
	
}
