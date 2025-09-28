package com.mttk.orche.deploy.support;

import com.mttk.orche.util.StringUtil;

//拆分由冒号分隔的两部分
public class Split {
	private String part1=null;
	private String part2=null;
	public Split(String raw) {
		if (StringUtil.isEmpty(raw)) {
			return;
		}
		raw=raw.trim();
		int index=raw.indexOf(":");
		if (index<0) {
			part1=raw;
		}else if (index==0) {
			part2=raw.substring(1);
		}else if (index==raw.length()-1) {
			part1=raw.substring(0, index);
		}else {
			part1=raw.substring(0, index);
			part2=raw.substring(index+1);
		}
		part1=trimIfNeed(part1);
		part2=trimIfNeed(part2);
	}
	
	public String getPart1() {

		return part1;
	}
	
	public String getPart2() {
		return part2;
	}

	@Override
	public String toString() {
		return "part1="+part1+",part2="+part2;
	}
	//
	private String trimIfNeed(String str) {
		if (str==null) {
			return null;
		}else {
			return str.trim();
		}
	}
}