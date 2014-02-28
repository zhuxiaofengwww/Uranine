package com.version1.uranine;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	ImageButton imageButton1,imageButton2,imageButton3,imageButton4; 
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageButton1 = (ImageButton)findViewById(R.id.ImageButton01);
		imageButton2 = (ImageButton)findViewById(R.id.ImageButton02);
		imageButton3 = (ImageButton)findViewById(R.id.ImageButton03);
		imageButton4 = (ImageButton)findViewById(R.id.ImageButton04);
		
		imageButton1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MainActivity.this,AppManagerActivity.class);
				startActivity(intent);
			}
		});	
		imageButton2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});	
		imageButton3.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

			}
		});	
		imageButton4.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});	
		
		
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
		Intent activityIntent = new Intent(MainActivity.this,AppManagerActivity.class);
	
		PendingIntent intent = PendingIntent.getActivity(this, 0,
				activityIntent, 0);
		buidler.setContentIntent(intent);
		Notification notification = buidler.getNotification();
		// 3. define the content of a notification
		// notification.setLatestEventInfo(context, contentTitle, contentText,
		// contentIntent);
		nm.notify(0, notification);	
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
