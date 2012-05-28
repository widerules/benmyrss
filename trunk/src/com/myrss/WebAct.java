package com.myrss;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebAct extends Activity {

	private WebView wv;
	private ProgressDialog pd;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webui);
		init();// 执行初始化函数
		Intent startingIntent = getIntent();
		if (startingIntent != null) {
			Bundle bundle = startingIntent
					.getBundleExtra("android.intent.extra.rssItem");
			if (bundle == null) {
				setTitle("不好意思程序出错啦");
			} else {
				setTitle(bundle.getString("title"));
				loadurl(wv, bundle.getString("link"));
			}
		} else {
			setTitle("不好意思程序出错啦");
		}
		handler = new Handler() {
			public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
				if (!Thread.currentThread().isInterrupted()) {
					switch (msg.what) {
					case 0:
						pd.show();// 显示进度对话框
						break;
					case 1:
						pd.hide();// 隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
						break;
					}
				}
				super.handleMessage(msg);
			}
		};
	}

	public void init() {// 初始化
		wv = (WebView) findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true);// 可用JS
		wv.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
		wv.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {
				loadurl(view, url);// 载入网页
				return true;
			}
		});
		wv.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				if (progress == 100) {
					handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框
				}
				super.onProgressChanged(view, progress);
			}
		});

		pd = new ProgressDialog(WebAct.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("数据载入中……");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {// 捕捉返回键
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			WebAct.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void loadurl(final WebView view, final String url) {
		new Thread() {
			public void run() {
				handler.sendEmptyMessage(0);
				view.loadUrl(url);// 载入网页
			}
		}.start();
	}
}