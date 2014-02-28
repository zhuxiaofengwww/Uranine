package com.version1.uranine.utils;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.version1.uranine.AppManagerActivity;

import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

/**
 * 
 * @author ggy. Report leakage to the server
 *
 */

public class LeakageReportThread implements Runnable {
	static String reportURL = CommonUtilities.SERVER_URL + "/detached_leakage";
	//static String reportURL = "http://10.0.2.2:8000/detached_leakage";
	String device_id = null;
	String app_name = null;
	String app_version = null;
	String leakage_type = null;
	String leakage_destination = null;
	
	public LeakageReportThread(String device_id, String app_name, String app_version, String leakage_type, String leakage_destination){
		this.device_id = device_id;
		this.app_name = app_name;
		this.app_version = app_version;
		this.leakage_destination = leakage_destination;
		this.leakage_type = leakage_type;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// TODO Auto-generated method stub 	 
	    try {
	        HttpPost post = new HttpPost(reportURL);
	        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Chicago"));
	        
	        MultipartEntity mpEntity = new MultipartEntity();
	        mpEntity.addPart("device_id", new StringBody(device_id.toString()));
	        mpEntity.addPart("app_name", new StringBody(app_name.toString()));
	        mpEntity.addPart("app_version", new StringBody(app_version.toString()));
	        mpEntity.addPart("leakage_type", new StringBody(leakage_type.toString()));
	        mpEntity.addPart("leakage_destination", new StringBody(leakage_destination.toString()));
	        mpEntity.addPart("leakage_time_year", new StringBody(cal.get(Calendar.YEAR)+""));
	        mpEntity.addPart("leakage_time_month", new StringBody(cal.get(Calendar.MONTH)+1+""));
	        mpEntity.addPart("leakage_time_day", new StringBody(cal.get(Calendar.DAY_OF_MONTH)+""));
	        mpEntity.addPart("leakage_time_hour", new StringBody(cal.get(Calendar.HOUR_OF_DAY)+""));
	        mpEntity.addPart("leakage_time_minute", new StringBody(cal.get(Calendar.MINUTE)+""));
	        mpEntity.addPart("leakage_time_second", new StringBody(cal.get(Calendar.SECOND)+""));
//	        Log.d("CalendarChicago","Year:"+cal.get(Calendar.YEAR)+", Month:"+(cal.get(Calendar.MONTH)+1)+
//	        		", Day:"+cal.get(Calendar.DAY_OF_MONTH)+", Hour:"+cal.get(Calendar.HOUR_OF_DAY)+", Minute:"+
//	        		cal.get(Calendar.MINUTE)+", Second:"+cal.get(Calendar.SECOND));
	        post.setEntity(mpEntity);
	        Log.d("LeakageReporting",device_id + " " + app_name + " " + app_version + " "+leakage_type+" "+leakage_destination);
	        HttpResponse response = new DefaultHttpClient().execute(post);
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	Message msg = new Message();
	            Bundle b = new Bundle();// 存放数据
	            b.putString("function", "show_message");
	            b.putString("message", "Network error when reporting leakage! Bad request");
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	            
	        	return;
	        }
	        String strResult = EntityUtils.toString(response.getEntity());
	        return;
	    } catch (Exception e) {
	        e.printStackTrace();
	        Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when reporting leakage! Exception");
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	        return;
	    }
	}
}
