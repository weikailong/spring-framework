/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.framework.autoproxy;

import com.kaixin1928.model.MyClassPathXmlApplicationContext;
import com.kaixin1928.model.MyTestBean;
import com.kaixin1928.model.TestEvent;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

public class MySpringTests {
	// TODO 隗凯隆
	@Test
	public void testSpring(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring/spring.xml");

		Map<String,String> systemPropertiesBean = (Map<String, String>) ac.getBean("systemProperties");
		for (Map.Entry<String, String> entry : systemPropertiesBean.entrySet()) {
			System.out.println(entry.getKey() + "--->" + entry.getValue());
		}
		System.out.println("=============================================");
		Map<String,String> systemEnvironmentBean = (Map<String, String>) ac.getBean("systemEnvironment");
		for (Map.Entry<String, String> entry : systemEnvironmentBean.entrySet()) {
			System.out.println(entry.getKey() + "--->" + entry.getValue());
		}
		
	}
	
	@Test
	public void testSimpleLoad(){
		
		BeanFactory bf = new XmlBeanFactory(new ClassPathResource("spring/spring.xml"));
		MyTestBean myTestBean = (MyTestBean) bf.getBean("myTestBean");
		assertEquals("testStr", myTestBean.getTestStr());

		ApplicationContext ac = new ClassPathXmlApplicationContext("spring/spring.xml");

	}

	@Test
	public void testSpring2(){
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring/spring.xml");
		System.out.println(ac.getBean("myTestBean"));

		System.out.println("~~~~~~~~~~~~~~~~~~~~~");
		BeanFactory bf = new XmlBeanFactory(new ClassPathResource("spring/spring.xml"));
		MyTestBean myTestBean = (MyTestBean) bf.getBean("myTestBean");
		System.out.println(myTestBean);
	}

	@Test
	public void testSpring3(){

		ApplicationContext ac = new MyClassPathXmlApplicationContext("spring/spring.xml");
		System.out.println(ac.getBean("testA"));

	}

	@Test
	public void testSpring4(){

		ApplicationContext ac = new ClassPathXmlApplicationContext("spring/spring.xml");
		TestEvent event = new TestEvent("hello", "msg");
		ac.publishEvent(event);

	}

	private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);
	
	@Test
	public void testSpring5(){

		String name = "A";
		String alias = "B";
		
		aliasMap.put("A","C");
		aliasMap.put("C","B");

		checkForAliasCircle(name, alias);
		
		this.aliasMap.put(name, name);

		System.out.println(aliasMap);
	}

	protected void checkForAliasCircle(String name, String alias) {
		boolean b = hasAlias(alias, name);
		System.out.println("校验结果:"+b);
		if (b) {
			throw new IllegalStateException("Cannot register alias '" + alias +
					"' for name '" + name + "': Circular reference - '" +
					name + "' is a direct or indirect alias for '" + alias + "' already");
		}
	}

	public boolean hasAlias(String name, String alias) {
		for (Map.Entry<String, String> entry : this.aliasMap.entrySet()) {
			String registeredName = entry.getValue();
			if (registeredName.equals(name)) {
				String registeredAlias = entry.getKey();
				return (registeredAlias.equals(alias) || hasAlias(registeredAlias, alias));
			}
		}
		return false;
	}
	

}
