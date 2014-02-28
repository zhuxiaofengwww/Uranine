package com.version1.uranine.domain;

public class LogInfo {
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	private String appname;
	private String content;
	private String logtime;
	public String getLogtime() {
		return logtime;
	}
	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}
}
