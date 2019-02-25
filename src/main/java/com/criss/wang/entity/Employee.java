package com.criss.wang.entity;

import java.io.Serializable;

public class Employee implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2460309992826073173L;

	private String name;

	private String nickName;

	private int age;

	private String sex;

	public Employee() {
		super();
	}

	public Employee(String name, String nickName, int age, String sex) {
		super();
		this.name = name;
		this.nickName = nickName;
		this.age = age;
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}



}
