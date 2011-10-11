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
import java.util.List;

import com.julewan.mongo.MongoType;
import com.julewan.mongo.ql.Builder;
import com.julewan.mongo.ql.OP;
import com.julewan.mongo.ql.Wrong;

public class Func implements Exp{
	
	public Opr op = null;
	public Exp a = null, b = null;
	
	private boolean close = false;
	
	@Override
	public boolean closed(){
		return close;
	}

	@Override
	public boolean accept(Ctx c){
		return false;
	}

	@Override
	public boolean accept(Exp e){
		if(close){
			if(b == null)return false;
			if(b.accept(e))return true;
			if(e instanceof Opr){
				switch(op.value){
				case AND: case OR:
					break;
				default:
					return false;
				}
				switch(((Opr)e).value){
				case AND: case OR:case NOT:
					return false;
				}
				Func f = new Func();
				f.a = b;
				b = f;
				b.accept(e);
				return true;
			}
			return false;
		}
		if(e instanceof Opr){
			if(op != null){
				throw new Wrong("dup op");
			};
			op = (Opr)e;
			//目前单目操作只能出现在not上
			if(op.value == OP.NOT){
				if(a != null){
					throw new Wrong("need no single op");
				}
				return true;
			}else{
				if(a == null){
					throw new Wrong(op.value.getCode() + " is not single op");
				}
			}
			return true;
		}
		if(op == null){
			if(a != null){
				throw new Wrong("no op");
			}
			a = e;
			return true;
		}else if(op.value == OP.NOT){
			if(a != null){
				throw new Wrong("has end");
			}else if(a instanceof Func){
				a = e;
				close = true;
				return true;
			}else{
				throw new Wrong("error not format");
			}
		}else{
			if(a == null){
				a = e;
			}else{
				b = e;
				close = true;
			}
			return true;
		}
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(op.value).append("(\n").append(a);
		if(b != null)buf.append(b);
		return buf.append(")\n").toString();
	}

	@Override
	public Object build(Builder builder, Object object) {
		switch(this.op.value){
		case NOT:{
			//Object obj = builder.create(object, op.value);
			//return a.build(builder, obj);
			throw new Wrong("not clause exists");
		}
		case AND: case OR:{
			Object obj = builder.create(object, op.value == OP.AND);
			a.build(builder, obj);
			b.build(builder, obj);
			return obj;
		}
		default:{
			builder.append(object, op.value, 
				(String)a.build(builder, object),
				b.build(builder, object)
			);
		}break;
		}
		return null;
	}

	@Override
	public Exp closeWithNot(boolean not) {
		if(b == null){//唯有not是单向操作的
			//消除not
			return a.closeWithNot(!not);
		}
		op.closeWithNot(not);
		a = a.closeWithNot(not);
		b = b.closeWithNot(not);
		//另外做点特殊化的处理
		switch(op.value){
		case MOD_REG:case NOT_MOD:{
			if(b instanceof Str){
				 Str str = (Str)b;
				 //字符串模式的正则表达式
				 if(str.value.charAt(0) == '/'){
					 int idx = str.value.lastIndexOf('/');
					 List<String> ls = new ArrayList<String>();
					 ls.add(str.value.substring(1, idx));
					 ls.add(str.value.substring(idx + 1));
					 Val val = new Val();
					 val.value = ls;
					 b = val;
				 }
			}
		}break;
		case TYPE_AS:case NOT_TYPE:
			//将字符串类型转换为int
			if(b instanceof Str){
				b = new Val(MongoType.of(((Str)b).value).getIndex());
			}else if(b instanceof Lab){
				b = new Val(MongoType.of(((Lab)b).value).getIndex());
			}
		}
		return this;  
	}
	
	

}
