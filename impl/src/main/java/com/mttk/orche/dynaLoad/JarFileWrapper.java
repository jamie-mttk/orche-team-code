package com.mttk.orche.dynaLoad;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

class JarFileWrapper
{private String fileName=null;
private long lastModified=0;

public JarFileWrapper(File file) throws IOException{
  fileName=file.getCanonicalPath();
  lastModified=file.lastModified();
}

public String getFileName()
{
  return fileName;
}

public long getLastModified()
{
  return lastModified;
}

@Override
public String toString() {
	String lm=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(lastModified));
	return "JarFileWrapper [fileName=" + fileName + ", lastModified=" + lm + "]";
}

}
