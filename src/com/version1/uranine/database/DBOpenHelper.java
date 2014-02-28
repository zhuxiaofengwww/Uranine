package com.version1.uranine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
*
*
*
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "uranine.db", null, 1);
		/*
		 * param1: path of database file
		 * param2: name of db
		 * param3: null presents default factory
		 * param4: update new version if possible 
		 */
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("onCreate");
//		db.execSQL("CREATE TABLE app(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(20),status INTEGER(5))");
//		db.execSQL("INSERT INTO app (name,status) values('com.xiaofeng.uranine','0')");		
//		db.execSQL("CREATE TABLE log(id INTEGER PRIMARY KEY AUTOINCREMENT, appname VARCHAR(30),content VARCHAR(200))");
//		
		db.execSQL("CREATE TABLE app(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(20),status INTEGER(5), atime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
		db.execSQL("INSERT INTO app (name,status) values('com.xiaofeng.uranine',0)");		
		db.execSQL("CREATE TABLE log(id INTEGER PRIMARY KEY AUTOINCREMENT, appname VARCHAR(30),content VARCHAR(200), ltime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

		System.out.println("Database Create done");
		db.execSQL("INSERT INTO log (appname,content) values('com.www.helloworld','attempt to upload a document invalidly')");
		db.execSQL("INSERT INTO log (appname,content) values('com.today.helloworld','attempt to upload a document invalidly')");		
		db.execSQL("INSERT INTO log (appname,content) values('gmail','uploading files')");
		db.execSQL("INSERT INTO log (appname,content) values('twitter','reading calendar')");
		db.execSQL("INSERT INTO log (appname,content) values('facebook','fetch contact info')");
		db.execSQL("INSERT INTO log (appname,content) values('QQ','sharing photos')");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onUpgrade");
	}

}
