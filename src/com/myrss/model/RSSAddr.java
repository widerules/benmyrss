package com.myrss.model;

public class RSSAddr {
	
	private String Id;
	private String Name;
	private String URL;
	private String CategoryId;
	private String Flag;

	public RSSAddr() {
		super();
	}

	public RSSAddr(String id, String name, String uRL, String categoryId,
			String flag) {
		super();
		Id = id;
		Name = name;
		URL = uRL;
		CategoryId = categoryId;
		Flag = flag;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getCategoryId() {
		return CategoryId;
	}

	public void setCategoryId(String categoryId) {
		CategoryId = categoryId;
	}

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
	}
}
