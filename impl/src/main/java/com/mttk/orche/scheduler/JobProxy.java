package com.mttk.orche.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mttk.orche.service.support.SchedulerCallback;



public class JobProxy implements Job {
	public static final String JOB_CLASS_NAME="JOB_CLASS_NAME";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//System.out.println("$$$Executing"+context);
		JobDataMap jobDataMap=context.getJobDetail().getJobDataMap();
		Map<String,Object> map=new HashMap<>();
		String[] keys=jobDataMap.getKeys();
		String className=null;
		for (String key:keys){
			if (JobProxy.JOB_CLASS_NAME.equalsIgnoreCase(key)){
				className=jobDataMap.getString(JobProxy.JOB_CLASS_NAME);
			}else{
				map.put(key, jobDataMap.get(key));
			}
		}
		if (className==null){
			throw new JobExecutionException("No job class is found");
		}
		Object object=null;
		try{
			object=Class.forName(className).newInstance();
		}catch(Exception e){
			throw new JobExecutionException("Fail to instance class:"+className+".Reason:"+e.getMessage(),e);
		}
		if (object instanceof SchedulerCallback){
			SchedulerCallback callback=(SchedulerCallback)object;
			try{
				callback.doInScheduler(map);
			}catch(Exception e){
				throw new JobExecutionException(e);
			}
		}else{
			throw new JobExecutionException(className+" does not instance of interface "
					+SchedulerCallback.class.getCanonicalName());
		}
	}

}
