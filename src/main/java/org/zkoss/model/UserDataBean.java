package org.zkoss.model;

public class UserDataBean {

	private String name;
	private int age;

	public UserDataBean(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
