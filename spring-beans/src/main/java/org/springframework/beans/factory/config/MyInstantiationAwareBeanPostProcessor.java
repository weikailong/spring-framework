package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * @description: ${description}
 * @author: weikailong on 2018-11-28 14:28
 **/
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
	
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException{
		System.out.println("=======");
		return false;
	}
	
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("--------------");
		return null;
	}
	
}
