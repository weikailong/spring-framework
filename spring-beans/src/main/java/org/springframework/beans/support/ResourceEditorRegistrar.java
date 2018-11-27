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

package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import org.xml.sax.InputSource;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.beans.propertyeditors.ReaderEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * PropertyEditorRegistrar implementation that populates a given
 * {@link org.springframework.beans.PropertyEditorRegistry}
 * (typically a {@link org.springframework.beans.BeanWrapper} used for bean
 * creation within an {@link org.springframework.context.ApplicationContext})
 * with resource editors. Used by
 * {@link org.springframework.context.support.AbstractApplicationContext}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 */
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {

	private final PropertyResolver propertyResolver;

	private final ResourceLoader resourceLoader;


	/**
	 * Create a new ResourceEditorRegistrar for the given {@link ResourceLoader}
	 * and {@link PropertyResolver}.
	 * @param resourceLoader the ResourceLoader (or ResourcePatternResolver)
	 * to create editors for (usually an ApplicationContext)
	 * @param propertyResolver the PropertyResolver (usually an Environment)
	 * @see org.springframework.core.env.Environment
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.context.ApplicationContext
	 */
	public ResourceEditorRegistrar(ResourceLoader resourceLoader, PropertyResolver propertyResolver) {
		this.resourceLoader = resourceLoader;
		this.propertyResolver = propertyResolver;
	}


	/**
	 * Populate the given {@code registry} with the following resource editors:
	 * ResourceEditor, InputStreamEditor, InputSourceEditor, FileEditor, URLEditor,
	 * URIEditor, ClassEditor, ClassArrayEditor.
	 * <p>If this registrar has been configured with a {@link ResourcePatternResolver},
	 * a ResourceArrayPropertyEditor will be registered as well.
	 * @see org.springframework.core.io.ResourceEditor
	 * @see org.springframework.beans.propertyeditors.InputStreamEditor
	 * @see org.springframework.beans.propertyeditors.InputSourceEditor
	 * @see org.springframework.beans.propertyeditors.FileEditor
	 * @see org.springframework.beans.propertyeditors.URLEditor
	 * @see org.springframework.beans.propertyeditors.URIEditor
	 * @see org.springframework.beans.propertyeditors.ClassEditor
	 * @see org.springframework.beans.propertyeditors.ClassArrayEditor
	 * @see org.springframework.core.io.support.ResourceArrayPropertyEditor
	 */
	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {

		/**
		 * 		注册了一系列的常用类型的属性编辑器,例如,代码doRegisterEditor(registry,Class.class,new ClassEditor(classLoader))实现的功能就是注册
		 * 	Class类对应的属性编辑器.那么注册后,一旦周哥实体bean中存在一些Class类型的属性,那么Spring会调用ClassEditor将配置文件中定义的String类型转换为
		 * 	Class类型并进行赋值.
		 * 		分析到这里不禁有个疑问,虽说ResourceEditorRegistrar类的registerCustomEditors方法实现了批量注册功能,但是
		 * 	beanFactory.addPropertyEditorRegistrar(this,getEnvironment())仅仅是注册了ResourceEditorRegistry实例,却并没有调用ResourceEditorRegistrar
		 * 	的registerCustomEditors方法进行注册,那么到底是在什么时候进行注册的呢?
		 * 		在Bean的初始化后会调用ResourceEditorRegistrar的registerCustomEditors方法进行批量的通用属性编辑器注册.注册后,在属性填充的环节便可以直接
		 * 	让Spring使用这些编辑器进行属性的解析了.
		 * 		Spring中用于封装bean的是BeanWrapper类型,而它又间接集成了propertyEditorRegistry类型,也就是我们之前反复看到的方法参数PropertyEditorRegistry registry
		 * 	其实大部分情况下都是BeanWrapper,对于BeanWrapper在Spring中的默认实现是BeanWrapperImpl,而BeanWrapperImpl出了实现BeanWrapper接口外还继承了PropertyEditorRegistrySupport,
		 * 	在PropertyEditorRegistrySupport中有这样一个方法:createDefaultEditors();通过这个方法我们可以知道Spring中定义了一系列常用的属性编辑器使我们可以方便的进行配置.
		 * 	如果我们定义的bean中的某个属性的类型不在上面的常用配置中的话,才需要我们进行个性化属性编辑器的注册.
		 * 		P143
		 */

		ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
		doRegisterEditor(registry, Resource.class, baseEditor);
		doRegisterEditor(registry, ContextResource.class, baseEditor);
		doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
		doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
		doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
		doRegisterEditor(registry, Path.class, new PathEditor(baseEditor));
		doRegisterEditor(registry, Reader.class, new ReaderEditor(baseEditor));
		doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));

		ClassLoader classLoader = this.resourceLoader.getClassLoader();
		doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
		doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
		doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));

		if (this.resourceLoader instanceof ResourcePatternResolver) {
			doRegisterEditor(registry, Resource[].class,
					new ResourceArrayPropertyEditor((ResourcePatternResolver) this.resourceLoader, this.propertyResolver));	
		}
	}

	/**
	 * Override default editor, if possible (since that's what we really mean to do here);
	 * otherwise register as a custom editor.
	 */
	private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
		if (registry instanceof PropertyEditorRegistrySupport) {
			((PropertyEditorRegistrySupport) registry).overrideDefaultEditor(requiredType, editor);
		}
		else {
			registry.registerCustomEditor(requiredType, editor);
		}
	}

}
