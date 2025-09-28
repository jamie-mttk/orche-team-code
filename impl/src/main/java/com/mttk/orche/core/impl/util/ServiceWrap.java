package com.mttk.orche.core.impl.util;

import org.bson.Document;
//记录了服务以及服务的Document
import com.mttk.orche.core.Service;

public class ServiceWrap {
	private Document doc;
	private  Service service;
	
	public ServiceWrap() {
		super();
	}
	public ServiceWrap(Document doc, Service service) {
		super();
		this.doc = doc;
		this.service = service;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	
}
