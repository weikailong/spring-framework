/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * Allows for custom modification of an application context's bean definitions,
 * adapting the bean property values of the context's underlying bean factory.
 *
 * <p>Application contexts can auto-detect BeanFactoryPostProcessor beans in
 * their bean definitions and apply them before any other beans get created.
 *
 * <p>Useful for custom config files targeted at system administrators that
 * override bean properties configured in the application context.
 *
 * <p>See PropertyResourceConfigurer and its concrete implementations
 * for out-of-the-box solutions that address such configuration needs.
 *
 * <p>A BeanFactoryPostProcessor may interact with and modify bean
 * definitions, but never bean instances. Doing so may cause premature bean
 * instantiation, violating the container and causing unintended side-effects.
 * If bean instance interaction is required, consider implementing
 * {@link BeanPostProcessor} instead.
 *
 * @author Juergen Hoeller
 * @since 06.07.2003
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * 		BeanFactoryPostProcessor可以对bean的定义(配置元数据)进行处理.也就是说,Spring IoC容器允许BeanFactoryPostProcessor
	 * 	在容器实际实例化任何其他的bean之前读取配置元数据,并有可能修改它.如果你愿意,你可以配置多个BeanFactoryPostProcessor.
	 * 	你还能通过设置"order"属性来控制BeanFactory的执行次序(仅当BeanFactoryPostProcessor实现了Ordered接口时你才可以设置此
	 * 	属性,因此在实现BeanFactoryPostProcessor时,就应当考虑实现Ordered接口).
	 * 		如果你想改变实际的bean实例(例如从配置元数据创建的对象),那么你最好使用BeanPostProcessor.同样的,BeanFactoryPostProcessor
	 * 	的作用域范围是容器级的.它只和你所使用的容器有关.如果你在容器中定义一个BeanFactoryPostProcessor,它仅仅对此容器中的bean进行后置
	 * 	处理.BeanFactoryPostProcessor不会对定义在另一个容器中的bean进行后置处理,即使这两个容器都是在同一层次上.
	 * 		P147
	 * 		通过实现BeanFactoryPostProcessor接口,BeanFactory会在实例化任何bean之前获得配置信息,从而能够正确解析bean描述文件中的变量引用.	
	 */

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for overriding or adding
	 * properties even to eager-initializing beans.
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
