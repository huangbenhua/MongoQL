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

public interface Exp {
	
	public boolean closed();
	public boolean accept(Ctx c);
	public boolean accept(Exp e);
	public Exp closeWithNot(boolean not);
	public Object build(Builder builder, Object object);
	
}
