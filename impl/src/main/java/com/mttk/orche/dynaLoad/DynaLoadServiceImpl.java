package com.mttk.orche.dynaLoad;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;
import com.mttk.orche.core.impl.AbstractService;
import com.mttk.orche.service.DynaLoadService;

import com.mttk.orche.support.ServerUtil;

@ServiceFlag(key = "dynaLoadService", name = "动态加载jar包", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class DynaLoadServiceImpl
    extends AbstractService
    implements DynaLoadService {
  @Control(label = "检查路径", size = 1, defaultVal = "")
  private String checkingPath = null;
  @Control(label = "检查间隔(秒)", mandatory = true, size = 1, defaultVal = "60")
  private long checkPeriod = 60; // 检查秒数,每隔此时间会检查是否有文件更新
  @Control(label = "加载前等待(秒)", mandatory = true, size = 1, defaultVal = "1")
  private long delayPeriod = 1; // 等待秒数,检查到更新后等待此秒数,防止文件正在写入
  private File checkingPathFile = null;
  private CheckPathPollingThread checkPathPollingThread = null;
  private JarFileClassLoader dynamicClassLoader = null;
  private Logger logger = LoggerFactory.getLogger(DynaLoadServiceImpl.class);

  public void setCheckingPath(String checkingPath) {
    this.checkingPath = checkingPath;
    //
    checkingPathFile = new File(checkingPath);
  }

  public String getCheckingPath() {
    return checkingPath;
  }

  public long getCheckPeriod() {
    return checkPeriod;
  }

  public void setCheckPeriod(long checkPeriod) {
    this.checkPeriod = checkPeriod;
  }

  public void setDelayPeriod(long delayPeriod) {
    this.delayPeriod = delayPeriod;
  }

  public long getDelayPeriod() {
    return delayPeriod;
  }

  @Override
  protected void doStart() {

    if (checkingPathFile == null) {
      checkingPathFile = new File(ServerUtil.getPathHome(server) + File.separator + "dynamic");
      if (checkingPathFile.isDirectory() && checkingPathFile.exists()) {

      } else {
        logger.info("No checking path is found,dynamic load is disabled:" + checkingPathFile);
        return;
      }
    }
    //
    logger.info("Start dynamic load from " + checkingPathFile + " with check period " + checkPeriod
        + " second(s) and delay period " + delayPeriod + " second(s)");
    //
    try {
      doCheck(true);
    } catch (Exception e) {
      logger.error("Check dynamic load failed", e);
    }
    //
    checkPathPollingThread = new CheckPathPollingThread(this, checkPeriod);
    checkPathPollingThread.start();

  }

  @Override
  protected void doStop() {
    if (checkPathPollingThread != null) {
      checkPathPollingThread.setStop(true);
      checkPathPollingThread.interrupt();
    }
  }

  @Override
  public void resetClassLoader() throws Exception {
    doCheck(true);
  }

  protected void doCheck(boolean immediate) throws Exception {
    List<File> list = isChanged();
    if (list != null) {
      logger.info("Dynamicload detect change,class loader will be recreated with " + list);
    } else {
      // logger.info("Dynamic load does not detect any change.");
      return;
    }
    //
    if (getDelayPeriod() > 0 && !immediate) {
      logger.info("Dynamic load detected change,wait for  " + getDelayPeriod() + " seconds");
      try {
        Thread.sleep(getDelayPeriod() * 1000);
      } catch (Exception e) {
        //
      }
    }
    //
    buildClassLoader(list);
  }

  private void buildClassLoader(List<File> list) throws IOException {

    // 创建一个新的classloader
    JarFileClassLoader dynamicClassLoaderNew = new JarFileClassLoader(Thread.currentThread().getContextClassLoader());
    for (File file : list) {
      try {
        dynamicClassLoaderNew.addJarFile(file);
      } catch (Exception e) {
        logger.error("Add jar file to dynamic load class loader failed:" + file.getAbsolutePath(), e);
      }
    }
    //
    if (dynamicClassLoader != null) {
      try {
        dynamicClassLoader.close();
      } catch (Exception e) {
        logger.error("Close class loader failed:" + dynamicClassLoader, e);
      }
    }
    // 把新创建的赋值给以前的，以前的舍弃
    dynamicClassLoader = dynamicClassLoaderNew;
    //
    logger.info("New class loader is built:" + dynamicClassLoader);
  }

  private FileFilter filter = new FileFilter() {
    public boolean accept(File file) {
      return file.isDirectory() || file.getName().endsWith(".jar");
    };
  };

  private void scan(File dir, List<File> list) throws IOException {
    File[] files = dir.listFiles(filter);
    if (files != null) {
      for (File file : files) {
        if (file.isFile()) {
          list.add(file);
        } else if (file.isDirectory()) {
          scan(file, list);
        }
      }
    }
  }

  // private void scan(File dir,List<File> list) throws Exception{
  // PaperService paperService=context.getServer().getService(PaperService.class);
  // Paper paper=paperService.parse("/_DYNAMIC/_MAPPING");
  // File file=paperService.toFile(paper);
  // this.scan1(file, list);
  // }
  // private void scan1(File dir,List<File> list) throws Exception{
  // File[] files = dir.listFiles(filter);
  // if (files != null) {
  // for (File file : files) {
  // if (file.isFile()) {
  // list.add(file);
  // } else if(file.isDirectory()){
  // scan1(file,list);
  // }
  // }
  // }
  // }
  // 列表是否有修改
  private List<File> isChanged() throws Exception {

    List<File> list = new ArrayList<File>(20);
    scan(checkingPathFile, list);
    // logger.info("####FOUND LIST:"+list);
    if (dynamicClassLoader == null) {
      return list;
    }
    if (list.size() != dynamicClassLoader.getJarFileList().size()) {
      return list; // Maybe some file is added or removed
    }
    for (File file : list) {
      if (isModified(file, dynamicClassLoader.getJarFileList())) {
        return list;
      }
    }
    return null;
  }

  private boolean isModified(File file, List<JarFileWrapper> list) throws IOException {
    String path = file.getCanonicalPath();
    JarFileWrapper fileFound = null;
    for (JarFileWrapper wrap : list) {
      if (path.equals(wrap.getFileName())) {
        fileFound = wrap;
        break;
      }
    }
    if (fileFound == null) {
      return true;// this means the file (first parameter) indicated is a new file
    }
    return fileFound.getLastModified() != file.lastModified();
  }

  @Override
  public ClassLoader obtainClassLoader() throws Exception {
    if (dynamicClassLoader == null) {
      return Thread.currentThread().getContextClassLoader();
    } else {
      return dynamicClassLoader;
    }
  }

  @Override
  public Object createObject(String name) throws Exception {
    ClassLoader cl = obtainClassLoader();
    //
    try {
      Class clazz = cl.loadClass(name);
      Object object = clazz.newInstance();
      if (logger.isDebugEnabled()) {
        logger.debug("Dynamic load " + name + " with class loader:" + cl);
      }
      return object;
    } catch (Exception e) {
      throw new Exception("Dynamic create class failed:" + name, e);
    }
    //

  }

}
