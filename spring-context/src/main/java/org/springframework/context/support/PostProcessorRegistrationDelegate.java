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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
class PostProcessorRegistrationDelegate {

	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		/**
		 * 	对于BeanDefinitionRegistry类型的处理器的处理主要包括以下内容:
		 * 		(1)	对于硬编码注册的后处理器的处理,主要是通过AbstractApplicationContext岁Be中的添加处理器方法addBeanFactoryPostProcessor
		 * 			进行添加.添加后的后处理器会存放在BeanFactoryPostProcessors中,而在处理BeanFactoryPostProcessor时候会首先检测beanFactoryPostProcessors
		 * 			是否有数据.当然,BeanDefinitionRegistryPostProcessor继承自BeanFactoryPostProcessor,不但有BeanFactoryPostProcessor的特性,同时还有自己
		 * 			定义的个性化方法,也需要在此调用.所以这里需要从beanFactoryPostProcessors中挑出BeanDefinitionRegistryPostProcessor的后处理器,并进行其
		 * 			postProcessBeanDefinitionRegistry方法的激活.
		 * 		
		 * 		(2)	记录后处理器主要使用了三个List完成.
		 * 				* registryPostProcessors:记录通过硬编码方式注册的BeanDefinitionRegistryPostProcessor类型的处理器
		 * 				* regularPostProcessors:记录通过硬编码方式注册的BeanFactoryPostProcessor类型的处理器.
		 * 				* currentRegistryProcessors:记录通过配置方式注册的BeanDefinitionRegistryPostProcessor类型的处理器
		 * 			
		 * 		(3)	对以上所记录的List中的后处理器进行统一调用BeanFactoryPostProcessor的postProcessBeanFactory方法.
		 * 		(4)	对BeanFactoryPostProcessors中非BeanDefinitionRegistryPostProcessor类型的后处理器进行统一的BeanFactoryPostProcessor的PostProcessBeanFactory方法调用.
		 * 		(5)	普通beanFactory处理
		 * 				BeanDefinitionRegistryPostProcessor只对BeanDefinitionRegistry类型的ConfigurableListableBeanFactory有效,所以如果判断所示的beanFactory
		 * 			并不是BeanDefinitionRegistry,那么便可以忽略BeanDefinitionRegistryPostProcessor,而直接处理BeanFactoryPostProcessor,当然获取的方式与上面的
		 * 			获取类似.
		 * 		
		 * 		这里需要提到的是,对于硬编码方式手动添加的后处理器是不需要做任何排序的,但是在配置文件中读取的处理器,Spring并不保证读取的顺序,
		 * 	所以,为了保证用户的调用顺序的要求,Spring对于后处理器的调用支持按照PriorityOrdered或者Ordered的顺序调用.
		 */
		
		
		Set<String> processedBeans = new HashSet<>();

		// 对BeanDefinitionRegistry类型的处理
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// 记录通过硬编码方式注册的BeanFactoryPostProcessor类型的处理器
			List<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList<>();
			// 记录通过硬编码方式注册的BeanDefinitionRegistryPostProcessor类型的处理器
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new LinkedList<>();

