package com.version1.uranine.database;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.version1.uranine.database.DBOpenHelper;
import com.version1.uranine.domain.LogInfo;

public class Query {

	private DBOpenHelper helper;


	public Query(Context context) {
		helper = new DBOpenHelper(context);
	}
	/**
	 * add app name
	 * 
	 * @param appname
	 *            
	 * @param status
	 *            
	 */
	public void addApp(String name, int status) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into app (name,status) values (?,?)",
				new Object[] { name,status });
		System.out.println("Insert done");
		db.close();
	}
	
//	/**
//	 * update app status
//	 * 
//	 * @param appname
//	 *            
//	 * @param status
//	 *            
//	 */
//	public void updateApp(String name, int status) {
//		SQLiteDatabase db = helper.getReadableDatabase();
//		Cursor curosr = db.rawQuery("select name from app ",
//				new String[] {});
//		if (curosr.moveToNext()) {
//			db.execSQL("update app set status=? where name=?",
//					new Object[] { status, name });
//		}else{
//			db.execSQL("insert into app (name,status) values (?,?)",
//					new Object[] { name,status });
//		}
//		curosr.close();
//		db.close();
//	}
	
	/**
	 * add log name
	 * 
	 * @param appname
	 *            
	 * @param content
	 *            
	 */
	public void add(String appname, String content) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into log (appname,content) values (?,?)",
				new Object[] { appname,content });
		System.out.println("Insert done");
		db.close();
	}

	/**
	 * find log name 
	 * 
	 * @param appname
	 *            
	 * @return
	 */
	public boolean findLog(String appname) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select * from log where appname=?",
				new String[] { appname });
		if (curosr.moveToNext()) {
			result = true;
		}
		curosr.close();
		db.close();
		return result;
	}
	/**
	 *find app Status
	 * 
	 * @param app
	 *            
	 * @return  stauts
	 * 			0 
	 *			1
	 *			2
	 *			-1
	 */
	public int findStatus(String name) {
		int status  = -1;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select status from app where name=?",
				new String[] { name });
		if (curosr.moveToNext()) {
			status = Integer.parseInt(curosr.getString(0));
		}
		curosr.close();
		db.close();
		return status;
	}
	/**
	 * update app status
	 * @param newmStatus
	 *            
	 * @param name
	 *            
	 */
	public void updateStatus(String newStatus, String name) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update blacknumber set app=? where name=?",
				new Object[] { newStatus, name });
		db.close();
	}

	/**
	 * delete app Status
	 * 
	 * @param number
	 */
	public void deleteStatus(String name) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from app where name=?",
				new Object[] { name });
		db.close();
	}
	
	/**
	 * output all instrumented appname
	 */
	public ArrayList<String> findAllApp() {
		ArrayList<String> appNames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select name from app where status=0",
				new String[] {});
		while (curosr.moveToNext()) {
			String appname = curosr.getString(0);
			appNames.add(appname);
		}
		curosr.close();
		db.close();
		return appNames;
	}

	/**
	 * output appname,content
	 */
	public ArrayList<LogInfo> findAll() {
		ArrayList<LogInfo> logs = new ArrayList<LogInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select appname,content,Datetime(ltime,'localtime') from log ",
				new String[] {});
		while (curosr.moveToNext()) {
			String appname = curosr.getString(0);
			String content = curosr.getString(1);
			String logtime=curosr.getString(2);
			LogInfo bean = new LogInfo();
			bean.setAppname(appname);
			bean.setContent(content);
			bean.setLogtime(logtime);
			logs.add(bean);
			bean = null;
		}
		curosr.close();
		db.close();
		return logs;
	}

	/**
	 * find the first ? Part
	 * 
	 * @param startindex
	 *            
	 * @param maxnum
	 *            
	 * @return
	 */
	public ArrayList<LogInfo> findLogPart(int startindex, int maxnum) {
		ArrayList<LogInfo> logs = new ArrayList<LogInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery(
				"select appname,content from log order by id desc limit ? offset ? ",
				new String[] { String.valueOf(maxnum),
						String.valueOf(startindex) });
		while (curosr.moveToNext()) {
			String appname = curosr.getString(0);
			String content = curosr.getString(1);
			LogInfo bean = new LogInfo();
			bean.setAppname(appname);
			bean.setContent(content);
			logs.add(bean);
			bean = null;
		}
		curosr.close();
		db.close();
		return logs;
	}
	
	public String getLogTime(){
		String logTimestamp=null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select Datetime(ltime,'localtime') from log where id=(select max(id) from log)",null);
		if (cursor.moveToNext()) {
			logTimestamp = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return logTimestamp;
		
	}	
	/**
	 * find num of instrumented apps
	 * @param maxnumber
	 * @return count
	 */
	public int getAppNumber(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select name from app where status=0", null);
		int totalnumber = cursor.getCount();
		cursor.close();
		db.close();
		return totalnumber;
		
	}
	public int getLogNumber(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from log", null);
		int totalnumber = cursor.getCount();
		cursor.close();
		db.close();
		return totalnumber;
		
	}
	public int getTotalPage(int maxnumber){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from app", null);
		int totalnumber = cursor.getCount();
		cursor.close();
		db.close();
		if(totalnumber%maxnumber==0){
			return totalnumber/maxnumber;
		}else{
			return totalnumber/maxnumber + 1;
		}
		
	}
}
