package com.mttk.orche.support;

import java.util.regex.Pattern;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.util.PatternUtil;
import com.mttk.orche.util.StringUtil;

public class NamePatternFilter {
	public enum PATTERN_TYPE {
		WILD_CARD, REGEX
	};

	private String filePattern = null;
	private String excludeFilePattern = null;
	private Pattern filePatternObj = null;
	private Pattern excludeFilePatternObj = null;

	public NamePatternFilter(AdapterConfig config) {
		this(PATTERN_TYPE.valueOf(config.getString("patternType", "WILD_CARD")),
				config.getString("filePattern"), config.getString("excludeFilePattern"));
	}

	public NamePatternFilter(PATTERN_TYPE patternType, String filePattern, String excludeFilePattern) {
		this.filePattern = filePattern;
		//
		if (StringUtil.notEmpty(filePattern)) {
			filePatternObj = Pattern.compile(convertPattern(patternType, filePattern));
		}
		if (StringUtil.notEmpty(excludeFilePattern)) {
			excludeFilePatternObj = Pattern.compile(convertPattern(patternType, excludeFilePattern));
		}
	}

	private String convertPattern(PATTERN_TYPE patternType, String pattern) {
		switch (patternType) {
			case WILD_CARD:
				return PatternUtil.wildCardTransform(pattern);
			case REGEX:
			default:
				return pattern;
		}
	}

	public boolean accept(String name) {
		// Check whether it matches the include pattern
		if (filePatternObj != null && !filePatternObj.matcher(name).matches()) {
			return false;
		}

		// Check whether it does not match the exclude pattern
		if (excludeFilePatternObj != null && excludeFilePatternObj.matcher(name).matches()) {
			return false;
		} else {
			return true;
		}
	}

}
