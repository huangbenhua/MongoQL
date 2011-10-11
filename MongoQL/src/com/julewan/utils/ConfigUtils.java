/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-2-20
 * 
**/
package com.julewan.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class ConfigUtils {
	private static final ClassLoader loader = ConfigUtils.class.getClassLoader();
	public static final Properties loadSystemConfig(String url){
		boolean isConf = url.endsWith(".conf");
		InputStream in = loader.getResourceAsStream(url);
		if(in == null)return null;
		Properties prop = new Properties();
		combine(isConf, in, prop);
		return prop;
	}
	
	public static final Properties loadFullSystemConfig(String url){
		Properties prop = loadSystemConfig(url);
		if(prop == null)return null;
		fullLoadConfig(prop);
		return prop;
	}
	
	public static final void fullLoadConfig(Properties conf){
		Set<Object> keys = new HashSet<Object>(conf.keySet());
		boolean reload = false;
		for(Object k:keys){
			String pkg = k.toString();
			if(pkg == null  || pkg.trim().equals(""))continue;
			if(pkg.equals("include")){
				pkg = "";
			}else if(pkg.startsWith("include.")){
				pkg = pkg.substring("include.".length()).replace('.', '/');
				if(!pkg.endsWith("/"))pkg += "/";
			}else continue;
			String ps[] = (conf.get(k) + "").split(",");
			conf.remove(k);
			if(ps.length < 1)continue;
			for(String p:ps){
				if(p==null)continue;
				p = p.trim();
				if(p.equals("") || p.equals("null"))continue;
				if(p.contains("\\") || p.contains("//")){//绝对地址
					try {
						FileInputStream fin = new FileInputStream(p);
						combine(!p.endsWith(".properties"), fin, conf);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						continue;
					}
				}else if(p.endsWith(".properties")){
					p = p.substring(0, p.lastIndexOf('.')).replace('.', '/') + ".properties";
					combine(loadSystemConfig(pkg + p), conf);
				}else if(p.endsWith(".conf")){
					p = p.substring(0, p.lastIndexOf('.')).replace('.', '/') + ".conf";
					combine(true, loader.getResourceAsStream(pkg + p), conf);
				}else{
//					try {
//						Class<?> c = Class.forName(pkg.replace('/', '.') + p);
//						@SuppressWarnings("rawtypes")
//						Map m = ContextLoader.getCurrentWebApplicationContext().getBeansOfType(c);
//						if(m == null || m.isEmpty())continue;
//						PropertiesLoader loader = (PropertiesLoader)m.values().iterator().next();
//						combine(loader.getProperties(), conf);
//					} catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
				}
				reload=true;
			}
		}
		if(reload)fullLoadConfig(conf);
	}
	
	public static final void combineConf(InputStream in, Properties to){
		try {
			combineConf(StreamUtils.readText(in), to);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final void combineConf(String conf, Properties to){
		try{
			String txt = new String(conf.getBytes("ISO-8859-1"));
			Properties lp = new Properties();
			lp.load(new ByteArrayInputStream(txt.getBytes()));
			//
			for(Object k:lp.keySet()){
				String v = new String(((String)lp.get(k)).getBytes("ISO-8859-1"), "UTF-8");
				to.put(k, v);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static final void combine(boolean isconf, InputStream in, Properties to){
		if(isconf){
			combineConf(in, to);
		}else{
			Properties lp = new Properties();
			try {lp.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			combine(lp, to);
		}
	}
	
	public static final void combine(Properties from, Properties to){
		for(Map.Entry<Object, Object> ent:from.entrySet()){
			to.put(ent.getKey(), ent.getValue());
		}
	}
	
}
