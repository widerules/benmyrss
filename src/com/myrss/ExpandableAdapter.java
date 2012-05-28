package com.myrss;

import java.util.ArrayList;
import java.util.List;

import com.myrss.model.Category;
import com.myrss.model.RSSAddr;
import com.myrss.util.DBHelper;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

//二级链表的Adapter
public class ExpandableAdapter extends BaseExpandableListAdapter{
	
	private Activity activity;
	private List<Category> groupArray;
	private List<List<RSSAddr>> childArray = new ArrayList<List<RSSAddr>>();

	//初始化数据
	private void initData() {
		DBHelper dbHelper = DBHelper.GetInstance(activity);
		groupArray = dbHelper.GetCategoryList(dbHelper);

		for (int index = 0; index < groupArray.size(); ++index) {
			List<RSSAddr> tempArray = dbHelper.GetRSSListByID(dbHelper,
					groupArray.get(index).getId());
			childArray.add(tempArray);
		}
	}

	public ExpandableAdapter(Activity a) {
		initData();
		activity = a;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return childArray.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return childArray.get(groupPosition).size();
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String string = childArray.get(groupPosition).get(childPosition)
				.getName();
		return getGenericView(string);
	}

	public Object getGroup(int groupPosition) {
		return groupArray.get(groupPosition);
	}

	public int getGroupCount() {
		return groupArray.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String string = groupArray.get(groupPosition).getName();
		return getGenericView(string);
	}

	public TextView getGenericView(String string) {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView text = new TextView(activity);
		text.setLayoutParams(layoutParams);
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		text.setPadding(36, 0, 0, 0);
		text.setText(string);
		return text;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}