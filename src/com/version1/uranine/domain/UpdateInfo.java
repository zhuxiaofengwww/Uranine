package com.version1.uranine.domain;

public class UpdateInfo {

	private String version;
	private String description;
	private String path;
	
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "UpdateInfo [version=" + version + ", description="
				+ description + ", path=" + path + "]";
	}
	
	
}
