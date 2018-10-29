/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.springframework.aop.SpringProxy;

/**
 * Default {@link AopProxyFactory} implementation, creating either a CGLIB proxy
 * or a JDK dynamic proxy.
 *
 * <p>Creates a CGLIB proxy if one the following is true for a given
 * {@link AdvisedSupport} instance:
 * <ul>
 * <li>the {@code optimize} flag is set
 * <li>the {@code proxyTargetClass} flag is set
 * <li>no proxy interfaces have been specified
 * </ul>
 *
 * <p>In general, specify {@code proxyTargetClass} to enforce a CGLIB proxy,
 * or specify one or more interfaces to use a JDK dynamic proxy.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

	@Override
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {

		/**
		 * 	平时我们说的AOP原理三句话就能概括:
		 * 		1.对类生成代理使用CGLIB
		 * 		2.对接口生成代理使用JDK原生的Proxy
		 * 		3.可以通过配置文件指定对接口使用CGLIB生成代理
		 * 	
		 * 	这三句话的出处就是createAopProxy方法.默认是使用JDK自带的Proxy生成代理,碰到以下三种情况例外:
		 * 		1.ProxyConfig的isOptimize方法为true,这表示让Spring自己去优化而不是用户指定
		 * 		2.ProxyConfig的isProxyTargetClass方法为true,这表示配吹了proxy-target-class="true"
		 * 		3.ProxyConfig满足hasNoUserSuppliedProxyInterfaces方法执行结果为true,表示<bean>对象没有实现任何接口或者实现的借口是SpringProxy接口
		 * 	
		 * 	在进入if判断之后再根据目标<bean>的类型决定返回那种AopProxy,简单总结起来:
		 * 		1.proxy-target-class没有配置或者proxy-target-class="false",返回JdkDynamicAopProxy
		 * 		2.proxy-target-class="true"或者<bean>对象没有实现任何接口或者只实现了SpringProxy接口,返回Cglib2AopProxy.	
		 * 	
		 * 	当然,不管是JdkDynamicAopProxy还是Cglib2AopProxy, AdvisedSupport都是作为构造函数参数传入的,里面存储了具体的Advisor
		 * 	
		 */


		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			return new ObjenesisCglibAopProxy(config);
		}
		else {
			return new JdkDynamicAopProxy(config);
		}
	}

	/**
	 * Determine whether the supplied {@link AdvisedSupport} has only the
	 * {@link org.springframework.aop.SpringProxy} interface specified
	 * (or no proxy interfaces specified at all).
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
