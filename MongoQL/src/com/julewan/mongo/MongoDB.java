/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo;

import java.util.Properties;

import com.julewan.mongo.impl.MongoLoader;
import com.julewan.utils.ConfigUtils;

public class MongoDB {
	//////////////////
	private MongoLoader mongoLoader = null;
	public MongoDB(Properties props){
		mongoLoader = new MongoLoader(props);
		if(!mongoLoader.isValid()){
			mongoLoader = null;
		}
	}
	
	private static final Properties _load_conf_(){
		Properties props = ConfigUtils.loadFullSystemConfig("mongo.conf");
		if(props != null)return props;
		return ConfigUtils.loadFullSystemConfig("mongo.properties");
	}
	
	public MongoDB(){
		this(_load_conf_());
	}

	//这是唯一可用的方法
	public Mongoes getMongoes(String name){
		return mongoLoader.getMongoes(name);
	}
	
	public boolean isValid(){
		return mongoLoader != null;
	}
	
}
