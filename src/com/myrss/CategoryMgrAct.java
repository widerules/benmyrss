package com.myrss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myrss.model.Category;
import com.myrss.util.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class CategoryMgrAct extends Activity {

	private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter = null;
	private DBHelper dbhelper = DBHelper.GetInstance(CategoryMgrAct.this);

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.categorymgrui);
		initData();
	}

	// 初始化数据
	private void initData() {
		final ListView categoryLv = (ListView) findViewById(R.id.categoryLv);
		List<Category> lsCategories = dbhelper.GetCategoryList(dbhelper);
		data.clear();
		for (Category c : lsCategories) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("Id", c.getId());
			item.put("Name", c.getName());
			data.add(item);
		}
		adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_multiple_choice,
				new String[] { "Name" }, new int[] { android.R.id.text1 }) {
			@Override
			public long getItemId(int pos) {
				long id = Long.parseLong(data.get(pos).get("Id"));
				return id;
			}
		};
		categoryLv.setAdapter(adapter);
		categoryLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		for (int i = 0; i < categoryLv.getChildCount(); i++) {
			CheckBox chk = (CheckBox) categoryLv
					.findViewById(android.R.id.checkbox);
			chk.setFocusable(false);
			chk.setClickable(false);
			chk.setFocusableInTouchMode(false);
		}

		// 单击条目
		categoryLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Bundle value = new Bundle();
				SparseBooleanArray sbArray = categoryLv
						.getCheckedItemPositions();
				for (int i = 0; i < data.size(); i++) {
					if (sbArray.valueAt(i)) {
						String id = String.valueOf(categoryLv.getAdapter()
								.getItemId(sbArray.keyAt(i)));
						value.putString("categoryid", id);
						intent.putExtra("android.intent.extra.categoryid",
								value);
						intent.setClass(CategoryMgrAct.this, RssMgrAct.class);
						startActivity(intent);
					}
				}
			}
		});
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
						item.put("Id", dbhelper.Add(dbhelper, "Category", initialValues));
						item.put("Name",tvcname.getText().toString());
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
	
	@Override
	// 设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "添加分类").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "编辑分类").setIcon(
				android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, "删除分类").setIcon(
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
			EditCategoryDialog();
			break;
		case Menu.FIRST + 3:
			DeleteCategoryDialog();
			break;
		}
		return false;
	}
	
	//编辑分类
	private void EditCategoryDialog() {
		ListView categoryLv = (ListView) findViewById(R.id.categoryLv);
		SparseBooleanArray sbArray = categoryLv.getCheckedItemPositions();
		for (int i = 0; i < data.size(); i++) {
			if (sbArray.valueAt(i)) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> c = (HashMap<String, String>) categoryLv
						.getAdapter().getItem(sbArray.keyAt(i));
				buildEditDialog(CategoryMgrAct.this, c.get("Id"), c.get("Name"))// 获取选中项并传递数据
						.show();
			}
		}
	}
	
	//删除分类
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
						SparseBooleanArray sbArray = categoryLv
								.getCheckedItemPositions();
						for (int i = 0; i < data.size(); i++) {
							if (sbArray.valueAt(i)) {
								String id = String.valueOf(categoryLv
										.getAdapter().getItemId(
												sbArray.keyAt(i)));
								dbhelper.Delete(dbhelper, "Category", id);// 删除RSS
								dbhelper.ExeSQL(dbhelper,
										"DELETE FROM RssAddr WHERE CategoryId='"
												+ id + "'");// 删除分类下的RSS
								data.remove(i);//从list中移除
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
}