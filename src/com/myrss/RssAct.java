package com.myrss;

import java.util.List;

import com.myrss.model.Category;
import com.myrss.model.RSSAddr;
import com.myrss.util.DBHelper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RssAct extends Activity {

	private DBHelper dbhelper = DBHelper.GetInstance(RssAct.this);
	private Boolean IsEdit = false;
	private String sql = null, RssID = null;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.rss);
		initWidget();
		initData();   
	}

	// 初始化组件
	private void initWidget() {
		Button btnOKRss = (Button) findViewById(R.id.btnOKRss);
		Button btnCancelRss = (Button) findViewById(R.id.btnCancelRss);
		btnOKRss.setOnClickListener(btnOKRssListener);
		btnCancelRss.setOnClickListener(btnCancelListener);
	}

	// 初始化数据
	private void initData() {
		List<Category> categoryls = dbhelper.GetCategoryList(dbhelper);
		Spinner spinner = (Spinner) findViewById(R.id.spCategory);
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this,
				android.R.layout.simple_spinner_item, categoryls);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			RssID = (String) bundle.get("RssID");
			IsEdit =Boolean.valueOf((String) bundle.get("IsEdit"));
			EditText tvrssname = (EditText) findViewById(R.id.rssname_edit);
			EditText rssaddr = (EditText) findViewById(R.id.rssaddr_edit);
			RSSAddr rssAddr = dbhelper.GetRSSByID(dbhelper, RssID);
			tvrssname.setText(rssAddr.getName());
			rssaddr.setText(rssAddr.getURL());
			for (int i = 0; i < spinner.getCount(); i++) {
				Category category = (Category) spinner.getItemAtPosition(i);
				if (category.getId().equals(rssAddr.getCategoryId())) {
					spinner.setSelection(i);
					break;
				}
			}
		}
	}

	// 添加RSS源
	private Button.OnClickListener btnOKRssListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			EditText tvrssname = (EditText) findViewById(R.id.rssname_edit);
			EditText rssaddr_edit = (EditText) findViewById(R.id.rssaddr_edit);// 获取RSS地址
			Spinner spinner = (Spinner) findViewById(R.id.spCategory);
			String categoryId = ((Category) spinner.getSelectedItem()).getId();// 获取分类ID

			if (true == IsEdit) {// 编辑更新数据
				String tString=tvrssname.getText().toString();
				sql = "UPDATE RssAddr SET Name='" +tString
						+ "',URL='" + rssaddr_edit.getText() + "',CategoryId='"
						+ categoryId + "' WHERE Id='" + RssID + "'";
			} else {
				sql = "INSERT INTO RssAddr(Name,URL,CategoryId,Flag) "
						+ "VALUES('" + tvrssname.getText() + "','"
						+ rssaddr_edit.getText() + "','" + categoryId
						+ "','0')";
			}
			Log.d("MyDebug", sql);
			dbhelper.ExeSQL(dbhelper, sql);
			finish();
		}
	};

	// 取消
	private Button.OnClickListener btnCancelListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
}