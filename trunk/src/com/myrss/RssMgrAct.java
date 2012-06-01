package com.myrss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myrss.model.RSSAddr;
import com.myrss.util.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RssMgrAct extends Activity {

	private DBHelper dbhelper = DBHelper.GetInstance(RssMgrAct.this);
	private SimpleAdapter adapter = null;
	private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.rssmgrui);
		initData();
	}

	// 初始化数据
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent
					.getBundleExtra("android.intent.extra.categoryid");
			if (bundle == null) {
				setTitle("不好意思程序出错啦");
			} else {
				// 初始化列表
				ListView categoryLv = (ListView) findViewById(R.id.rssLv);
				List<RSSAddr> lsRSS = dbhelper.GetRSSListByID(dbhelper,
						bundle.getString("categoryid"));
				for (RSSAddr c : lsRSS) {
					HashMap<String, String> item = new HashMap<String, String>();
					item.put("Id", c.getId());
					item.put("Name", c.getName());
					data.add(item);
				}
				adapter = new SimpleAdapter(this, data,
						android.R.layout.simple_list_item_multiple_choice,
						new String[] { "Name" },
						new int[] { android.R.id.text1 }) {
					@Override
					public long getItemId(int pos) {
						long id = Long.parseLong(data.get(pos).get("Id"));
						return id;
					}
				};
				categoryLv.setAdapter(adapter);
				categoryLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			}
		} else {
			setTitle("不好意思程序出错啦");
		}
	}

	@Override
	// 设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "添加源").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "编辑源").setIcon(
				android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, "删除源").setIcon(
				android.R.drawable.ic_menu_delete);
		return true;
	}

	@Override
	// 菜单按钮点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			AddRss();
			break;
		case Menu.FIRST + 2:
			EditRss();
			break;
		case Menu.FIRST + 3:
			DeleteRSS();
			break;
		}
		return false;
	}

	// 删除RSS
	private void DeleteRSS() {
		// 设置提示框
		AlertDialog.Builder builder = new AlertDialog.Builder(RssMgrAct.this);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(R.string.alert);
		builder.setMessage(R.string.del_alert);
		// 点击确定
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView categoryLv = (ListView) findViewById(R.id.rssLv);
						// 获取所有选中项的ID
						SparseBooleanArray sbArray = categoryLv
								.getCheckedItemPositions();
						for (int i = 0; i < data.size(); i++) {
							if (sbArray.valueAt(i)) {
								String id = String.valueOf(categoryLv
										.getAdapter().getItemId(
												sbArray.keyAt(i)));
								dbhelper.Delete(dbhelper, "RssAddr", id);// 删除RSS
								data.remove(i);// 从list中移除
							}
						}
						adapter.notifyDataSetChanged();// 刷新数据
					}
				});
		// 点击取消
		builder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	// 添加RSS
	private void AddRss() {
		Intent intent = new Intent();
		intent.setClass(RssMgrAct.this, RssAct.class);
		startActivity(intent);
	}

	// 编辑RSS
	private void EditRss() {
		String id = null;
		ListView categoryLv = (ListView) findViewById(R.id.rssLv);
		// 获取所有选中项的ID
		SparseBooleanArray sbArray = categoryLv.getCheckedItemPositions();
		for (int i = 0; i < data.size(); i++) {
			if (sbArray.valueAt(i)) {
				id = String.valueOf(categoryLv.getAdapter().getItemId(
						sbArray.keyAt(i)));
			}
		}
		if (id != null && id != "") {
			Intent intent = new Intent();
			intent.putExtra("IsEdit", "true"); // 设置编辑标志位
			intent.putExtra("RssID", id);
			intent.setClass(RssMgrAct.this, RssAct.class);
			startActivity(intent);
		}
	}
}