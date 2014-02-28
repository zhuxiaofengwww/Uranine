package com.version1.uranine.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private Drawable icon;
	private String name;
	private String packname;
	private String version;
	private String signature;
	private boolean inrom;
	private boolean userapp;
	
	private String appPath;
	

	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packname=" + packname
				+ ", version=" + version + ", inrom=" + inrom + ", userapp="
				+ userapp + "]";
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isInrom() {
		return inrom;
	}
	public void setInrom(boolean inrom) {
		this.inrom = inrom;
	}
	public boolean isUserapp() {
		return userapp;
	}
	public void setUserapp(boolean userapp) {
		this.userapp = userapp;
	}
	
	//get apk path
	public String getAppPath() {
		return appPath;
	}
	//set apk path
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}	
	
}
