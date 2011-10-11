/**
 * Copyright (c) 2010-2011 julewa.com.  
 * All rights reserved. 
 * 
 * @author Huang Benhua
 * @date 2011-2-20
 * 
**/
package com.julewan.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


public final class StreamUtils{

	public static final String readText(InputStream in) throws IOException{
		if(in == null)return "";
    	int c = -1;
    	StringBuffer buffer = new StringBuffer();
		while((c=in.read()) != -1) buffer.append((char)c);
		return buffer.toString();
	}
	
	public static final String readNetFile(String urlstr){
		InputStream in = null;
		try {
			URL url = new URL(urlstr);
			in = url.openStream();
			String txt = readText(in);
			return new String(txt.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally{
			if(in != null)try{in.close();}catch(Exception e){}
		}
	}
	
	public static final void saveFile(String file, String text){
		try {
			File f = new File(file);
			if(!f.exists())f.createNewFile();
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(text.getBytes());
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
	

    public static final boolean conjoin(InputStream in, OutputStream out[]) throws IOException{
        try{
	        BufferedInputStream input = new BufferedInputStream(in);
	        BufferedOutputStream output[] = new  BufferedOutputStream[out.length];
	        for(int i = out.length-1; i > -1; i --){
	        	output[i] = new  BufferedOutputStream(out[i]);
	        }
	        int i = 0;
	        while((i = input.read()) != -1){
	            for(BufferedOutputStream o: output)o.write(i);
	        }
	        for(BufferedOutputStream o: output)o.flush();
        }catch(IOException e){
            throw e;
        }finally{
        	for(OutputStream o: out)try{o.close();}catch(Exception e){}
            try{in.close();}catch(Exception e){}
        }
        return true;
    }

	public static final boolean conjoin(InputStream in, OutputStream out) throws IOException{
        try{
	        BufferedInputStream input = new BufferedInputStream(in);
	        BufferedOutputStream output = new  BufferedOutputStream(out);
	        int i = 0;
	        while((i = input.read()) != -1){
	            output.write(i);
	        }
	        output.flush();
        }catch(IOException e){
            throw e;
        }finally{
            try{out.close();}catch(Exception e){}
            try{in.close();}catch(Exception e){}
        }
        return true;
    }
    
}
