package com.mttk.orche.addon.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mttk.orche.addon.Event;

/**
 * Event实现类
 *
 */
public class EventImpl extends EntityImpl<Event> implements Event {
	public EventImpl() {		
	}
	public EventImpl(Map<String,Object> map) {	
		super(map);
	}
	@Override
	public  Event getBean(String key) {
		Map<String,Object> map=(Map<String,Object>)get(key, Map.class);
		if (map==null) {
			return null;
		}else {
			return new EventImpl(map);
		}
	}
	@Override
	public  List<Event> getBeanList(String key){
		List<Map<String,Object>> list=(List<Map<String,Object>>)get(key, List.class);
		if (list==null) {
			return null;
		}
		List<Event> newList=new ArrayList<>(list.size());
		list.forEach((m)->{
			newList.add(new EventImpl(m));
		});
		return newList;
	}
}
