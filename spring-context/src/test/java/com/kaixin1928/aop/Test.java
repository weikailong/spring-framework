package com.kaixin1928.aop;

import javax.annotation.Resource;

/**
 * @description: ${description}
 * @author: weikailong on 2018-12-01 09:41
 **/
public class Test {
	@Resource(name="aServiceImpl")
	private AService aService;

	public static void main(String[] args) throws Exception {
		
		AService aService = new AServiceImpl();
		aService.a();


		AService aService1 = new AServiceImpl();
		
		
		
	}
	
	
	
	
	
	
}
