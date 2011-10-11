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
import com.julewan.mongo.ql.Wrong;

public class Val implements Exp{
	
	public Object value = null;
	private StringBuffer buffer = new StringBuffer();
	private boolean hasDot = false; 
	
	public Val(){};
	
	public Val(Lab lab){
		buffer = null;
		value = new Boolean("true".equals(lab.value));
	}
	
	public Val(Object obj){
		value = obj;
	}

	public boolean closed(){
		return buffer == null;
	}

	public static boolean accept(char c){
		return c >= '0' && c <= '9';
	}
		
	public boolean accept(Ctx c){
		if(closed())return false;
		if(Val.accept(c.getChar())){
			//yes
		}else if(c.getChar() == '.'){
			if(hasDot){
				throw new Wrong("error float");
			}
			hasDot = true;
		}else{
			if(hasDot){
				value = new Double(buffer.toString());
			}else{
				value = new Long(buffer.toString());
			}
			buffer = null;
			return false;
		}
		buffer.append(c.getChar());
		return true;
	}
	@Override
	public boolean accept(Exp e) {
		return false;
	}
	
	public String toString(){
		return value + "--\n";
	}
	@Override
	public Object build(Builder builder, Object object) {
		return this.value;
	}
	@Override
	public Exp closeWithNot(boolean not) {
		if(value == null){
			throw new Wrong("null value");
		}
		return this;
	}
	
}
