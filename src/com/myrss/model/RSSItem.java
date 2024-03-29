package com.myrss.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RSSItem {
	public static final String TITLE = "title";
	public static final String PUBDATE = "pubdate";
	private String title = null;
	private String description = null;
	private String link = null;
	private String category = null;
	private String pubdate = null;

	public RSSItem() {
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setPubDate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public String getCategory() {
		return category;
	}

	public String getPubDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now=new Date(pubdate);
		return formatter.format(now);
	}

	public String toString() {
		if (title.length() > 20) {
			return title.substring(0, 30) + "...";
		}
		return title;
	}
}