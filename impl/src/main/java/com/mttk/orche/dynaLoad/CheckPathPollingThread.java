package com.mttk.orche.dynaLoad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckPathPollingThread
    extends Thread
{private boolean isStop=false;
  private DynaLoadServiceImpl engine=null;
  private long  checkPeriod=10;
  private Logger logger=LoggerFactory.getLogger(CheckPathPollingThread.class);
  public CheckPathPollingThread(DynaLoadServiceImpl engine,long  checkPeriod){
	  this.setName("DynaLoad thread");
    this.engine=engine;
    this.checkPeriod=checkPeriod;
  }
  public void setStop(boolean isStop)
{
  this.isStop = isStop;
}

  @Override
  public void run() {
    logger.info("Check dynamic load thread start running");
    
    while(!isStop){
      try{
        sleep(checkPeriod*1000);//Check every 10 seconds
      }catch(Exception e){
        
      }
      try{
        engine.doCheck(false);
      }catch(Exception e){
        logger.error("Check dynamic load failed", e);
      }
    }
  }
}
