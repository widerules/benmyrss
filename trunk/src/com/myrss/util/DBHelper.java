package com.myrss.util;

import java.util.ArrayList;
import java.util.List;

import com.myrss.model.Category;
import com.myrss.model.RSSAddr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "Study.db";
	private static final int DB_VERSION = 1;
	private static DBHelper dbHelper = null;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public static DBHelper GetInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
		}
		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS RssAddr(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name NVARCHAR(20),URL NVARCHAR(100), CategoryId INTEGER,Flag INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS Category(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name NVARCHAR(20));");

		db.execSQL("INSERT INTO Category(Name) VALUES('IT世界');");
		db.execSQL("INSERT INTO Category(Name) VALUES('财经');");
		db.execSQL("INSERT INTO Category(Name) VALUES('时政要闻');");
		db.execSQL("INSERT INTO Category(Name) VALUES('技术博客');");
		db.execSQL("INSERT INTO Category(Name) VALUES('英语学习');");
		db.execSQL("INSERT INTO Category(Name) VALUES('读书文娱');");

		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('Android中文','http://rss.tgbus.com/android.xml','1','1');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('CSDN资讯','http://articles.csdn.net/api/rss.php?tid=1008','1','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('每日英语','http://english-feed.beanwoo.com','1','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('婚姻物语','http://rss.sina.com.cn/eladies/qinggan/hunyin50.xml','2','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('分享热点','http://www.sahot.com/feed','2','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('FT中文网','http://www.ftchinese.com/rss/column/007000004','3','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('南方周末','http://www.infzm.com/rss/home/rss2.0.xml','3','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('联合早报','http://www.zaobao.com/yl/yl.xml','4','0');");
		db.execSQL("INSERT INTO RssAddr(Name,URL,CategoryId,Flag) VALUES('豆瓣','http://www.douban.com/feed/review/book','5','0');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS RssAddr");
		db.execSQL("DROP TABLE IF EXISTS Category");
		onCreate(db);
	}

	// 执行SQL语句
	public boolean ExeSQL(DBHelper dbHelper, String sql) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.execSQL(sql);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	// 插入数据
	public String Add(DBHelper dbHelper, String tbname, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			long rowid = db.insert(tbname, null, values);
			return String.valueOf(rowid);
		} catch (SQLException e) {
			return "-1";
		}
	}

	// 根据ID删除数据
	public boolean Delete(DBHelper dbHelper, String tbname, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.delete(tbname, "Id=?", new String[] { id });
			return true;
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
			return false;
		}
	}

	// 更新数据
	public boolean Update(DBHelper dbHelper, String tbname, String id,
			ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.update(tbname, values, "Id=?", new String[] { id });
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	// 获取分类列表
	public List<Category> GetCategoryList(DBHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Category> list = new ArrayList<Category>();
		try {
			Cursor cursor = db.query("Category", new String[] { "Id", "Name" },
					null, null, null, null, null);
			while (cursor.moveToNext()) {
				Category c = new Category();
				try {
					c.setId(cursor.getString(cursor.getColumnIndex("Id")));
					c.setName(cursor.getString(cursor.getColumnIndex("Name")));
				} catch (Exception e) {
					Log.d("MyDebug", e.getMessage());
				}
				list.add(c);
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		}
		return list;
	}

	// 根据分类ID获取RSS地址列表
	public List<RSSAddr> GetRSSListByID(DBHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<RSSAddr> list = new ArrayList<RSSAddr>();
		try {
			Cursor cursor = db.query("RssAddr", new String[] { "Id", "Name",
					"CategoryId", "Flag", "URL" }, "CategoryId=?",
					new String[] { id }, null, null, null);
			while (cursor.moveToNext()) {
				RSSAddr c = new RSSAddr();
				c.setId(cursor.getString(cursor.getColumnIndex("Id")));
				c.setName(cursor.getString(cursor.getColumnIndex("Name")));
				c.setCategoryId(cursor.getString(cursor
						.getColumnIndex("CategoryId")));
				c.setFlag(cursor.getString(cursor.getColumnIndex("Flag")));
				c.setURL(cursor.getString(cursor.getColumnIndex("URL")));
				list.add(c);
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		}
		return list;
	}

	// 根据ID获取RSS
	public RSSAddr GetRSSByID(DBHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		RSSAddr r = new RSSAddr();
		try {
			Cursor cursor = db.query("RssAddr", new String[] { "Id", "Name",
					"URL", "CategoryId" }, "Id=?", new String[] { id }, null,
					null, null);
			while (cursor.moveToNext()) {
				r.setId(cursor.getString(cursor.getColumnIndex("Id")));
				r.setName(cursor.getString(cursor.getColumnIndex("Name")));
				r.setURL(cursor.getString(cursor.getColumnIndex("URL")));
				r.setCategoryId(cursor.getString(cursor
						.getColumnIndex("CategoryId")));
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		}
		return r;
	}

	// 获取默认RSS
	public RSSAddr GetDefaultRSS(DBHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		RSSAddr r = new RSSAddr();
		try {
			Cursor cursor = db.query("RssAddr", new String[] { "Id", "Name",
					"URL" }, "Flag=?", new String[] { "1" }, null, null, null);
			while (cursor.moveToNext()) {
				r.setId(cursor.getString(cursor.getColumnIndex("Id")));
				r.setName(cursor.getString(cursor.getColumnIndex("Name")));
				r.setURL(cursor.getString(cursor.getColumnIndex("URL")));
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		}
		return r;
	}
}