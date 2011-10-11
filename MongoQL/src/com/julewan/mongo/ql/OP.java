/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.ql;

public enum OP {
	AND("&&"),
	OR("||"),
	NOT("!"),
	
	EQUAL("="),
	NOT_EQUAL("!="),
	
	GREATE_THAN(">"),
	GREATE_EQUAL(">="),
	
	LESS_THAN("<"),
	LESS_EQUAL("<="),
	
	MOD_REG("%="),
	NOT_MOD("!%"),
	TYPE_AS("@="),
	NOT_TYPE("!@");
	private OP not = null;
	private String code=null;
	OP(String c){code = c;};
	public String getCode(){
		return code;
	}
	public OP getNOT(){
		return not;
	}
	private static void SET_NOT(OP a, OP b){
		a.not = b;b.not = a;
	}
	static{
		SET_NOT(AND, OR);
		SET_NOT(EQUAL, NOT_EQUAL);
		SET_NOT(GREATE_THAN,LESS_EQUAL);
		SET_NOT(GREATE_EQUAL,LESS_THAN);
		SET_NOT(MOD_REG,NOT_MOD);
		SET_NOT(TYPE_AS,NOT_TYPE);
	}
}
