package com.myrss;

import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.myrss.R;
import com.myrss.model.RSSAddr;
import com.myrss.model.RSSFeed;
import com.myrss.model.RSSItem;
import com.myrss.sax.RSSHandler;
import com.myrss.util.DBHelper;

public class MainAct extends Activity implements OnItemClickListener {

	private RSSFeed feed = null;
	private RSSAddr rssAddr = null;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mainui);
		initWidget();
		showListView("");// 加载默认RSS
	}

	// 初始化组件
	private void initWidget() {
		Button btnMgr = (Button) findViewById(R.id.btnMgr);
		Button btnSel = (Button) findViewById(R.id.btnSel);
		Button btnDel = (Button) findViewById(R.id.btnDel);
		Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
		Button btnDefault = (Button) findViewById(R.id.btnDefault);
		btnMgr.setOnClickListener(btnMgrListener);
		btnSel.setOnClickListener(btnSelListener);
		btnDel.setOnClickListener(btnDelListener);
		btnDefault.setOnClickListener(btnDefaultListener);
		btnRefresh.setOnClickListener(btnRefreshListener);
	}

	// 管理RSS
	private Button.OnClickListener btnMgrListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainAct.this, CategoryMgrAct.class);
			startActivity(intent);
		}
	};
	
	// 选择RSS
	private Button.OnClickListener btnSelListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainAct.this, SelRSSAct.class);
			startActivityForResult(intent, 0);// 获取有返回结果的activity
		}
	};
	
	// 删除RSS
	private Button.OnClickListener btnDelListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (feed == null) {
				return;
			}
			// 设置提示框
			AlertDialog.Builder builder = new AlertDialog.Builder(MainAct.this);
			builder.setIcon(R.drawable.alert);
			builder.setTitle(R.string.alert);
			builder.setMessage(R.string.del_alert);
			// 点击确定
			builder.setPositiveButton(R.string.btn_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DBHelper dbHelper = DBHelper
									.GetInstance(MainAct.this);
							if (dbHelper.Delete(dbHelper, "RssAddr",
									feed.getId())) {// 根据ID删除RSS
								showListView("");
							} else {
								setTitle("不好意思程序出错啦");
							}
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
	};
	
	// 设置默认RSS
	private Button.OnClickListener btnDefaultListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (feed == null) {
				return;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(MainAct.this);
			builder.setIcon(R.drawable.alert);
			builder.setTitle(R.string.alert);
			builder.setMessage(R.string.default_alert);
			// 点击确定
			builder.setPositiveButton(R.string.btn_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DBHelper dbHelper = DBHelper
									.GetInstance(MainAct.this);
							// 首先清空所有的RSS flag标记 然后设置当前的RSS flag为1
							String clearsql = "UPDATE RssAddr SET Flag='0'";
							dbHelper.ExeSQL(dbHelper, clearsql);
							String sql = "UPDATE RssAddr SET Flag='1' WHERE Id="
									+ feed.getId();
							dbHelper.ExeSQL(dbHelper, sql);
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
	};
	
	// 刷新列表
	private Button.OnClickListener btnRefreshListener = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			showListView("");
		}
	};

	// 获取数据
	private RSSFeed getFeed(String urlString) {
		try {
			URL url = new URL(urlString);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();

			RSSHandler rssHandler = new RSSHandler();
			xmlreader.setContentHandler(rssHandler);

			InputSource is = new InputSource(url.openStream());
			xmlreader.parse(is);

			return rssHandler.getFeed();
		} catch (Exception ee) {
			return null;
		}
	}

	// 显示列表
	private void showListView(String id) {
		DBHelper dbhelper = DBHelper.GetInstance(MainAct.this);
		if (id.equals("")) {// id为空获取默认RSS
			rssAddr = dbhelper.GetDefaultRSS(dbhelper);
		} else {// 不为空获取指定ID的RSS
			rssAddr = dbhelper.GetRSSByID(dbhelper, id);
		}
		String url = rssAddr.getURL();
		Log.d("MyDebug", "url: " + url);
		feed = getFeed(url);
		ListView itemlist = (ListView) findViewById(R.id.RssList);
		if (feed == null) {
			setTitle("RSS源地址无效,请您重新选择");
			itemlist.setAdapter(null);
			return;
		}
		feed.setId(rssAddr.getId());
		setTitle(rssAddr.getName());
		SimpleAdapter adapter = new SimpleAdapter(this,
				feed.getAllItemsForListView(),
				android.R.layout.simple_list_item_2, new String[] {
						RSSItem.TITLE, RSSItem.PUBDATE }, new int[] {

				android.R.id.text1, android.R.id.text2 });
		itemlist.setAdapter(adapter);
		itemlist.setOnItemClickListener(this);
		itemlist.setSelection(0);
	}

	// 详细页面
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent itemintent = new Intent(this, WebAct.class);
		Bundle b = new Bundle();
		b.putString("link", feed.getItem(position).getLink());
		b.putString("title", feed.getItem(position).getTitle());
		itemintent.putExtra("android.intent.extra.rssItem", b);// 设置activity的数据
		startActivityForResult(itemintent, 0);
	}

	// 获取选择的RSS
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) { // 根据状态码，获取返回结果
		case RESULT_OK:
			Bundle bundle = data.getExtras(); // 获取intent里面的bundle对象
			String id = (String) bundle.get("ID");
			showListView(id);
			break;
		default:
			break;
		}
	}

	@Override
	// 设置菜单
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST + 1, 5, "退出").setIcon(
				android.R.drawable.ic_menu_delete);
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "刷新").setIcon(
				android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu.FIRST + 3, 6, "帮助").setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, Menu.FIRST + 4, 1, "添加").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST + 5, 4, "详细").setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, Menu.FIRST + 6, 3, "分享").setIcon(
				android.R.drawable.ic_menu_send);
		return true;
	}

	@Override//菜单按钮点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			dialog();
			break;
		case Menu.FIRST + 2:
			this.showListView("");
			break;
		case Menu.FIRST + 3:
			Toast.makeText(this, "帮助菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 4:
			Toast.makeText(this, "添加菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 5:
			Toast.makeText(this, "详细菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 6:
			Toast.makeText(this, "分享菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		}
		return false;
	}

	//退出提示框
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(MainAct.this);
		builder.setMessage("您确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}