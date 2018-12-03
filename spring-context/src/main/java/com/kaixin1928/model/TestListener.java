package com.kaixin1928.model;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-28 16:27
 **/
public class TestListener implements ApplicationListener {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof TestEvent){
			TestEvent testEvent = (TestEvent) event;
			testEvent.print();
		}
	}

}
