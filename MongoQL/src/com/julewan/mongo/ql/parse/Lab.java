/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.ql.parse;

import com.julewan.mongo.ql.Builder;

public class Lab implements Exp{
	
	public String value = null;
	private StringBuffer buffer = new StringBuffer();
	public boolean closed(){
		return buffer == null;
	}
	public boolean accept(Ctx c){
		return acceptMore(c.getChar());
	}
	
	public static boolean accept(char c){
		if(c >= 128 || c == '$' || c == '_' ||
			(c >= 'a' && c <= 'z') || 
			(c >= 'A' && c <= 'Z')
		){
			return true;
		}
		return false;
	}
	
	private boolean acceptMore(char c){
		if(closed())return false;
		if(accept(c)){
			buffer.append(c);
			return true;
		}
		if(buffer.length() > 0){
			if(c == '.' || Val.accept(c)){
				buffer.append(c);
				return true;
			}
		}
		//
		value = buffer.toString();
		buffer = null;
		return false;
	}
	@Override
	public boolean accept(Exp e) {
		return false;
	}
	
	public String toString(){
		return value + "\n";
	}
	@Override
	public Object build(Builder builder, Object object) {
		return this.value;
	}
	@Override
	public Exp closeWithNot(boolean not) {
		return this;
	}
	
}
