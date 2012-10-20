package com.myrss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myrss.model.RSSAddr;
import com.myrss.util.DBHelper;
import com.myrss.util.MyAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class RssMgrAct extends Activity {

	private ListView list = null;
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private MyAdapter adapter = null;
	private DBHelper dbhelper = DBHelper.GetInstance(RssMgrAct.this);
	private String categoryid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rssmgrui);
		list = (ListView) findViewById(R.id.rssLv);
		// 配置适配器
		adapter = new MyAdapter(this, getData());
		list.setAdapter(adapter);
		
		// 长按事件
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> c = (HashMap<String, String>) list
						.getAdapter().getItem(pos);
				buildEditDialog(RssMgrAct.this, c.get("Id"), c.get("Name"),c.get("URL"))
						.show();
				return false;
			}
		});
	}

	// 获取数据
	private List<Map<String, String>> getData() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent
					.getBundleExtra("android.intent.extra.categoryid");
			if (bundle == null) {
				setTitle("不好意思程序出错啦");
			} else {
				categoryid = bundle.getString("categoryid");
				List<RSSAddr> lsRSS = dbhelper.GetRSSListByID(dbhelper,
						categoryid);
				data.clear();
				for (RSSAddr c : lsRSS) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("Id", c.getId());
					item.put("Name", c.getName());
					item.put("URL",c.getURL());
					data.add(item);
				}
			}
		}
		return data;
	}

	@Override
	// 设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "添加RSS").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "删除RSS").setIcon(
				android.R.drawable.ic_menu_delete);
		return true;
	}

	@Override
	// 菜单按钮点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			buildDialog(RssMgrAct.this, categoryid).show();
			break;
		case Menu.FIRST + 2:
			DeleteCategoryDialog();
			break;
		}
		return false;
	}

	// 添加RSS
	private Dialog buildDialog(Context context, final String cid) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.rss, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(R.string.addRSS_alert);
		builder.setView(textEntryView);
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText rssname_edit = (EditText) textEntryView
								.findViewById(R.id.rssname_edit);
						EditText rssaddr_edit = (EditText) textEntryView
								.findViewById(R.id.rssaddr_edit);
						ContentValues initialValues = new ContentValues();
						initialValues.put("Name", rssname_edit.getText()
								.toString());
						initialValues.put("URL", rssaddr_edit.getText()
								.toString());
						initialValues.put("CategoryId", cid);
						initialValues.put("Flag", "0");

						HashMap<String, String> item = new HashMap<String, String>();
						item.put("Id", dbhelper.Add(dbhelper, "RssAddr",
								initialValues));
						list = (ListView) findViewById(R.id.rssLv);
						adapter = new MyAdapter(RssMgrAct.this, getData());
						list.setAdapter(adapter);
					}
				});
		builder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		return builder.create();
	}

	// 删除RSS
	private void DeleteCategoryDialog() {
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
						ListView rssLv = (ListView) findViewById(R.id.rssLv);
						// 获取所有选中项的ID
						MyAdapter myAdapter = (MyAdapter) rssLv
								.getAdapter();
						Map<Integer, Map<String, String>> selectMap = myAdapter.selectMap;
						Iterator<Entry<Integer, Map<String, String>>> select = selectMap
								.entrySet().iterator();
						for (int i = 0; i < selectMap.size(); i++) {
							Map.Entry<Integer, Map<String, String>> entry = (Map.Entry<Integer, Map<String, String>>) select
									.next();
							Map<String, String> value = (Map<String, String>) entry
									.getValue();
							String id = value.get("Id");
							dbhelper.Delete(dbhelper, "RssAddr", id);// 删除RSS
							data.remove(value);
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

	// 编辑RSS
	private Dialog buildEditDialog(Context context, final String id, String name,String url) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.rss, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(R.string.editRSS_alert);
		builder.setView(textEntryView);
		final EditText rssname_edit = (EditText) textEntryView
				.findViewById(R.id.rssname_edit);
		final EditText rssaddr_edit = (EditText) textEntryView
				.findViewById(R.id.rssaddr_edit);
		rssname_edit.setText(name);
		rssaddr_edit.setText(url);
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String sql = "UPDATE RssAddr SET Name='"
								+ rssname_edit.getText().toString() + "',URL='"
								+ rssaddr_edit.getText().toString() + "' WHERE Id='"
								+ id + "'";
						dbhelper.ExeSQL(dbhelper, sql);
						list = (ListView) findViewById(R.id.rssLv);
						adapter = new MyAdapter(RssMgrAct.this, getData());
						list.setAdapter(adapter);
					}
				});
		builder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		return builder.create();
	}
}