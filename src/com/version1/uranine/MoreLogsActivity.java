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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;


public class MoreLogsActivity extends Activity {
	private CornerListView mListView = null;
	public Query db_sl;
	private ImageButton app_back;
	ArrayList<HashMap<String, String>> map_list1 = null;
	private ArrayList<LogInfo> logs=null;
//	private TextView mainInfo;
	public static MyHandler myHandler;
	
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

       requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //define customized title 
       setContentView(R.layout.activity_more_logs); 
       getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.more_logs_title);//assignment of customized layout		

		db_sl = new Query(this);	
		myHandler = new MyHandler();
		logs=db_sl.findAll();

		getDataSource1(logs);



		SimpleAdapter adapter1 = new SimpleAdapter(this, map_list1,
				R.layout.simple_list_item_1, new String[] { "item" },
				new int[] { R.id.item_title });
		mListView = (CornerListView) findViewById(R.id.list1);
		mListView.setAdapter(adapter1);
		mListView.setOnItemClickListener(new OnItemListSelectedListener());
		app_back=(ImageButton) findViewById(R.id.app_back);
		app_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});		
				
	}

	private DBOpenHelper DBOpenHelper(MoreLogsActivity mainPage) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<HashMap<String, String>> getDataSource1( ArrayList<LogInfo> logs) {

		map_list1 = new ArrayList<HashMap<String, String>>();
	
		for(int i=0;i<logs.size();i++){
			HashMap<String, String> map = new HashMap<String, String>();
			String strInfoString="At "+logs.get(i).getLogtime()+"\n"+logs.get(i).getAppname()+" "+logs.get(i).getContent();
			map.put("item", strInfoString);
			map_list1.add(map);
		}
		
		return map_list1;
	}

	class OnItemListSelectedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
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

//				mainInfo.setText("Hello, your last access time: " + logTime+" \n\nYou have instrumented " + appNumber+" app(s). \n" + logNumber+" privacy leakage(s) have been prevented!");		

				logs=db_sl.findAll();

				getDataSource1(logs);

				SimpleAdapter adapter1 = new SimpleAdapter(MoreLogsActivity.this, map_list1,
						R.layout.simple_list_item_1, new String[] { "item" },
						new int[] { R.id.item_title });
				mListView = (CornerListView) findViewById(R.id.list1);
				mListView.setAdapter(adapter1);
				mListView.setOnItemClickListener(new OnItemListSelectedListener());
				
			}
        }
	}
}