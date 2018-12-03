package com.kaixin1928.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @description: ${description}
 * @author: weikailong on 2018-12-01 09:59
 **/
@Aspect
public class CustomerServiceInterceptor {

	
	@Before("execution(* com.kaixin1928..*.*(..))")
	public void doBefore(){
		System.out.println(" do something before ....... ");
	}
	
}
