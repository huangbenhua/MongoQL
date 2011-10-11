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

public class Str implements Exp{
	
	public String value = null;
	private StringBuffer buffer = new StringBuffer(); 
	public boolean closed(){
		return buffer == null;
	}
	
	public static boolean accept(char c){
		return c == '\'';
	}
	
	public boolean accept(Ctx c){
		if(closed())return false;
		switch(c.getChar()){
			case '\'':{
				if(c.getLast() != '\\'){
					value = buffer.toString();
					buffer = null;
					return true;
				}
			}break;
			case '\\':{
				if(c.getLast() != '\\'){
					return true;
				}
			}break;
		}
		buffer.append(c.getChar());
		return true;
	}
	@Override
	public boolean accept(Exp e) {
		return false;
	}
	
	public String toString(){
		return "'" + value + "'\n";
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
