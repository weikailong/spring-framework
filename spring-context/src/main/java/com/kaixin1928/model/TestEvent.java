package com.kaixin1928.model;

import org.springframework.context.ApplicationEvent;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-28 16:25
 **/
public class TestEvent extends ApplicationEvent {

	public String msg;
	
	public TestEvent(Object source) {
		super(source);
	}
	public TestEvent(Object source, String msg){
		super(source);
		this.msg = msg;
	}
	public void print(){
		System.out.println(msg);
	}
}
