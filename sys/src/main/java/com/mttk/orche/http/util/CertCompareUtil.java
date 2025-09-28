package com.mttk.orche.http.util;

import java.security.cert.X509Certificate;

public class CertCompareUtil {

	//如果cert1和cert2是同一个cert，返回true
	public static boolean certEqual(X509Certificate cert1,X509Certificate cert2) {
		//
//		System.out.println("========================1");
//		System.out.println(cert1.getSerialNumber());
//		System.out.println(cert1.getSigAlgOID());
//		System.out.println(cert1);
//		System.out.println("========================2");
//		System.out.println(cert2.getSerialNumber());
//		System.out.println(cert2.getSigAlgOID());
//		System.out.println(cert2);
		//System.out.println("========================1");
		return cert1.getSerialNumber().equals(cert2.getSerialNumber()) && cert1.getSigAlgOID().equalsIgnoreCase(cert2.getSigAlgOID());
		//
		//return true;
	}
}
