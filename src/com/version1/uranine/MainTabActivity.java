package com.version1.uranine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.version1.uranine.database.DBOpenHelper;
import com.version1.uranine.database.Query;
import com.version1.uranine.domain.AppInfo;
import com.version1.uranine.engine.AppInfoProvider;
import com.version1.uranine.utils.LeakageDetectThread;
import com.version1.uranine.utils.LeakageReportThread;

import android.R.integer;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.Toast;
import android.os.Bundle;  
import android.os.Handler;  
import android.os.Looper;
import android.os.Message; 
import android.provider.Settings;
@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {
	private Process mLogcatProc = null;
	private BufferedReader reader = null;
    private  String content="";
    private ActivityManager mActivityManager;
    private Query db_sl;
    
    //write by guanyu, receive info from thread
    public static MyHandler myHandler;
	String deviceId = "";
	final String ourPackageName = "com.version1.uranine";
    
//send from instrumented apps
//	
//	public static final String ACTION_INTENT_TEST = "uranine.tracking.LEAK";
	@SuppressWarnings("deprecation")
	@Override
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHost tabHost = getTabHost();
		
		LayoutInflater.from(this).inflate(R.layout.activity_main_tab, tabHost.getTabContentView(),true);
		tabHost.addTab(tabHost.newTabSpec("tab01").setIndicator("Main page").setContent(new Intent(this,MainPage.class)));
		tabHost.addTab(tabHost.newTabSpec("tab02").setIndicator("App Manager").setContent(new Intent(this,AppManagerActivity.class)));
//		tabHost.addTab(tabHost.newTabSpec("tab03").setIndicator("User Setting").setContent(new Intent(this,SettingCenterActivity.class)));

//send from instrumented apps		
//		Intent intent = new Intent(ACTION_INTENT_TEST);
//		intent.putExtra("message","HelloWoreld!");
//        sendBroadcast(intent);
		

		myHandler = new MyHandler();
		deviceId = getDeviceId();
		Log.d("MainTab",deviceId);
		
        
		try
        {
        	LeakageDetectThread leakageDetectThread = new LeakageDetectThread();
			new Thread(leakageDetectThread).start();
        }
//        
//        		System.out.println("****************************************");
//                mLogcatProc = Runtime.getRuntime().exec(new String[]
//                		{"logcat", "-d","-v","process","Ads:I *:S"});
//                reader = new BufferedReader(new InputStreamReader
//                		(mLogcatProc.getInputStream()));
//
//                String line;
//
////              final StringBuilder logger = new StringBuilder();
//                String separator = System.getProperty("line.separator"); 
//        		HashMap<String, String> map = new HashMap<String, String>();
////        		String regex = "I \\(.+?\\) adRequestUrlHtml: <html><head><script src=\"(.+?)\"></script><script>AFMA_buildAdURL\\(\\{(.+?)\\}\\);</script></head><body></body></html>"; 		
//        		String regex = "I\\(.+?\\) adRequestUrlHtml: <html><head><script src=\"(.+?)\"></script><script>AFMA_buildAdURL\\(\\{(.+?)\\}\\);</script></head>.+?\\(Ads\\)"; 		
//        	    		        		
//                while ((line = reader.readLine()) != null)
//                {
////        			System.out.println("---------------------"+line);
//
//            		Pattern pattern = Pattern.compile(regex);
//            		Matcher matcher = pattern.matcher(line);
//
//            		if (matcher.matches()) {
//            			
//            			String source = matcher.group(1);
////            			System.out.println(source); 
//            			String destination=source.substring(0, source.lastIndexOf("/"));
//            			System.out.println(destination);    	
//            			
//            			String group = matcher.group(2);
////						System.out.println(group);
//						String[] str1 = group.split(",\"");
//					    for(int i=0; i<str1.length;i++){
//						  String hashcontent[]=str1[i].replace("\"",""  ).split(":");
//					      String title=hashcontent[0];
//					      String content=hashcontent[1];
//					      map.put(title, content);  
//
//        		      }
//        		      String app_name=map.get("msid");
//        		      System.out.println(app_name);    
//        		      if(app_name!=null){
//        		    	  mActivityManager= (ActivityManager) MainTabActivity.this.getSystemService(ACTIVITY_SERVICE);
//        		    	  db_sl = new Query(this);
//        		    	  db_sl.add(app_name,destination);
//        		    	  showDialog(app_name);
//        		    	  
//        		      }
//          			
//            		}
////        			mActivityManager.killBackgroundProcesses("logos.quiz.companies.game");             		
////                			Log.i("LOG_TAG", "logcat"+line+separator);  
////                    		logger.append(line);
////                    		logger.append(separator);                			
////                			content+=logger.toString();
//                }
//                // do whatever you want with the log.  I'd recommend using Intents to create an email
//        }
//
//        catch (IOException e)
//        {
//                
//        }

        finally
        {
                if (reader != null)
                        try
                        {
                                reader.close();
                        }
                        catch (IOException e)
                        {
                                
                        }

        } 
        
//        System.out.println("content"+content);        
//        myReceiver = new MyReceiver() { 
//        	 
//            @Override 
//            public void onReceive(Context context, Intent intent) { 
//                Toast.makeText(context, "myReceiver receive", Toast.LENGTH_SHORT) 
//                        .show(); 
//                showdialog(MainTabActivity.this);
//            }     
//        };  
//        IntentFilter filter = new IntentFilter(); 
//        filter.addAction(ACTION_INTENT_TEST); 
//        filter.setPriority(Integer.MAX_VALUE); 
//        registerReceiver(myReceiver, filter);     

        
	}
	
    private void showDialog(final String apkString, final String appRealName, final String leakageType){  
        new AlertDialog.Builder(this).setTitle("Are you sure to allow this privacy leakage?")  
            .setMessage(appRealName+" try leaked type: "+leakageType)  
            .setNegativeButton("deny",new DialogInterface.OnClickListener(){ 
            	
                public void onClick(DialogInterface dialog, int which){
                	mActivityManager.killBackgroundProcesses(apkString); 
                }  
            }) 
            .setPositiveButton("Allow",

	              new android.content.DialogInterface.OnClickListener() {

	                  @Override

	                  public void onClick(DialogInterface dialog, int which) {

	                      dialog.dismiss();


	                  }

	              })            
            .show();  
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
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle b = msg.getData();
            String function = b.getString("function");
            Log.d("Myhandler",function);
			if(function.equalsIgnoreCase("report_leakage")){
				int pid = b.getInt("pid");
				String leakageType = b.getString("leakageType");
				String destination = b.getString("destination");
				String appName = null;
				String appVersion = null;
				String appRealName = null;
				mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
				List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager  
		                .getRunningAppProcesses(); 
				for(ActivityManager.RunningAppProcessInfo apl:appProcessList){
					//Log.e("RunningAppInfo","pid: "+apl.pid+" processname: "+apl.processName);
					if(pid==apl.pid){
						appName=apl.processName;
						break;
					}
					//Log.e("RunningAppInfo","pid: "+apl.pid+" processname: "+apl.processName);
					//Log.e("RunningAppInfo","pid: "+apl.pkgList.toString());
				}
				
				if(appName!=null){
					List<AppInfo> appinfos = AppInfoProvider
							.getAppInfos(getApplicationContext());
					for (AppInfo info : appinfos) {
						if(appName.contains(info.getPackname())||info.getPackname().contains(appName)){
							appName = info.getPackname();
							appVersion = info.getVersion();
							appRealName = info.getName();
							break;
						}					
					}
				}
				
//				if(deviceId==null||deviceId==""){
//					deviceId="Unknown";
//				}
//				if(appName==null||appName==""){
//					appName="Unknown";
//				}
//				if(appVersion==null||appVersion==""){
//					appVersion="Unknown";
//				}				
				
				//add your operation to database
			    //System.out.println(app_name);    
//			    if(appName!=null&&(!appName.contains("Unknown"))){
//			  	  mActivityManager= (ActivityManager) MainTabActivity.this.getSystemService(ACTIVITY_SERVICE);
//			  	  db_sl = new Query(MainTabActivity.this);
//			  	  db_sl.add(appName,destination);
//			  	  showDialog(appName);
//			    }
			    if(appRealName!=null){
			    	// check whether our app is in top
//			    	if (ourPackageName.equals(mActivityManager.getRunningTasks(1).get(0).topActivity
//			    	.getPackageName())) {
//			    		showDialog(appName,appRealName);
//			    	}
			    	//showDialog(appName,appRealName,leakageType);
			    	String allLeakageType="";
			    	int leakageTypeInt = Integer.valueOf(leakageType,16);
			    	if((leakageTypeInt&0x0001)!=0){
			    		allLeakageType+=" Location";
			    	}
			    	if((leakageTypeInt&0x0002)!=0){
			    		allLeakageType+=" Contacts";
			    	}
			    	if((leakageTypeInt&0x0008)!=0){
			    		allLeakageType+=" Phone Number";
			    	}
			    	if((leakageTypeInt&0x0400)!=0){
			    		allLeakageType+=" IMEI";
			    	}
			    	if((leakageTypeInt&0x0800)!=0){
			    		allLeakageType+=" IMSI";
			    	}
			    	if((leakageTypeInt&0x1000)!=0){
			    		allLeakageType+=" ICCID";
			    	}
			    	
			    
			    	Toast.makeText(getApplicationContext(), appRealName+" tried leaked type: "+allLeakageType , Toast.LENGTH_SHORT).show();
        			
//					mActivityManager= (ActivityManager) MainTabActivity.this.getSystemService(ACTIVITY_SERVICE);
			    	db_sl = new Query(MainTabActivity.this);
					db_sl.add(appRealName,destination);
					Message newmsg = new Message();
	                Bundle newb = new Bundle();
	                newb.putString("function", "refresh_page");
	                newmsg.setData(newb);
	                MainPage.myHandler.sendMessage(newmsg);
//					Intent intent = new Intent(MainTabActivity.this, MainTabActivity.class);
//					startActivity(intent);
//					finish();	
					
					LeakageReportThread l1 = new LeakageReportThread(AppManagerActivity.deviceId,appName,appVersion,leakageType,destination);
					new Thread(l1).start();
			    }
			}
        }
    } 
    
    /**
     * Used to get deviceId, which is used to represent User
     * */
    private String getDeviceId(){
    	TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
    	
        UUID deviceUuId = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuId.toString();
    }
    public void onBackPressed() {  
 	   new AlertDialog.Builder(MainTabActivity.this)
 	   	   .setTitle("Do you really want to close Uranine?")  
 	       .setIcon(android.R.drawable.ic_dialog_info)  
 	       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {  
 	     
 	           @Override  
 	           public void onClick(DialogInterface dialog, int which) {  
 	           // Bye
 	        	   MainTabActivity.this.finish();  
 	     
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
}

