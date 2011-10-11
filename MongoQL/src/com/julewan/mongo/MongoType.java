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

public enum MongoType {
	Double(1),
	Float(1),
	String(2),
	Object(3),
	Array(4),
	Binary(5),
	Bin(5),
	ObjectId(7),
	Id(7),
	Boolean(8),
	Bool(8),
	Date(9),
	Null(10),
	RegularExpression(11),
	Regex(11),
	JavaScript(13),
	JS(13),
	Symbol(14),
	JavaScriptScope(15),
	JSCOPE(15),
	Integer32(16),
	Int32(16),
	Timestampe(17),
	Datetime(17),
	Integer64(18),
	Int64(18),
	Minkey(255),
	Maxkey(127)
	;
	private int idx = 0;
	MongoType(int idx){
		this.idx = idx;
	}
	public int getIndex(){
		return idx;
	}
	private static Map<String, MongoType> types 
		= Collections.synchronizedMap(new HashMap<String, MongoType>());
	static{
		for(MongoType t:values()){
			types.put(t.name().toLowerCase(), t);
		}
	}
	public static MongoType of(String name){
		return types.get(name.toLowerCase());
	}
}
