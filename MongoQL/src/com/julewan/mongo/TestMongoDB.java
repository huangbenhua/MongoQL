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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class TestMongoDB {
	
	private void _load_props_(Properties props){
		props.put("mongo.url", "localhost:12345");
		props.put("q.*.testcol.testfind", "name=?");
		props.put("q.testdb.*.getmore", "name=? && age > ?");
	}
	
	private MongoDB mongoDB = null;
	private Mongoes mongoes = null;
	public TestMongoDB(){
		Properties props = new Properties();
		this._load_props_(props);
		mongoDB = new MongoDB(props);
		mongoes = mongoDB.getMongoes("testdb.testcol");
	}
	private void LOG(Object ...objects ){
		for(Object o:objects){
			if(o == null)continue;
			System.out.print(o.toString());
		}
		System.out.println();
	}
	public void testInsert(){
		DBObject obj = new BasicDBObject();
		obj.put("name", "haha");
		obj.put("age", 120);
		mongoes.insert(obj);
		DBCursor c = mongoes.find("testfind", "haha");
		if(!c.hasNext()){
			LOG("FAILED TO INSERT");
		}else{
			obj = c.next();
			LOG("INSERT:", "haha".equals(obj.get("name")));
		}
	}
	
	public void testUpdate(){
		
		/*
		DBObject obj = new BasicDBObject();
		obj.put("name", "haha");
		obj.put("age", 120);
		mongoes.insert(obj);
		*/
	}
	
	public void testFind(){
		
	}
	
	public void testRemove(){
		
	}
	
	public static final void main(String args[]){
		TestMongoDB tm = new TestMongoDB();
		tm.testInsert();
	}
	
}
