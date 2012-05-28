package com.myrss.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RSSFeed {
	private String Id= null;
	private String title = null;
	private String pubdate = null;
	private int itemcount = 0;
	private List<RSSItem> itemlist;

	public RSSFeed() {
		itemlist = new Vector<RSSItem>(0);
	}

	public int addItem(RSSItem item) {
		itemlist.add(item);
		itemcount++;
		return itemcount;
	}

	public RSSItem getItem(int location) {
		return itemlist.get(location);
	}

	public List<RSSItem> getAllItems() {
		return itemlist;
	}

	public List<Map<String, Object>> getAllItemsForListView() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		int size = itemlist.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(RSSItem.TITLE, itemlist.get(i).getTitle());
			item.put(RSSItem.PUBDATE, itemlist.get(i).getPubDate());
			data.add(item);
		}
		return data;
	}

	int getItemCount() {
		return itemcount;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPubDate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getTitle() {
		return title;
	}

	public String getPubDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now=new Date(pubdate);
		return formatter.format(now);
	}

	public void setId(String id) {
		Id = id;
	}

	public String getId() {
		return Id;
	}
}
