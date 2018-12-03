package com.kaixin1928.model;

import javax.ejb.Local;
import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @description: 国际化
 * @author: weikailong on 2018-11-28 15:34
 **/
public class LocaleModel {

	public static void main(String[] args) {

		// "国际化信息"也称为"本地化信息"
		
		// 1.带有语言和国家/地区信息的本地化对象
		Locale locale1 = new Locale("zh", "CN");

		// 2.只有语言信息的本地化对象
		Locale zh = new Locale("zh");
		
		// 3.等同于Locale("zh","CN")
		Locale china = Locale.CHINA;

		// 4.等同于Locale("zh")
		Locale chinese = Locale.CHINESE;

		// 5.获取本地系统默认的本地化对象
		Locale aDefault = Locale.getDefault();

		// 1.信息格式化
		String pattern1 = "{0},你好!你于{1}在工商银行存入{2}元.";
		String pattern2 = "At {1,time,short} On{1,date,long},{0}, paid {2,number,currency}.";
		
		// 2.用于动态替换占位符的参数
		Object[] params = {"John", new GregorianCalendar().getTime(), 1.0E3};
		
		// 3.使用默认本地化对象格式化信息
		String msg1 = MessageFormat.format(pattern1,params);
		
		// 4.使用指定的本地化对象格式化信息
		MessageFormat mf = new MessageFormat(pattern2, Locale.US);
		String msg2 = mf.format(params);
		System.out.println(msg1);
		System.out.println(msg2);

	}
	
	
	
}
