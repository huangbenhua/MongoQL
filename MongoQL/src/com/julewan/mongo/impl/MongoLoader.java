/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.julewan.mongo.Mongoes;
import com.julewan.mongo.ql.Statement;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class MongoLoader {
	//这是唯一可用的方法
	public Mongoes getMongoes(String name){
		if(name == null)return null;
		name = name.toLowerCase();
		Mongoes mongoes = mongoesMap.get(name);
		if(mongoes != null)return mongoes;
		return _load_mongoes_(name, null);
	}
	
	//////////////////
	private Mongo mongo = null;
	private Map<String, DB> mongoDBs = Collections.synchronizedMap(new HashMap<String, DB>());
	private Map<String, Mongoes> mongoesMap = Collections.synchronizedMap(new HashMap<String, Mongoes>());
	public MongoLoader(Properties props){
		if(props == null)return;
		try {
			//TODO 先不处理登陆和连接池等问题
			_load_mongo_(props.getProperty("mongo.url", "127.0.0.1"));
			//boolean auth = db.authenticate(myUserName, myPassword);
		} catch (Exception e) {
			e.printStackTrace();
			mongo = null;
		}
		if(mongo == null)return;
		load(props);
	}
	
	public boolean isValid(){
		return mongo != null;
	}
	
	private DB _get_DB_(String name){
		//防止错误
		name = name.replace(".", "").replaceAll("\\s", "");
		DB db = mongoDBs.get(name);
		if(db == null){
			db = mongo.getDB(name);
			mongoDBs.put(name, db);
		}
		return db;
	}
	private Map<String, Map<String, Statement>> global_db_statements 
				= Collections.synchronizedMap(new HashMap<String, Map<String, Statement>>());
	private Map<String, Map<String, Statement>> global_collection_statements 
				= Collections.synchronizedMap(new HashMap<String, Map<String, Statement>>());
	private void load(Properties props){
		//collection statements
		Map<String, Map<String, Statement>> local_statements  = new HashMap<String, Map<String, Statement>>();
		//load all statements
		Set<Object> keys = props.keySet();
		for(Object k:keys){
			String key = k.toString().replaceAll("\\s", "");
			if(!key.startsWith("q."))continue;
			key = key.substring("q.".length());
			int idx = key.indexOf("*.");
			if(idx < 0){
				//
				idx = key.lastIndexOf(".");
				if(idx < 1)continue;
				if(idx == key.indexOf("."))continue;
				//
				_create_statement_(local_statements, key, props.get(k).toString());
			}else{
				key = key.replace("*.", "");
				k = props.get(k);
				if(idx == 0){
					_create_statement_(global_collection_statements, key, k.toString());
				}else{
					_create_statement_(global_db_statements, key, k.toString());
				}
			}
		}
		//create default db collections
		for(String key:local_statements.keySet()){
			_load_mongoes_(key, local_statements.get(key));
		}
	}
	
	private void _create_statement_(Map<String, Map<String, Statement>> map, String key, String ql){
		int idx = key.lastIndexOf(".");
		String k = key.substring(0, idx);
		key = key.substring(idx + 1);
		//
		Map<String, Statement> m = map.get(k);
		if(m == null){
			m = Collections.synchronizedMap(new HashMap<String, Statement>());
			map.put(k, m);
		}
		m.put(key, new Statement(ql));
	}
	
	private void _load_mongo_(String url) throws Exception{
		url = url.replaceAll("\\s", "");
		int idx = url.indexOf(":");
		if(idx < 0){
			mongo = new Mongo(url);
		}else if(idx == 0){
			mongo = new Mongo("127.0.0.1", Integer.parseInt(url.substring(1)));
		}else{
			mongo = new Mongo(url.substring(0, idx), Integer.parseInt(url.substring(idx + 1)));
		}
	}

	private Mongoes _load_mongoes_(String name, Map<String, Statement> mymap){
		int idx = name.indexOf(".");
		if(idx < 1 || name.lastIndexOf(".") != idx){
			System.err.println("INVALID MONGO DB NAME:" + name);
			return null;
		}
		String dbname = name.substring(0, idx);
		String colname = name.substring(idx + 1);
		//
		Map<String, Statement> lmap, statements = new HashMap<String, Statement>();
		lmap = global_collection_statements.get(colname);
		if(lmap != null) statements.putAll(lmap);
		lmap = global_db_statements.get(dbname);
		if(lmap != null) statements.putAll(lmap);
		if(mymap != null) statements.putAll(mymap);
		//
		DB db = _get_DB_(dbname);
		DBCollection col = db.getCollection(colname);
		//
		Mongoes mg = new Mongoes(col, statements);
		//
		mongoesMap.put(name, mg);
		return mg;
	}
}
