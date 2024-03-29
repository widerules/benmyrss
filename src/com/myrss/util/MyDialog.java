package com.myrss.util;

import java.io.UnsupportedEncodingException;
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

public class MyDialog extends SQLiteOpenHelper {

	private static final String DB_NAME = "Study.db";
	private static final int DB_VERSION = 1;
	private static MyDialog dbHelper = null;

	public MyDialog(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public static MyDialog GetInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new MyDialog(context, DB_NAME, null, DB_VERSION);
		}
		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS RssAddr(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name NVARCHAR(20),URL NVARCHAR(100), CategoryId INTEGER,Flag INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS Category(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name NVARCHAR(20));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// 执行SQL语句
	public boolean ExeSQL(MyDialog dbHelper, String sql) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.execSQL(sql);
			Log.d("MyDebug", "ExeSQL");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	// 插入数据
	public boolean Add(MyDialog dbHelper, String tbname, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			db.insert(tbname, null, values);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	// 根据ID删除数据
	public boolean Delete(MyDialog dbHelper, String tbname, String id) {
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
	public boolean Update(MyDialog dbHelper, String tbname, String id,
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
	public List<Category> GetCategoryList(MyDialog dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Category> list = new ArrayList<Category>();
		try {
			Cursor cursor = db.query("Category", new String[] { "Id", "Name" },
					null, null, null, null, null);
			while (cursor.moveToNext()) {
				Category c = new Category();
				try {
					c.setId(cursor.getString(cursor.getColumnIndex("Id")));
					c.setName(new String(cursor.getBlob(cursor
							.getColumnIndex("Name")), "GBK"));
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
	public List<RSSAddr> GetRSSListByID(MyDialog dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<RSSAddr> list = new ArrayList<RSSAddr>();
		try {
			Cursor cursor = db.query("RssAddr", new String[] { "Id", "Name",
					"CategoryId", "Flag", "URL" }, "CategoryId=?",
					new String[] { id }, null, null, null);
			while (cursor.moveToNext()) {
				RSSAddr c = new RSSAddr();
				c.setId(cursor.getString(cursor.getColumnIndex("Id")));
				c.setName(new String(cursor.getBlob(cursor
						.getColumnIndex("Name")), "GBK"));
				c.setCategoryId(cursor.getString(cursor
						.getColumnIndex("CategoryId")));
				c.setFlag(cursor.getString(cursor.getColumnIndex("Flag")));
				c.setURL(cursor.getString(cursor.getColumnIndex("URL")));
				list.add(c);
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.d("MyDebug", e.getMessage());
		}
		return list;
	}

	// 根据ID获取RSS
	public RSSAddr GetRSSByID(MyDialog dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		RSSAddr r = new RSSAddr();
		try {
			Cursor cursor = db.query("RssAddr", new String[] { "Id", "Name",
					"URL" }, "Id=?", new String[] { id }, null, null, null);
			while (cursor.moveToNext()) {
				r.setId(cursor.getString(cursor.getColumnIndex("Id")));
				r.setName(new String(cursor.getBlob(cursor
						.getColumnIndex("Name")), "GBK"));
				r.setURL(cursor.getString(cursor.getColumnIndex("URL")));
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return r;
	}

	// 获取默认RSS
	public RSSAddr GetDefaultRSS(MyDialog dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		RSSAddr r = new RSSAddr();
		try {
			Cursor cursor = db.query("RssAddr", new String[] { "Id", "Name",
					"URL" }, "Flag=?", new String[] { "1" }, null, null, null);
			while (cursor.moveToNext()) {
				r.setId(cursor.getString(cursor.getColumnIndex("Id")));
				r.setName(new String(cursor.getBlob(cursor
						.getColumnIndex("Name")), "GBK"));
				r.setURL(cursor.getString(cursor.getColumnIndex("URL")));
			}
			cursor.close();
		} catch (SQLException e) {
			Log.d("MyDebug", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return r;
	}
}