/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.ql;

import com.julewan.mongo.ql.parse.Ctx;
import com.julewan.mongo.ql.parse.Exp;

public class Statement {
	 
	private Exp expression = null;
	public Statement(String ql){
		expression = new Ctx(ql).getExpression(); 
	}

	public boolean isValid(){
		return expression != null;
	}
	
	public Object build(Builder b, Object parent){
		return expression.build(b, parent);
	}
	
}
