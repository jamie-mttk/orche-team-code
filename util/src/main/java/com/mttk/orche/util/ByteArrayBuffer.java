package com.mttk.orche.util;

import java.io.*;


//maping.jar在使用
public class ByteArrayBuffer {
	ByteArrayOutputStream bos=null; 
	public ByteArrayBuffer(){ 
		this(10);
	}
	public ByteArrayBuffer(int capacity){
		bos=new ByteArrayOutputStream(capacity);
	}
	public ByteArrayBuffer append(byte data)throws IOException{
		bos.write(new byte[]{data});
		return this;
	}
	public ByteArrayBuffer append(byte[] data) throws IOException{
		bos.write(data);
		return this;
	}
	public int length(){
		if (bos==null){
			return 0;
		}
		return bos.size();
	}
	public byte[] toByteArray(){
		if (bos==null){
			return null;
		}
		//
		return bos.toByteArray();
	}
	
}
