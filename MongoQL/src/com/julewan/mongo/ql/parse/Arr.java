/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.ql.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.julewan.mongo.ql.Builder;
import com.julewan.mongo.ql.Wrong;

public class Arr implements Exp{
	
	public Stack<Exp> values = new Stack<Exp>();
	private boolean close = false;
	
	@Override
	public boolean closed(){
		return close;
	}
	
	public static boolean accept(char c){
		return c == '[';
	}
	
	@Override
	public boolean accept(Ctx c) {
		if(closed())return false;
		if(!values.isEmpty() && values.peek().accept(c)){
			return true;
		}
		if(c.getChar() == ','){
			return true;
		}
		if(c.getChar() == ']'){
			//rebuild array
			close = true; 
			return true;
		}
		c.pushExp();
		return true;
	}
	
	@Override
	public boolean accept(Exp e) {
		if(close)return false;
		//if(e instanceof Lab){
		//	e = new Num((Lab)e);
		//}
		values.push(e);
		return true;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer("[\n");
		for(Exp e:values){
			buffer.append(e);
		}
		return buffer.append("]\n").toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object build(Builder builder, Object object) {
		List<Object> ls = new ArrayList<Object>();
		for(Exp e:values){
			Object obj = e.build(builder, null);
			if(obj == null)continue;
			if(obj instanceof Collection){
				ls.addAll((Collection)obj);
			}else if(obj instanceof Iterator){
				Iterator it = (Iterator)obj;
				while(it.hasNext()){
					ls.add(it.next());
				}
			}else{
				ls.add(obj);
			}
		}
		return ls;
	}

	@Override
	public Exp closeWithNot(boolean not) { 
		 Stack<Exp> ls = new Stack<Exp>();
		 for(Exp e:values){
			 if(e instanceof Lab){
				 ls.add(new Val((Lab)e));
			 }else if(e instanceof Val){
				 ls.add(e);
			 }else if(e instanceof Arg){
				 ls.add(e);
			 }else if(e instanceof Str){
				 ls.add(e);
			 }else{
				 throw new Wrong("invalid array member:" + e);
			 }
		 }
		 values = ls;
		 return this;
	}
	
}
