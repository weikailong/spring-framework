/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.transaction.annotation;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * Strategy implementation for parsing Spring's {@link Transactional} annotation.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
@SuppressWarnings("serial")
public class SpringTransactionAnnotationParser implements TransactionAnnotationParser, Serializable {

	@Override
	@Nullable
	public TransactionAttribute parseTransactionAnnotation(AnnotatedElement ae) {
		AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
				ae, Transactional.class, false, false);
		if (attributes != null) {
			return parseTransactionAnnotation(attributes);
		}
		else {
			return null;
		}
	}

	public TransactionAttribute parseTransactionAnnotation(Transactional ann) {
		return parseTransactionAnnotation(AnnotationUtils.getAnnotationAttributes(ann, false, false));
	}

	protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {

		/**
		 * 		此方法中实现了对对应类或者方法的事务属性解析,在这个中可以看到常用的或者不常用的的属性提取.
		 * 		至此,我们完成了事务标签的解析.我们司仪已经忘记了从哪里开始了.再回顾一下,我们现在的任务是找出某个增强器是否适合于对应
		 * 	的类,而是否匹配的关键则在于是否从指定的类或者类中的方法中找到对应的事务属性,现在我们以UserServiceImpl为例,已经在它的接口
		 * 	UserService中找到了事务属性,所以,它是与事务增强器匹配的,也就是它会被事务功能修饰.
		 * 		至此,事务功能的初始化工作边结束了,当判断某个bean使用与事务增强时,也就是适用于增强器BeanFactoryTransactionAttributeSourceAdvisor,
		 * 	没错,还是这个类,所以说,在自定义标签解析时,注入的类成为了整个事务功能的基础.
		 * 		BeanFactoryTransactionAttributeSourceAdvisor作为Advisor的实现类,自然要遵从Advisor的处理方式,当代理被调用时会调用
		 * 	这个类的增强方法,也就是此bean的Advise,又因为在解析事务定义标签时我们把TransactionInterceptor类型的bean注入到了BeanFactoryTransactionAttributeSourceAdvisor
		 * 	中,所以,在调用事务增强器增强的代理类时会首先执行TransactionInterceptor进行增强,同时,也就是在TransactionInterceptor类中
		 * 	的invoke方法中完成整个事务的逻辑.
		 * 		P268
		 */

		RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
		// 解析propagation
		Propagation propagation = attributes.getEnum("propagation");
		rbta.setPropagationBehavior(propagation.value());
		// 解析isolation
		Isolation isolation = attributes.getEnum("isolation");
		rbta.setIsolationLevel(isolation.value());
		// 解析timeout
		rbta.setTimeout(attributes.getNumber("timeout").intValue());
		// 解析readOnly
		rbta.setReadOnly(attributes.getBoolean("readOnly"));
		// 解析value
		rbta.setQualifier(attributes.getString("value"));
		ArrayList<RollbackRuleAttribute> rollBackRules = new ArrayList<>();
		// 解析rollbackFor
		Class<?>[] rbf = attributes.getClassArray("rollbackFor");
		for (Class<?> rbRule : rbf) {
			RollbackRuleAttribute rule = new RollbackRuleAttribute(rbRule);
			rollBackRules.add(rule);
		}
		// 解析rollbackForClassName
		String[] rbfc = attributes.getStringArray("rollbackForClassName");
		for (String rbRule : rbfc) {
			RollbackRuleAttribute rule = new RollbackRuleAttribute(rbRule);
			rollBackRules.add(rule);
		}
		// 解析noRollbackFor
		Class<?>[] nrbf = attributes.getClassArray("noRollbackFor");
		for (Class<?> rbRule : nrbf) {
			NoRollbackRuleAttribute rule = new NoRollbackRuleAttribute(rbRule);
			rollBackRules.add(rule);
		}
		// 解析noRollbackForClassName
		String[] nrbfc = attributes.getStringArray("noRollbackForClassName");
		for (String rbRule : nrbfc) {
			NoRollbackRuleAttribute rule = new NoRollbackRuleAttribute(rbRule);
			rollBackRules.add(rule);
		}
		rbta.getRollbackRules().addAll(rollBackRules);
		return rbta;
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || other instanceof SpringTransactionAnnotationParser);
	}

	@Override
	public int hashCode() {
		return SpringTransactionAnnotationParser.class.hashCode();
	}

}
