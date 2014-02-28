package com.version1.uranine.utils;

import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

//import com.google.android.gcm.GCMRegistrar;
import com.version1.uranine.AppManagerActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author ggy. Instrument file
 *
 */

public class FileInstrumentThread implements Runnable {
	static String regId = null;
	//static String instrumentURL = "http://10.0.2.2:8000/instrument_file";
	static String instrumentURL = CommonUtilities.SERVER_URL+"/instrument_file";
	boolean isLoginAccount = false;
	String apkPath = null;
	String appPackName = null;
	String appVersion = null;
	String deviceID = null;
	BooleanTool isTransfer = null;
	int location = 0;
	String appRealName = null;
	
	public FileInstrumentThread(String apkPath, String appRealName, String appPackName, String appVersion, String regId, String deviceID, boolean isLoginAccount, BooleanTool isTransfer, int location){
		this.apkPath = apkPath;
		this.appRealName = appRealName;
		this.appPackName = appPackName;
		this.appVersion = appVersion;
		this.regId = regId;
		this.deviceID = deviceID;
		this.isLoginAccount = isLoginAccount;
		this.isTransfer = isTransfer;
		this.location = location;
	}
	
	@Override
	public void run() {
		try {
			
			if(regId!=""){
				deviceID=regId.substring(regId.length()-30, regId.length());
			}
			
            String instrumentFileURL = instrumentURL + "?app_name="+appPackName+"&app_version="+appVersion+"&device_id="+deviceID;
	    	
            HttpGet httpGet = new HttpGet(instrumentFileURL);
//	        HttpPost post = new HttpPost(instrumentURL);
//	
//	        MultipartEntity mpEntity = new MultipartEntity();
//	        mpEntity.addPart("app_name", new StringBody(appName.toString()));
//	        mpEntity.addPart("app_version", new StringBody(appVersion.toString()));
//	        mpEntity.addPart("device_ID", new StringBody(deviceID.toString()));
//	        post.setEntity(mpEntity);
	        //Log.d("Instrument app",appName+" "+appVersion+" "+deviceID);
	        //HttpResponse response = new DefaultHttpClient().execute(httpGet);
	        
	        HttpClient client = new DefaultHttpClient();
            // 请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse response = client.execute(httpGet);
	         
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	//Log.d("UranineInstrument","-----failed-----");
	        	
	        	Message msg = new Message();
	            Bundle b = new Bundle();// 存放数据
	            b.putString("function", "show_message");
	            b.putString("message", "Network error when instrument " + appRealName+" ! Bad request");
	            //b.putLong("filesize", instrumentFileLength);
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	        	
	            isTransfer.setB(false);
		        return;
	        }
	        String strResult = EntityUtils.toString(response.getEntity());
	        Log.d("Instrument file","result: "+strResult);
	        //Log.d("Instrument file path",apkPath);
	        if(strResult.equalsIgnoreCase("No")){
	        	File uploadFile = new File(apkPath);
				if(uploadFile.exists()){ 
					FileUploadThread fileUploadThread = new FileUploadThread(appPackName,appVersion, deviceID, uploadFile,regId,isLoginAccount,isTransfer,location, 0);
					new Thread(fileUploadThread).start();
					//FileDownloadThread fileDownloadThread = new FileDownloadThread(uploadFile.getName());
					//new Thread(fileDownloadThread).start();
					
    			}else{
    				
    				Message msg = new Message();
    	            Bundle b = new Bundle();// 存放数据
    	            b.putString("function", "show_message");
    	            b.putString("message", appRealName+"'s apk file doesn't exist");
    	            msg.setData(b);
    	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
    			
    			}
	        }else if(strResult.equalsIgnoreCase("Instrumenting")){
	        	
	        	Message msg = new Message();
	            Bundle b = new Bundle();// 存放数据
	            b.putString("function", "show_message");
	            b.putString("message", "App "+appRealName+" is instrumenting. Please try again latter");
	            //b.putLong("filesize", instrumentFileLength);
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
		        isTransfer.setB(false);
//	        	//Every 5 minute to get instrument file, or you can alert the user to try again later
//	        	String instrumentFileName = appName+"_"+appVersion+".urn.apk";
//	        	//String downloadURL="http://10.0.2.2:8000/get_instrument_file";
//	        	String getInstrumentURL=CommonUtilities.SERVER_URL + "/get_instrument_file";
//	        	String getInstrumentFileURL = getInstrumentURL + "?filename="+instrumentFileName;
//	        	Log.d("UranineInstrument",getInstrumentFileURL);
//	        	HttpGet httpFileGet = new HttpGet(getInstrumentFileURL);
//	        	String fileExist = "No";
//	        	for(int i=0;i<24;i++)
//	        	{
//	        		Thread.sleep(300000);
//	        		Log.d("UranineInstrument","request instrumented file"+instrumentFileName);
//	        		HttpResponse getFileResponse = new DefaultHttpClient().execute(httpFileGet);
//	        		if (getFileResponse.getStatusLine().getStatusCode() != 200) {
//	        			//return "Network error";
//	        			Log.d("UranineGetInstrument","-----failed-----");
//	        			
//	        			Message msg = new Message();
//	    	            Bundle b = new Bundle();// 存放数据
//	    	            b.putString("function", "show_message");
//	    	            b.putString("message", "network_error! get_instrumented_failed");
//	    	            //b.putLong("filesize", instrumentFileLength);
//	    	            msg.setData(b);
//	    	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
//	    	        	
//	        			return;
//	        		}
//	        		fileExist = EntityUtils.toString(getFileResponse.getEntity());
//	        		if(fileExist.contains("Yes")){
//	        			int filesize = Integer.parseInt(fileExist.substring("Yes".length()));
//	        			FileDownloadThread fileDownloadThread = new FileDownloadThread(instrumentFileName,FileTool.saveFilePath,isTransfer,location,filesize);
//	        			new Thread(fileDownloadThread).start();
//	        			break;
//	        		}
//	        	}
	        }else if(strResult.contains("Instrumented")){
	        	String downloadFileName = appPackName+"_"+appVersion+".urn.apk";
	        	int filesize = Integer.parseInt(strResult.substring("Instrumented".length()));
	        	FileDownloadThread fileDownloadThread = new FileDownloadThread(downloadFileName,FileTool.saveFilePath,isTransfer,location, filesize);
				new Thread(fileDownloadThread).start();
	        }else if(strResult.contains("Transfer")){
	        	//Log.d("UranineInstrument","Continue transfer app " + appName);
	        	File uploadFile = new File(apkPath);
	        	int startPosition = Integer.parseInt(strResult.substring("Transfer".length()));
				if(uploadFile.exists()){ 
					FileUploadThread fileUploadThread = new FileUploadThread(appPackName,appVersion, deviceID, uploadFile,regId,isLoginAccount,isTransfer,location,startPosition);
					new Thread(fileUploadThread).start();
					//FileDownloadThread fileDownloadThread = new FileDownloadThread(uploadFile.getName());
					//new Thread(fileDownloadThread).start();
    			}else{
    				Message msg = new Message();
    	            Bundle b = new Bundle();// 存放数据
    	            b.putString("function", "show_message");
    	            b.putString("message", appRealName+"'s apk file doesn't exist");
    	            //b.putLong("filesize", instrumentFileLength);
    	            msg.setData(b);
    	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
    			}
	        }else if(strResult.equalsIgnoreCase("Can't instrument")){
	        	//Log.d("UranineInstrument","Can't instrument app: " + appName);
	        	
	        	Message msg = new Message();
	            Bundle b = new Bundle();// 存放数据
	            b.putString("function", "show_message");
	            b.putString("message", appRealName + " can't insrtumented");
	            //b.putLong("filesize", instrumentFileLength);
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
		        isTransfer.setB(false);
	        }else{
	        	Message msg = new Message();
	            Bundle b = new Bundle();// 存放数据
	            b.putString("function", "show_message");
	            b.putString("message", "Network error when instrument " + appRealName);
	            //b.putLong("filesize", instrumentFileLength);
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	            isTransfer.setB(false);
	        }
	        return;
	        //return convertStreamToString(response.getEntity().getContent());
		} catch (Exception e) {
	        e.printStackTrace();
			Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when instrument " + appRealName+" ! Exception");
            //b.putLong("filesize", instrumentFileLength);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            isTransfer.setB(false);
            return;
	    }
	}
}