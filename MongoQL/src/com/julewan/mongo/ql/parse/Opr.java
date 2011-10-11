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
import com.julewan.mongo.ql.OP;
import com.julewan.mongo.ql.Wrong;

public class Opr implements Exp{ 
	
	public OP value = null;
	private StringBuffer buffer = new StringBuffer();
	public Opr(char c){
		buffer.append(c);
	}
	@Override
	public boolean closed() {
		return buffer == null;
	}
	public static boolean accept(char c){
		for(OP t: OP.values()){
			if(t.getCode().charAt(0) == c){
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean accept(Ctx c) {
		if(closed())return false;
		String code = buffer.append(c.getChar()).toString();
		for(OP t: OP.values()){
			if(t.getCode().equals(code)){
				value = t;
				buffer = null;
				return true;
			}
		}
		for(OP t: OP.values()){
			if(t.getCode().length() == 1 && 
			   t.getCode().charAt(0) == c.getLast()){
				value = t;
				buffer = null;
				return false;
			}
		}
		//WRONG CODE
		throw new Wrong("invalid operator");
	}
	@Override
	public boolean accept(Exp e) {
		return false;
	}
	
	public String toString(){
		return value.getCode() + "\n";
	}
	@Override
	public Object build(Builder builder, Object object) {
		return null;
	}
	@Override
	public Exp closeWithNot(boolean not) {
		if(not){
			this.value = value.getNOT();
		}
		return this;
	}
	
}
