package com.mttk.orche.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.mttk.orche.addon.annotation.ServiceFlag;
import com.mttk.orche.addon.annotation.ServiceFlag.SERVICE_TYPE;
import com.mttk.orche.addon.annotation.ui.Control;

import com.mttk.orche.core.impl.AbstractService;
import com.mttk.orche.service.LogService;
import com.mttk.orche.support.NamePatternFilter;
import com.mttk.orche.support.NamePatternFilter.PATTERN_TYPE;
import com.mttk.orche.support.ServerUtil;
import com.mttk.orche.util.StringUtil;

@ServiceFlag(key = "logService", name = "日志", description = "", type = SERVICE_TYPE.SYS, i18n = "/com/mttk/api/impl/i18n")
public class LogServiceImpl extends AbstractService implements
		LogService {
	@Control(label = "日志路径", defaultVal = "", size = 1)
	private String logPath = null;

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	private String getLogPath() {
		if (StringUtil.notEmpty(logPath)) {
			if (!(logPath.endsWith("/") || logPath.endsWith("\\"))) {
				logPath += File.separator;

			}
			// System.out.println("######################"+logPath);
			return logPath;
		} else {
			return ServerUtil.getPathHome(server) + File.separator + "log" + File.separator;
		}
	}

	@Override
	public List<LogService.LogFile> list(String nameFilter) throws Exception {
		// System.out.println("nameFilter="+nameFilter);
		NamePatternFilter filter = new NamePatternFilter(PATTERN_TYPE.WILD_CARD, nameFilter, null);

		File file = new File(getLogPath());
		List<LogService.LogFile> logs = new ArrayList<>(32);
		Stack<String> stack = new Stack<>();
		dumpList(file, logs, stack, filter);
		// sort
		logs.sort((a1, a2) -> {
			long result = (a2.getLastModified().getTime() - a1.getLastModified().getTime());
			// 把result转换成int直接返回有问题,可能result大于Integer.max导致引发下面错误
			// 原因是Comparator 要满足自反性，传递性，对称性
			// 参考 https://blog.csdn.net/liuxiao723846/article/details/53760363
			// Comparison method violates its general contract!
			if (result > 0) {
				return 1;
			} else if (result == 0) {
				return 0;
			} else {
				return -1;
			}
		});
		//
		return logs;
	}

	private void dumpList(File path, List<LogService.LogFile> logs, Stack<String> stack, NamePatternFilter filter) {
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				stack.push(files[i].getName());
				dumpList(files[i], logs, stack, filter);
				stack.pop();
			} else {
				if (filter.accept(files[i].getName())) {
					logs.add(toEntity(files[i], stack));
				}
			}
		}
	}

	private LogService.LogFile toEntity(File file, Stack<String> stack) {

		StringBuffer sb = new StringBuffer(128);
		for (int i = 0; i < stack.size(); i++) {
			sb.append(stack.get(i)).append("/");
		}
		sb.append(file.getName());
		return new LogService.LogFile(sb.toString(), file.length(), new Date(file.lastModified()));
	}

	@Override
	public InputStream load(String name) throws Exception {
		// Check name
		// remove .. / \
		// name=name.replaceAll("\\.\\.","").replaceAll("/", "").replaceAll("\\\\","");
		try {
			return new FileInputStream(getLogPath() + name);
		} catch (Exception e) {
			throw new Exception("Load log file failed:" + name, e);
		}
	}

}
