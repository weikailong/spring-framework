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

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

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

}
