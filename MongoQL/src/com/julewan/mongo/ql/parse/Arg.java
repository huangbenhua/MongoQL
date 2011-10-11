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

public class Arg implements Exp{
	
	public int index;
	
	public Arg(int idx){
		index = idx;
	}
	
	public static boolean accept(char c){
		return c == '?';
	}

	@Override
	public boolean closed() {
		return true;
	}

	@Override
	public boolean accept(Ctx c) {
		return false;
	}

	@Override
	public boolean accept(Exp e) {
		return false;
	}
	
	public String toString(){
		return "?" + index + "\n";
	}

	@Override
	public Object build(Builder builder, Object object) {
		return builder.getArgument(index);
	}

	@Override
	public Exp closeWithNot(boolean not) {
		return this;
	}
	
}
