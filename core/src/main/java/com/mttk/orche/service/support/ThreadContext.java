package com.mttk.orche.service.support;

//记录当前thread的一些信息
public class ThreadContext {
	protected static Class<? extends ThreadContext> contextClass = ThreadContext.class;
	// 线程是否被中断
	private boolean interrupted = false;
	//
	protected static final ThreadLocal<? extends ThreadContext> threadLocal = new ThreadLocal<ThreadContext>() {
		//
		@Override
		protected ThreadContext initialValue() {
			try {
				return contextClass.newInstance();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	};

	public ThreadContext() {
		super();
	}

	public static ThreadContext getCurrentContext() {
		ThreadContext context = threadLocal.get();
		return context;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

}
