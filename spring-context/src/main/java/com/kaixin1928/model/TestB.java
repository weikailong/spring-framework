package com.kaixin1928.model;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-20 18:02
 **/
public class TestB {
	
	private TestC testC;
	
	public void b(){
		testC.c();
	}

	public TestC getTestC() {
		return testC;
	}

	public void setTestC(TestC testC) {
		this.testC = testC;
	}
}
