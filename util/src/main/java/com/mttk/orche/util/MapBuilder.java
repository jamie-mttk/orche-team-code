package com.mttk.orche.util;

import java.util.HashMap;
import java.util.Map;

//通过流式方式创建Map
public class MapBuilder<K,V> {
	private    Map<K,V> map=new HashMap<>();
	  private  MapBuilder() {
         
      }
	  public static <K, V> MapBuilder<K, V> builder(K k,V v) {
	        return new MapBuilder<K,V>().put(k, v);
	    }
    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<K,V>();
    }
    public MapBuilder<K, V> put(K k, V v) {
    	map.put(k, v);
        return this;
    }
    public Map<K, V> build() {
        return map;
    }
}
