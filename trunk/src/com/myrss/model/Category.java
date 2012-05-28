package com.myrss.model;

public class Category {
	
	private String Id;
	private String Name;
	
	public Category() {
		super();
	}
	
	public Category(String id, String name) {
		this.Id = id;
		this.Name = name;
	}
	
	public String getId() {
		return this.Id;
	}

	public void setId(String id) {
		this.Id = id;
	}

	public String getName() {
		return this.Name;
	}

	public void setName(String name) {
		this.Name = name;
	}
	
	@Override
	public String toString() { 
		return this.Name;
	}
}
