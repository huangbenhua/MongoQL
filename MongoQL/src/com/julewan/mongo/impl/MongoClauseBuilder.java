/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.julewan.mongo.MongoType;
import com.julewan.mongo.ql.Builder;
import com.julewan.mongo.ql.OP;
import com.julewan.mongo.ql.Wrong;
import com.mongodb.BasicDBObject;

public class MongoClauseBuilder implements Builder{
	
	private static final ThreadLocal<Builder> builders = new ThreadLocal<Builder>(); 
	public static final Builder GetBuilderOf(Object ... args){
		Builder b = builders.get();
		if(b == null){
			b = new MongoClauseBuilder();
			builders.set(b);
		}
		b.initialize(args);
		return b;
	}
	
	private Object args[] = null;
	
	@Override
	public void initialize(Object... values) {
		args = values;
	}

	@Override
	public Object getArgument(int idx) { 
		return args[idx];
	}

	@Override
	public Object create(Object parent, boolean and) {
		BasicDBObject child = new BasicDBObject();
		((BasicDBObject)parent).append(and?"$and":"$or", child);
		return child;
	}

	@Override
	public void append(Object parent, OP op, String col, Object value) {
		if(value == null){
			throw new Wrong("Null value");
		}
		if(value instanceof Collection){
			append_collection((BasicDBObject)parent, op, col, value, false);
		}else if(value instanceof Iterator){
			append_collection((BasicDBObject)parent, op, col, value, true);
		}else{
			append_single((BasicDBObject)parent, op, col, value);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void append_collection(BasicDBObject parent, OP op, String col, Object value, boolean iter) {
		String oper = null;
		switch(op){
		case EQUAL:{//in
			oper = "$in";
		}break;
		case LESS_THAN://not
		case LESS_EQUAL://not
		case NOT_EQUAL:{
			//not in
			oper = "$nin";
		}break;
		case GREATE_THAN://all
		case GREATE_EQUAL:{ //TODO not all is nin?...
			oper = "$all";
		}break;
		case MOD_REG:{
			if(iter){
				append_mod(parent, false, col, (Iterator)value);
			}else{
				append_mod(parent, false, col, ((Collection)value).iterator());
			}
		}return;
		case NOT_MOD:{
			if(iter){
				append_mod(parent, true, col, (Iterator)value);
			}else{
				append_mod(parent, true, col, ((Collection)value).iterator());
			}
		}return;
		//
		case TYPE_AS:{
			if(iter){
				append_type(parent, false, col, (Iterator)value);
			}else{
				append_type(parent, false, col, ((Collection)value).iterator());
			}
		}return;
		case NOT_TYPE:{
			if(iter){
				append_type(parent, true, col, (Iterator)value);
			}else{
				append_type(parent, true, col, ((Collection)value).iterator());
			}
		}return;
		}
		//
		if(iter){
			List ls = new ArrayList();
			Iterator it = (Iterator)value;
			while(it.hasNext()){
				ls.add(it.next());
			}
			value = ls;
		}
		parent.append(col, new BasicDBObject(oper, value));
	}
	
	private BasicDBObject not_type_of(boolean not, Object value){
		BasicDBObject bo = null;
		if(value instanceof Number){
			bo = new BasicDBObject("$type", ((Number)value).intValue());
		}else{
			bo = new BasicDBObject("$type",  MongoType.of(value.toString()).getIndex());
		}
		if(not){
			return new BasicDBObject("$not", bo);
		}else{
			return bo;
		}
	}

	//这可只能是$type了
	@SuppressWarnings({ "rawtypes" })
	private void append_type(BasicDBObject parent, boolean not, String col, Iterator it){
		List<BasicDBObject> ls = new ArrayList<BasicDBObject>();//防止只有一个的情况产生
		while(it.hasNext()){
			ls.add(not_type_of(not, it.next()));
		}
		if(ls.isEmpty()){
			throw new Wrong("empty list");
		}
		if(ls.size() == 1){
			parent.append(col, ls.get(0));
			return;
		}
		BasicDBObject bo = new BasicDBObject();
		for(BasicDBObject o: ls){
			bo.append(col, o);
		};
		parent.append("$or", bo);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void append_mod(BasicDBObject parent, boolean not, String col, Iterator it){
		Object v1 = it.next();
		Object v2 = it.next();
		BasicDBObject bo = null;
		if(v1 instanceof Number){
			List ls = new ArrayList();
			ls.add(v1);
			ls.add(v2);
			bo = new BasicDBObject("$mod", ls);
		}else{
			bo = new BasicDBObject();
			bo.append("$regex", v1);
			bo.append("$options", v2);
		}
		if(not){
			bo = new BasicDBObject("$not", bo);
		}
		parent.append(col, bo);
	}
	
	private void append_single(BasicDBObject parent, OP op, String col, Object value) {
		switch(op){
		case EQUAL:
			parent.put(col, value);
			return;
		case NOT_EQUAL:
			parent.put(col, new BasicDBObject("$ne", value));
			break;
		case GREATE_THAN:
			parent.put(col, new BasicDBObject("$gt", value));
			break;
		case GREATE_EQUAL:
			parent.put(col, new BasicDBObject("$gte", value));
			break;
		case LESS_THAN:
			parent.put(col, new BasicDBObject("$lt", value));
			break;
		case LESS_EQUAL:
			parent.put(col, new BasicDBObject("$lte", value));
			break;
		case MOD_REG:{
			if(value instanceof Number){
				parent.put(col, new BasicDBObject("$size", value));
			}else{
				parent.put(col, new BasicDBObject("$regex", value));
			}
		}break;
		case NOT_MOD:{
			if(value instanceof Number){
				parent.put(col, new BasicDBObject("$not", new BasicDBObject("$size", value)));
			}else{
				parent.put(col, new BasicDBObject("$not", new BasicDBObject("$regex", value)));
			}
		}break;
		case TYPE_AS:{
			if(value instanceof Boolean){
				parent.put(col, new BasicDBObject("$exists", value));
			}else{
				parent.put(col, not_type_of(false, value));
			}
		}break;
		case NOT_TYPE:{
			if(value instanceof Boolean){
				parent.put(col, new BasicDBObject("$exists", !((Boolean) value)));
			}else{
				parent.put(col, not_type_of(true, value));
			}
		}break;
		}
	}
	
	
	
}
