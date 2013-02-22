package org.zkoss.model;

import org.zkoss.addon.SpaceTreeData;

public class UserDataBean implements SpaceTreeData {

	private String id;
	private String name;
	private int age;

	public UserDataBean(String id, String name, int age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
