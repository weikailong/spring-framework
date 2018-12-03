package com.kaixin1928.aop;

import org.springframework.stereotype.Service;

/**
 * @description: ${description}
 * @author: weikailong on 2018-12-01 09:55
 **/
@Service
public class CustomerServiceImpl implements CustomerService {

	@Override
	public void doSomething1() {
		doSomething2();
		System.out.println("CustomerServiceImpl.doSomething1");
	}

	@Override
	public void doSomething2() {
		System.out.println("CustomerServiceImpl.doSomething2");
	}
}
