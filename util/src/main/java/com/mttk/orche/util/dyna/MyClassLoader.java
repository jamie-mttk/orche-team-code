package com.mttk.orche.util.dyna;

public class MyClassLoader extends ClassLoader {
	private byte[] code;
	private String className;
	public MyClassLoader(ClassLoader parent,String className,byte[] code) {
		super(parent);
		//
		this.className=className;
		this.code=code;
	}
	protected  Class<?> findClass(String name)  throws ClassNotFoundException{
		  if (className.equals(name)) {
//			  System.out.println("Load and return:"+name);
			  return defineClass(name, code, 0, code.length);  
		  }else {
//			  System.out.println("Foward to parent:"+name);
			  return super.findClass(name);
		  }
      }

}
