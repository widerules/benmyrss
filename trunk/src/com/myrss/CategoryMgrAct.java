package com.myrss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.myrss.model.Category;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.Map;

public class CategoryMgrAct extends Activity {

	private ListView list = null;
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	private MyAdapter adapter = null;
	private DBHelper dbhelper = DBHelper.GetInstance(CategoryMgrAct.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorymgrui);
		list = (ListView) findViewById(R.id.categoryLv);
		// 配置适配器
		adapter = new MyAdapter(this, getData());
		list.setAdapter(adapter);
		// 点击列表事件
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Intent intent = new Intent();
				Bundle value = new Bundle();
				@SuppressWarnings("unchecked")
				HashMap<String, String> c = (HashMap<String, String>) list
						.getAdapter().getItem(pos);
				value.putString("categoryid", c.get("Id"));
				intent.putExtra("android.intent.extra.categoryid", value);
				intent.setClass(CategoryMgrAct.this, RssMgrAct.class);
				startActivity(intent);
			}
		});
		// 长按事件
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> c = (HashMap<String, String>) list
						.getAdapter().getItem(pos);
				buildEditDialog(CategoryMgrAct.this, c.get("Id"), c.get("Name"))
						.show();
				return false;
			}
		});
	}

	// 获取数据
	private List<Map<String, String>> getData() {
		List<Category> lsCategories = dbhelper.GetCategoryList(dbhelper);
		data.clear();
		for (Category c : lsCategories) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("Id", c.getId());
			map.put("Name", c.getName());
			data.add(map);
		}
		return data;
	}

	@Override
	// 设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "添加分类").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "删除分类").setIcon(
				android.R.drawable.ic_menu_delete);
		return true;
	}

	@Override
	// 菜单按钮点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			buildDialog(CategoryMgrAct.this).show();
			break;
		case Menu.FIRST + 2:
			DeleteCategoryDialog();
			break;
		}
		return false;
	}

	// 添加分类Dialog
	private Dialog buildDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.category, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(R.string.addCategory_alert);
		builder.setView(textEntryView);
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						EditText tvcname = (EditText) textEntryView
								.findViewById(R.id.categoryname_edit);// 获取分类名称
						ContentValues initialValues = new ContentValues();
						initialValues.put("Name", tvcname.getText().toString());
						HashMap<String, String> item = new HashMap<String, String>();
						item.put("Id", dbhelper.Add(dbhelper, "Category",
								initialValues));
						item.put("Name", tvcname.getText().toString());
						data.add(item);
						adapter.notifyDataSetChanged();
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

	// 删除分类
	private void DeleteCategoryDialog() {
		// 设置提示框
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CategoryMgrAct.this);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(R.string.alert);
		builder.setMessage(R.string.del_alert);
		// 点击确定
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView categoryLv = (ListView) findViewById(R.id.categoryLv);
						// 获取所有选中项的ID
						MyAdapter myAdapter = (MyAdapter) categoryLv
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
							dbhelper.Delete(dbhelper, "Category", id);
							dbhelper.ExeSQL(dbhelper,
									"DELETE FROM RssAddr WHERE CategoryId='"
											+ id + "'");// 删除分类下的RSS
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

	// 编辑分类Dialog
	private Dialog buildEditDialog(Context context, final String id, String name) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.category, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert);
		builder.setTitle(R.string.editCategory_alert);
		builder.setView(textEntryView);
		final EditText tvcname = (EditText) textEntryView
				.findViewById(R.id.categoryname_edit);
		tvcname.setText(name);// 设置当前分类名称
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String sql = "UPDATE Category SET Name='"
								+ tvcname.getText().toString() + "' WHERE Id='"
								+ id + "'";
						dbhelper.ExeSQL(dbhelper, sql);
						list = (ListView) findViewById(R.id.categoryLv);
						adapter = new MyAdapter(CategoryMgrAct.this, getData());
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
