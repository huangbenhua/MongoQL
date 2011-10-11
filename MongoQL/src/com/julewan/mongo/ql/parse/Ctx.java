/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.ql.parse;


public class Ctx {
	
	private char curr = 0, last = 0;
	private int idx = 0;
	private Exp root = null;
	public Ctx(String ql){
		ql = ql.trim() + " ";
		root = new Clo(false);
		for(int i = 0; i < ql.length(); ++ i){
			last = curr;
			curr = ql.charAt(i);
			root.accept(this);
		}
		//闭合
		root = root.closeWithNot(false);
	}
	
	public Exp getExpression(){
		return root;
	}
	
	private final boolean isBlank(){
		return curr <= ' ';
	}
	
	public void pushExp(){
		if(isBlank())return;
		Exp exp = null;
		if(Val.accept(curr)){
			exp = new Val();
			exp.accept(this);
		}else if(Lab.accept(curr)){
			exp = new Lab();
			exp.accept(this);
		}else if(Arg.accept(curr)){
			exp = new Arg(idx ++);
		}else if(Str.accept(curr)){
			exp = new Str();
		}else if(Opr.accept(curr)){
			exp = new Opr(curr);
		}else if(Arr.accept(curr)){
			exp = new Arr();
		}else if(Clo.accept(curr)){
			exp = new Clo(true);
		}else{
			exp = new Clo(false);
		}
		root.accept(exp);
	}

	public char getLast(){return last;}
	public char getChar(){return curr;}
	
	public String toString(){
		return root.toString();
	}
	
	public static final void main(String args[]){
		String ql = "a.b > ? || b < 12 && (f >= 'x\\'xx' || g.h @= [true, ?])";
		//String ql = "a && ( b || [1, 2, ?] )";
		//String ql = "a.b > ? || (b < 12 && c == f)";
		Ctx ctx = new Ctx(ql);
		System.out.println(ctx.getExpression());
	}
	
}
