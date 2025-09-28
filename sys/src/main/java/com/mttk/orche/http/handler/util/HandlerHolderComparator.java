package com.mttk.orche.http.handler.util;

import java.util.Comparator;

public class HandlerHolderComparator implements Comparator<HandlerHolder> {
	private Comparator<String> patternComparator;
	public HandlerHolderComparator(Comparator<String> patternComparator) {
		this.patternComparator=patternComparator;
	}
	@Override
    public int compare(HandlerHolder h1, HandlerHolder h2) {
    	return patternComparator.compare(h1.getAdapterConfig().getString("uri"), h2.getAdapterConfig().getString("uri"));
    }
}
