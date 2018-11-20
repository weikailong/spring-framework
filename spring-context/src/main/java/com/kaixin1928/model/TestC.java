package com.kaixin1928.model;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-20 18:02
 **/
public class TestC {
	
	private TestA testA;
	
	public void c(){
		testA.a();
	}

	public TestA getTestA() {
		return testA;
	}

	public void setTestA(TestA testA) {
		this.testA = testA;
	}
}
