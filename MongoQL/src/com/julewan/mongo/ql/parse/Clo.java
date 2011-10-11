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


public class Clo implements Exp{
	
	private boolean close = false;
	private boolean checkclose = false;
	public Clo(boolean chk){
		checkclose = chk;
	}
	
	public static boolean accept(char c){
		return c == '(';
	}

	@Override
	public boolean closed() {
		return close;
	}

	//public Stack<Exp> values = new Stack<Exp>();
	private Exp current = null;
	@Override
	public boolean accept(Ctx c) {
		if(closed())return false;
		if(current != null && current.accept(c)){
			buildFunc();
			return true;
		}
		buildFunc();
		if(c.getChar() == ')'){
			//end all;
			close = checkclose;
			if(close) return true;
			//throw new Wrong("error closure");
		}
		c.pushExp();
		return true;
	}
	
	@Override
	public boolean accept(Exp e) {
		if(close)return false;
		if(current == null){
			current = e;
		}else if(current.accept(e)){
			return true;
		}
		buildFunc();
		return true;
	}
	
	//try to build it
	private Func func = new Func();
	private void buildFunc(){
		if(current == null || !current.closed())return;
		//判断
		//System.out.println(current);
		if(!func.accept(current)){
			Func f = new Func();
			f.a = func;
			func = f;
			f.accept(current);
		};
		current = null;
	}
	
	public String toString(){
		/*
		StringBuffer buffer = new StringBuffer("(\n");
		for(Exp e:values){
			buffer.append(e.toString());
		}
		return buffer.append(")\n").toString();
		*/
		return func.toString();
	}

	@Override
	public Object build(Builder builder, Object object) {
		return func.build(builder, object);
	}

	@Override
	public Exp closeWithNot(boolean not) {
		func.closeWithNot(not);
		return func;
	}
	
}