			// 硬编码注册的后处理器
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					// 对于BeanDefinitionRegistryPostProcessor类型,在BeanFactoryPostProcessor的基础上还有自己定义的方法,需要先调用
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					// 记录常规BeanFactoryPostProcessor
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// 记录通过配置方式注册的BeanDefinitionRegistryPostProcessor类型的处理器
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// 对于配置中读取的BeanFactoryPostProcessor的处理
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 按照优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 按照order排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 获取beanDefinitionMap中的Bean,即用户自定义的Bean
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		/**
		 * 	这里分出了三个List,表示开发者可以自定义BeanFactoryProcessor的调用顺序,具体调用顺序为:
		 * 		* 如果BeanFactoryPostProcessor实现了PriorityOrdered接口(PriorityOrderd接口是Ordered的子接口,没有自己的接口方法定义,只是做一个标记
		 * 		  表示调用优先级高于Ordered接口的子接口).是优先级最高的调用,调用顺序是按照接口方法getOrder()的实现,对于返回的int值从小到大进行排序,进行调用.
		 * 		* 如果BeanFactoryPostProcessor实现了Ordered接口,是优先级次高的调用,将在所有实现PriorityOrdered接口的BeanFactoryPostProcessor调用完毕之后,
		 * 	 	  依据getOrder()√先对返回的int值从小到大排序,进行调用
		 * 	 	* 不实现Ordered接口的BeanFactoryPostProcessor在上面的BeanFactoryPostProcessor调用完毕之后进行调用,调用顺序就是Bean定义的顺序.
		 */
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 按照上面的规则依次先将BeanFactoryProcessor接口对应的实现类实例化出来并调用postProcessBeanFactory方法
		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	/**
	 *  整体代码思路和invokeBeanFactoryPostProcessors方法类似,但是这里不会调用BeanPostProcessor接口的方法,
	 *  而是把每一个BeanPostProcessor接口实例化出来并按照顺序放入一个List中,到时候按顺序调用.
	 *  
	 *  具体代码思路可以参考invokeBeanFactoryPostProcessors,这里就根据代码总结一下BeanPostProcessor接口的调用顺序:
	 *  	* 优先调用PriorityOrdered接口的子接口,调用顺序依照接口方法getOrder的返回值从小到大的调用排序
	 *  	* 其次调用Ordered接口的子接口,调用顺序依照接口方法getOrder的返回值从小到大排序
	 *  	* 接着按照BeanPostProcessor实现类在配置文件中定义的顺序进行调用
	 *  	* 最后调用MergedBeanDefinitionPostProcessor接口的实现Bean,同样按照在配置文件中定义的顺序进行调用	
	 */
	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		/**
		 * 		首先我们会发现,对于BeanPostProcessor的处理与BeanFactoryPostProcessor的处理极为相似,但是似乎又有些不一样的地方.经过
		 * 	反复对比发现,对于BeanFactoryPostProcessor的处理要区分两种情况,一种方式是通过硬编码方式的处理,另一种是通过配置文件方式的
		 * 	处理.那么为什么在BeanPostProcessor的处理中只考虑了配置文件的方式而不考虑硬编码的方式呢?
		 * 		提出这个问题,还是因为读者没有完全理解两者实现的功能.对于BeanFactoryPostProcessor的处理,不但要实现注册功能,而且还要
		 * 	实现对后处理器的激活操作,所以需要载入配置中的定义,并进行激活;而对于BeanPostProcessor并不需要马上调用,再说,硬编码的方式
		 * 	实现的功能是将后处理器提取并调用,这里并不需要调用,当然不需要考虑硬编码的方式了,这里的功能只需要将配置文件的BeanPostProcessor
		 * 	提取出来并注册进入beanFactory就可以了.
		 * 		对于beanFactory的注册,也不是直接注册就可以的.在Spring中支持对于BeanPostProcessor的排序,比如根据PriorityOrdered进行
		 * 	排序,根据Ordered进行排序或者无须,而Spring在BeanPostProcessor的激活顺序的时候也会考虑对于顺序的问题而先进行排序.	
		 * 		p155
		 */
		
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		/**
		 * 	BeanPostProcessChecker是一个普通的信息打印,可能会有些情况,当Spring的配置中的后处理器
		 * 	还没有被注册就已经开始了bean的初始化时便会打印出BeanPostProcessorChecker中设定的信息
		 */
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// 使用PriorityOrdered保证顺序
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		// 使用Ordered保证顺序
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 无序BeanPostProcessor
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		/**
		 * 	在registerBeanPostProcessors方法的实现中确保了beanPostProcessor的唯一性.个人猜想,之所以
		 * 	选择在registerBeanPostProcessors中没有进行重复移除操作或许是为了保持分类的效果,使逻辑更清晰吧.
		 */
		// 第一步,注册所有实现PriorityOrdered的BeanPostProcessor
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// 第二步,注册所有实现Ordered的BeanPostProcessor
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// 第三步,注册所有无序的BeanPostProcessor
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// 第四步,注册所有MergedBeanDefinitionPostProcessor类型的BeanPostProcessor,并非重复注册.
		// 在beanFactory.addBeanPostProcessor中会先移除已经存在的BeanPostProcessor
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// 添加ApplicationListener探测器
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			// addBeanPostProcessor保证了postProcessor的唯一性
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
