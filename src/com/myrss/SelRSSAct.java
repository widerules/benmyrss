package com.myrss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.myrss.R;
import com.myrss.model.RSSAddr;

public class SelRSSAct extends Activity {

	// 初始化
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.selrssui);

		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expGroup);
		expandableListView.setAdapter(new ExpandableAdapter(this));
		// 单击某一个子项
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				RSSAddr c = (RSSAddr) parent.getExpandableListAdapter()
						.getChild(groupPosition, childPosition);
				Intent intent = new Intent();
				intent = intent.setClass(SelRSSAct.this, MainAct.class);
				Bundle bundle = new Bundle();
				bundle.putString("ID", c.getId());// 添加数据
				intent.putExtras(bundle);
				SelRSSAct.this.setResult(RESULT_OK, intent);// RESULT_OK是返回状态码
				SelRSSAct.this.finish();
				return false;
			}
		});
	}

	@Override
	// 设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "Mini浏览器").setIcon(
				android.R.drawable.ic_menu_send);
		return true;
	}

	@Override
	// 菜单按钮点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			Intent intent = new Intent();
			intent.setClass(SelRSSAct.this, BrowseAct.class);
			startActivity(intent);
			break;
		}
		return false;
	}
}