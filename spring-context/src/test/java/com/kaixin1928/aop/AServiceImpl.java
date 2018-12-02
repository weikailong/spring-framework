package com.kaixin1928.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @description: ${description}
 * @author: weikailong on 2018-12-01 09:20
 **/
@Service("aServiceImpl")
public class AServiceImpl implements AService {
	
//	@Resource(name="aServiceImpl")
//	private AService aService;
	
	@Transactional
	public void a() throws Exception {
		this.b();
//		aService.b();
		System.out.println("a执行了");
//		throw new Exception();
	}

	@Transactional
	public void b() throws Exception {
		System.out.println("b执行了");
//		throw new Exception();
	}

	public static void main(String[] args) throws Exception {
		AService aService = new AServiceImpl();
		aService.a();
		
	}
	
	
}
