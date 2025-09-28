package com.mttk.orche.service.support;

public class DeployStrategy {
	// 如果true,则不保存直接返回
	private boolean parseOnly = false;
	// 是否覆盖:true,用新数据覆盖就数据;false,不覆盖旧数据
	private boolean overwrite = true;
	// 是否在遇到错误时任然保存
	private boolean forceSave = true;
	// 是否初始化模式,如果是则否则特殊情况下不报错
	private boolean initMode;

	//
	public DeployStrategy() {
	}

	//
	public DeployStrategy(boolean parseOnly, boolean overwrite, boolean forceSave) {
		this(parseOnly, overwrite, forceSave, false);
	}

	//
	public DeployStrategy(boolean parseOnly, boolean overwrite, boolean forceSave, boolean initMode) {
		super();
		this.parseOnly = parseOnly;
		this.overwrite = overwrite;
		this.forceSave = forceSave;
		this.initMode = initMode;
	}

	//
	public boolean isParseOnly() {
		return parseOnly;
	}

	public void setParseOnly(boolean parseOnly) {
		this.parseOnly = parseOnly;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean isForceSave() {
		return forceSave;
	}

	public void setForceSave(boolean forceSave) {
		this.forceSave = forceSave;
	}

	public boolean isInitMode() {
		return initMode;
	}

	public void setInitMode(boolean initMode) {
		this.initMode = initMode;
	}

}
