package com.mttk.orche.util.support;
//Similar to java function but support exception 
@FunctionalInterface
public interface MyFunction<T, R> {

	    /**
	     * Applies this function to the given argument.
	     *
	     * @param t the function argument
	     * @return the function result
	     */
	    R apply(T t) throws Exception;
}
