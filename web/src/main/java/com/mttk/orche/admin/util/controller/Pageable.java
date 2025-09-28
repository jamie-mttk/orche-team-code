package com.mttk.orche.admin.util.controller;

public class Pageable {
	private int total;
	private int page=1;//开始
	private int size=0;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPage() {
		if (size<=0) {
			return 1;
		}
		//判断page是否超过了总页数给出的范围
		int totalPage=0;
		if(total%size==0){
			totalPage=total/size;
		}else {
			totalPage=total/size+1;
		}
		if (page>totalPage) {
			page=totalPage;
		}
		if (page<=0) {
			page=1;
		}
		//
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	//得到当前page和size下偏移量
	public int getOffset() {
		return (getPage()-1)*getSize();
	}
	//
	@Override
	public String toString() {
		return "Pageable [total=" + total + ", page=" + page + ", size=" + size + "]";
	}
	
	
}
