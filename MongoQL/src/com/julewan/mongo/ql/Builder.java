/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-8-20
 * 
**/
package com.julewan.mongo.ql;


public interface Builder {
		
	public void initialize(Object ... values);
	public Object getArgument(int idx);
	public Object create(Object parent, boolean and);
	public void append(Object parent, OP op, String col, Object value);
	
}
