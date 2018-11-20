package com.kaixin1928.model;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-20 18:02
 **/
public class TestA {
	
	private TestB testB;
	
	public void a(){
		testB.b();
	}

	public TestB getTestB() {
		return testB;
	}

	public void setTestB(TestB testB) {
		this.testB = testB;
	}
}
