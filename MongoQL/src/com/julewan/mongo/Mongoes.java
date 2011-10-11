/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.julewan.mongo.impl.MongoClauseBuilder;
import com.julewan.mongo.ql.Builder;
import com.julewan.mongo.ql.Statement;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class Mongoes {
	private DBCollection collection;
	private Map<String, Statement> statements = 
		Collections.synchronizedMap(new HashMap<String, Statement>());
	//
	public Mongoes(DBCollection collection, Map<String, Statement> statements){
		this.collection = collection;
		this.statements.putAll(statements);
	}

	public DBCollection getCollection(){
		return collection;
	}
	
	public void setClause(String key, String ql){
		statements.put(key, new Statement(ql));
	}
	
	//
	public DBObject getClause(String key, Object ... args){
		BasicDBObject bo = new BasicDBObject();
		buildClause(bo, key, args);
		return bo;
	}
	
	//用以拼合条件
	public void buildClause(DBObject parent, String key, Object ... args){
		Builder b = MongoClauseBuilder.GetBuilderOf(args);
		Statement st = statements.get(key); 
		st.build(b, parent);
	}
	
	public BasicDBObject buildClause(BasicDBObject parent, boolean and){
		BasicDBObject clause = new BasicDBObject();
		parent.append(and?"$and":"$or", clause);
		return clause;
	}
	
	public DBObject get(Object id){
		if(id == null)return collection.findOne();
		return collection.findOne(id);
	}
	
	//find
	public DBCursor find(DBObject clause){
		if(clause == null)return collection.find();
		return collection.find(clause);
	}
	
	//find
	public DBCursor find(String key, Object ... args){
		return find(getClause(key, args));
	}
	
	//count
	public long count(DBObject clause){
		if(clause == null)return collection.count();
		return collection.count(clause);
	}
	
	//////
	public boolean save(DBObject object){
		return _result_(collection.save(object));
	}

	public boolean insert(DBObject ... objects){
		return _result_(collection.insert(objects));
	}
	
	public boolean remove(DBObject object){
		return _result_(collection.remove(object));
	} 
	
	public boolean update(DBObject object){
		DBObject q = new BasicDBObject();
		q.put("_id", object.get("_id"));
		return _result_(collection.update(q, object, true, false));
	}
	

	private boolean _result_(WriteResult rt){
		if(rt.getError() == null)return true;
		//TODO LOG ERROR INFO HERE
		return false;
	}

	
}
