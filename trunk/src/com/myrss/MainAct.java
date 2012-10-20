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
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.myrss.model.RSSAddr;
import com.myrss.model.RSSFeed;
import com.myrss.model.RSSItem;
import com.myrss.sax.RSSHandler;
import com.myrss.util.DBHelper;

public class MainAct extends Activity implements OnItemClickListener,
		OnGestureListener {

	private RSSFeed feed = null;
	private RSSAddr rssAddr = null;
	private GestureDetector detector = null;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mainui);
		detector = new GestureDetector(this);
		showListView("");
	}

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
		if(rssAddr.getURL()!=null)
		{	
			String url = rssAddr.getURL();
			Log.d("MyDebug", "url: " + url);
			feed = getFeed(url);
		}else {
			setTitle("请选择您要阅读的RSS");
			return;
		}
		ListView itemlist = (ListView) findViewById(R.id.RssList);
		if (feed == null) {
			setTitle("RSS源地址无效,请您重新选择");
			return;
		}

		feed.setId(rssAddr.getId());
		setTitle(rssAddr.getName() + "(" + feed.getItemCount() + "条待阅读)");
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
		menu.add(Menu.NONE, Menu.FIRST + 3, 6, "删除").setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, Menu.FIRST + 4, 1, "管理").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, Menu.FIRST + 5, 4, "默认").setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, Menu.FIRST + 6, 3, "选择").setIcon(
				android.R.drawable.ic_menu_send);
		return true;
	}

	@Override
	// 菜单按钮点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			dialog();
			break;
		case Menu.FIRST + 2:
			this.showListView("");
			break;
		case Menu.FIRST + 3:
			DeleteDialog();
			break;
		case Menu.FIRST + 4:
			Intent intent = new Intent();
			intent.setClass(MainAct.this, CategoryMgrAct.class);
			startActivity(intent);
			break;
		case Menu.FIRST + 5:
			SetDefaultDialog();
			break;
		case Menu.FIRST + 6:
			SelectRssDialog();
			break;
		}
		return false;
	}

	// 选择RSS
	private void SelectRssDialog() {
		Intent intent = new Intent();
		intent.setClass(MainAct.this, SelRSSAct.class);
		startActivityForResult(intent, 0);// 获取有返回结果的activity
	}

	// 设置默认RSS
	private void SetDefaultDialog() {
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
						DBHelper dbHelper = DBHelper.GetInstance(MainAct.this);
						// 首先清空所有的RSS flag标记 然后设置当前的RSS flag为1
						String clearsql = "UPDATE RssAddr SET Flag='0' WHERE CategoryId='"
								+ rssAddr.getCategoryId() + "'";
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

	// 删除RSS
	private void DeleteDialog() {
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
						DBHelper dbHelper = DBHelper.GetInstance(MainAct.this);
						if (dbHelper.Delete(dbHelper, "RssAddr", feed.getId())) {// 根据ID删除RSS
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

	// 退出提示框
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

	@Override
	public boolean onDown(MotionEvent e) {
		//Toast.makeText(getApplicationContext(), "向下滑动屏幕",Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 200) {
			this.startActivity(new Intent(this, SelRSSAct.class));
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		//Toast.makeText(getApplicationContext(), "长时间触摸屏幕",Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		//Toast.makeText(getApplicationContext(), "滚屏",Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		this.detector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

}