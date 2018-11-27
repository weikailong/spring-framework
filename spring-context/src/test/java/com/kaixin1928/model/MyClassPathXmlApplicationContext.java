package com.kaixin1928.model;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-27 11:38
 **/
public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {
	
	public MyClassPathXmlApplicationContext(String... configLocations){
		super(configLocations);
	}
	
	protected void initPropertySources(){
		// 添加验证要求
		getEnvironment().setRequiredProperties("VAR");
	}
	
}
