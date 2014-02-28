package com.version1.uranine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.version1.uranine.AppManagerActivity;
import com.version1.uranine.MainTabActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author ggy. Report leakage to the server
 *
 */

public class LeakageDetectThread implements Runnable {
	private Process mLogcatProc = null;
	private BufferedReader reader = null;
    private  String content="";
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// TODO Auto-generated method stub 	 

        try
        {
        	//System.out.println("****************************************");
        	Log.e("LeakageDetech","************************************");
        	
        	while(true){
        		
//            	mLogcatProc = Runtime.getRuntime().exec(new String[]
//            			{"logcat","-d","-v","process","Ads:I *:S"});
            	
            	mLogcatProc = Runtime.getRuntime().exec(new String[]
            			{"logcat","-d","-v","process","-s","Uranine"});
//            	Runtime.getRuntime().exec(new String[]
//            			{"logcat","-c","Uranine"});
        		//mLogcatProc = Runtime.getRuntime().exec(new String("logcat -s Uranine"));
            	
            	reader = new BufferedReader(new InputStreamReader
            			(mLogcatProc.getInputStream()));

            	String line;
//  
////                  final StringBuilder logger = new StringBuilder();
//            	String separator = System.getProperty("line.separator"); 
//            	HashMap<String, String> map = new HashMap<String, String>();
////            		String regex = "I \\(.+?\\) adRequestUrlHtml: <html><head><script src=\"(.+?)\"></script><script>AFMA_buildAdURL\\(\\{(.+?)\\}\\);</script></head><body></body></html>"; 		
//            	String regex = "I\\(.+?\\) adRequestUrlHtml: <html><head><script src=\"(.+?)\"></script><script>AFMA_buildAdURL\\(\\{(.+?)\\}\\);</script></head>.+?\\(Ads\\)"; 		
            	
            	while ((line = reader.readLine()) != null)
            	{
//            			System.out.println("---------------------"+line);
            		Log.e("LeakageDetectInfo",line);
            		if(line.contains("Uranine")&&line.contains("taint")){
            			String rex="[()]+";
                		int pid =Integer.parseInt((line.split(rex))[1].replace(' ', '0'));
                		//Log.e("LeakagePid",""+pid);
                		String leakageType=line.substring(line.lastIndexOf("taint")+6,line.lastIndexOf("(")-2);
                		//Log.e("LeakageType",leakageType);
                		String destination=line.substring(line.indexOf("leaked"),line.lastIndexOf("]")-1);
                		//Log.e("LeakageDestination",destination);
                		//Log.e("LeakageDetect","Pid: "+pid+" leakage_type: "+leakageType+" destination: " + destination);
                		Message msg = new Message();
                        Bundle b = new Bundle();// 存放数据
                        b.putString("function", "report_leakage");
                        b.putInt("pid", pid);
                        b.putString("leakageType", leakageType);
                        b.putString("destination", destination);
                        msg.setData(b);
                        MainTabActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            		}
            		
//            		Pattern pattern = Pattern.compile(regex);
//            		Matcher matcher = pattern.matcher(line);
//
//            		if (matcher.matches()) {
//                			
//            			String source = matcher.group(1);
////                			System.out.println(source); 
//            			String destination=source.substring(0, source.lastIndexOf("/"));
//            			System.out.println(destination);    	
//            			Log.d("UranineLeakageInfo","destination: "+destination);
//            			String group = matcher.group(2);
////    						System.out.println(group);
//            			String[] str1 = group.split("\",\"");
//            			for(int i=0; i<str1.length;i++){
//            				String hashcontent[]=str1[i].replace("\"",""  ).split(":");
//            				String title=hashcontent[0];
//            				String content=hashcontent[1];
//            				map.put(title, content);  
//            			}	
//            			String app_name=map.get("app_name");
//            			System.out.println(app_name);            			
//            			Log.d("UranineLeakageInfo","app_name: "+app_name);
////            			LeakageReportThread l1 = new LeakageReportThread(AppManagerActivity.deviceId,app_name,"2.2","IMEI",destination);
////    					new Thread(l1).start();
//            		}
////            			am.killBackgroundProcesses("logos.quiz.companies.game");             		
//            		Log.i("LOG_TAG", "logcat"+line+separator);
      
//                        		logger.append(line);
//                        		logger.append(separator);                			

//                    		content+=logger.toString();
            	}
                    // do whatever you want with the log.  I'd recommend using Intents to create an email
            	Thread.sleep(30000);
        	}
        }catch (IOException e)
        {
        	//Log.e("LeakageDetect","IOE1");
        	e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        finally
        {
        	if (reader != null){
        		try
            	{
        			reader.close();
            	}
            	catch (IOException e)
            	{
            		//Log.e("LeakageDetect","IOE2");
            		e.printStackTrace();
            	}	
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
}
