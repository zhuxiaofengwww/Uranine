package com.version1.uranine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.version1.uranine.AppManagerActivity.MyHandler;
import com.version1.uranine.database.DBOpenHelper;

import com.version1.uranine.database.Query;
import com.version1.uranine.domain.AppInfo;
import com.version1.uranine.domain.LogInfo;
import com.version1.uranine.engine.AppInfoProvider;


import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class MainPage extends Activity {
	private CornerListView mListView = null;
	public Query db_sl;

	ArrayList<HashMap<String, String>> map_list1 = null;
	private ArrayList<LogInfo> logs=null;
	private TextView mainInfo;
	public static MyHandler myHandler;
	
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		mainInfo= (TextView) findViewById(R.id.mainInfoView);
	
		db_sl = new Query(this);	
		myHandler = new MyHandler();
		
		ArrayList<String> instrumentedAppNames = db_sl.findAllApp();
		List<AppInfo> appinfos= new ArrayList<AppInfo>();
		appinfos = AppInfoProvider
				.getAppInfos(getApplicationContext());		
		boolean appExists = false;
		for(String appNames:instrumentedAppNames){
			System.out.println("Instrumented app: "+appNames);
			appExists = false;
			for (AppInfo info : appinfos) {
				if(info.isUserapp()&&info.getName().equalsIgnoreCase(appNames)){
					appExists = true;
				}
			}
			if(appExists==false){
				db_sl.deleteStatus(appNames);
			}
		}
		
		int appNumber=db_sl.getAppNumber();
		int logNumber=db_sl.getLogNumber();	
	
		String logTime=db_sl.getLogTime();
		System.out.println("logTime"+logTime);


		mainInfo.setText("Hello, your last access time: " + logTime+" \n\nYou have instrumented " + appNumber+" app(s). \n" + logNumber+" privacy leakage(s) have been prevented!");		

		logs=db_sl.findLogPart(0,5);

		getDataSource1(logs);



		SimpleAdapter adapter1 = new SimpleAdapter(this, map_list1,
				R.layout.simple_list_item_1, new String[] { "item" },
				new int[] { R.id.item_title });
		mListView = (CornerListView) findViewById(R.id.list1);
		mListView.setAdapter(adapter1);
		mListView.setOnItemClickListener(new OnItemListSelectedListener());
		
		// 1. Get a reference to the NotificationManager:
		//Instantiate a notification service
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// 2. create a notification instance
		Notification.Builder buidler = new Builder(this);
		buidler.setSmallIcon(R.drawable.icon);
		buidler.setTicker("Uranine is running");
		buidler.setWhen(System.currentTimeMillis());
		buidler.setContentTitle("Uranine");
		buidler.setContentText("Uranine has been protecting your phone");		
		// clear the notification
		buidler.setAutoCancel(true);
		buidler.setLights(255,255,0);
		Intent activityIntent = new Intent(this,MainTabActivity.class);
	
		PendingIntent intent = PendingIntent.getActivity(this, 0,
				activityIntent, 0);
		buidler.setContentIntent(intent);
		Notification notification = buidler.getNotification();
		// 3. define the content of a notification
		// notification.setLatestEventInfo(context, contentTitle, contentText,
		// contentIntent);
		nm.notify(0, notification);	
				
	}
    public void onBackPressed() {  
  	   new AlertDialog.Builder(MainPage.this)
  	   	   .setTitle("Do you really want to close Uranine?")  
  	       .setIcon(android.R.drawable.ic_dialog_info)  
  	       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {  
  	     
  	           @Override  
  	           public void onClick(DialogInterface dialog, int which) {  
  	           // Bye
  	        	   MainPage.this.finish();  
  	     
  	           }  
  	       })  
  	       .setNegativeButton("Return", new DialogInterface.OnClickListener() {  
  	     
  	           @Override  
  	           public void onClick(DialogInterface dialog, int which) {  
  	           // return back to Uranine  
  	           }  
  	       }).show();  
  	   // super.onBackPressed();  
  	} 

	private DBOpenHelper DBOpenHelper(MainPage mainPage) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<HashMap<String, String>> getDataSource1( ArrayList<LogInfo> logs) {

		map_list1 = new ArrayList<HashMap<String, String>>();
	
		for(int i=0;i<3;i++){
			HashMap<String, String> map = new HashMap<String, String>();
			String strInfoString=logs.get(i).getAppname()+" tried "+logs.get(i).getContent();
			if (strInfoString.length()>33)
				strInfoString=strInfoString.substring(0, 30)+"...";
			map.put("item", strInfoString);
			map_list1.add(map);
		}
		
		HashMap<String, String> map4 = new HashMap<String, String>();

		map4.put("item", "More ...");

		map_list1.add(map4);		

		
		return map_list1;
	}

	class OnItemListSelectedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			if (arg2 == 3) {
				System.out.println("3");
				Intent intent=new Intent(MainPage.this, MoreLogsActivity.class);
				startActivity(intent);
				
			}else{
				System.out.println("0,1,2");
			}
		}
	}
	
	
	/**
     * 接受消息,处理消息 ,此Handler会与当前主线程一块运行
     * */
    public class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法,接受数据
        @SuppressLint("NewApi")
		@Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle b = msg.getData();
            String function = b.getString("function");
            Log.d("Myhandler",function);
			if(function.equalsIgnoreCase("refresh_page")){
				int appNumber=db_sl.getAppNumber();
				int logNumber=db_sl.getLogNumber();	
			
				String logTime=db_sl.getLogTime();
				System.out.println("logTime"+logTime);

				mainInfo.setText("Hello, your last access time: " + logTime+" \n\nYou have instrumented " + appNumber+" app(s). \n" + logNumber+" privacy leakage(s) have been prevented!");		

				logs=db_sl.findLogPart(0,10);

				getDataSource1(logs);

				SimpleAdapter adapter1 = new SimpleAdapter(MainPage.this, map_list1,
						R.layout.simple_list_item_1, new String[] { "item" },
						new int[] { R.id.item_title });
				mListView = (CornerListView) findViewById(R.id.list1);
				mListView.setAdapter(adapter1);
				mListView.setOnItemClickListener(new OnItemListSelectedListener());
				
				// 1. Get a reference to the NotificationManager:
				//Instantiate a notification service
				NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

				// 2. create a notification instance
				Notification.Builder buidler = new Builder(MainPage.this);
				buidler.setSmallIcon(R.drawable.icon);
				buidler.setTicker("Uranine is running");
				buidler.setWhen(System.currentTimeMillis());
				buidler.setContentTitle("Uranine");
				buidler.setContentText("Uranine has been protecting your phone");		
				// clear the notification
				buidler.setAutoCancel(true);
				buidler.setLights(255,255,0);
				Intent activityIntent = new Intent(MainPage.this,MainTabActivity.class);
			
				PendingIntent intent = PendingIntent.getActivity(MainPage.this, 0,
						activityIntent, 0);
				buidler.setContentIntent(intent);
				Notification notification = buidler.getNotification();
				// 3. define the content of a notification
				// notification.setLatestEventInfo(context, contentTitle, contentText,
				// contentIntent);
				nm.notify(0, notification);	
			}
        }
	}
}