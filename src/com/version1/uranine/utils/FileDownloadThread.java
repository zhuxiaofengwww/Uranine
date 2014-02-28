package com.version1.uranine.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.version1.uranine.AppManagerActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author ggy. Download the instrumented apk from server
 *
 */

public class FileDownloadThread implements Runnable{
	String downloadFileName = null;
	String saveFilePath = null;
	//static String downloadURL="http://10.0.2.2:8000/download_file";
	static String downloadURL=CommonUtilities.SERVER_URL + "/download_file";
	int filesize = 0;
	BooleanTool isTransfer = null;
	int location = 0;
	
	public FileDownloadThread(String downloadFileName, String saveFilePath,BooleanTool isTransfer, int location, int filesize){
		this.downloadFileName=downloadFileName;
		this.saveFilePath = saveFilePath;
		this.filesize = filesize;
		this.isTransfer = isTransfer;
		this.location = location;
	}
	
//	public static String downloadFile(String downloadFileName) {
//	    
//	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {

			//Log.d("UranineDownload",saveFilePath);
	        File downloadFile = new File(saveFilePath+downloadFileName);
	        //Log.d("UranineDownload",downloadFile.getAbsolutePath());
	        int downloadFileSize = (int) downloadFile.length();
	        //Log.d("UranineDownload","downloadFileSize"+downloadFileSize);
	    	String downloadFileURL = downloadURL + "?filename="+downloadFileName+"&filestart="+""+downloadFileSize;
	    	
	    	File saveFolder = new File(saveFilePath);
	    	if(!saveFolder.exists()){
	    		saveFolder.mkdirs();
	    	}
	    	
            HttpGet httpGet = new HttpGet(downloadFileURL);
	    	
//	        HttpPost post = new HttpPost(downloadURL);
//	        
//	        List<NameValuePair> params=new ArrayList<NameValuePair>();
//
//	        params.add(new BasicNameValuePair("filename",downloadFileName));
//
//	        //发出HTTP request
//	        post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
//	        MultipartEntity mpEntity = new MultipartEntity();
//	        mpEntity.addPart("filename", downloadFileName));
//	        post.setEntity(mpEntity);
	        //post.addHeader("server_id", String.valueOf(server_id));
	        //Log.d("UranineDownload",downloadFileName);
	        //HttpResponse response = Connector.client.execute(post);
	        HttpResponse response = new DefaultHttpClient().execute(httpGet);
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	//Log.d("UranineDownload","-----failed-----");
	        
	        	Message msg = new Message();
	            Bundle b = new Bundle();// 存放数据
	            b.putString("function", "show_message");
	            b.putString("message", "Network error when downloading "+downloadFileName +" ! Bad request");
	            //b.putLong("filesize", instrumentFileLength);
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	            isTransfer.setB(false);
	            return ; 
	        }
	        HttpEntity fileData = response.getEntity();
	        InputStream fileFromServer = fileData.getContent();
	        //downloadFileName = downloadFileName.substring(4);
	        RandomAccessFile fileInStream;
	        fileInStream = new RandomAccessFile(downloadFile, "rw");
	        fileInStream.seek(downloadFileSize);
	        int instrumentedFileSize = filesize;
	        final int bufferSize = 8192;
	    	byte[] buffer=new byte[bufferSize];
			int num = fileFromServer.read(buffer);//接收文件内容
			int transferCount = 0;
			Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "transfer_file");
            b.putInt("location", location);
            b.putString("filesize", FileTool.getFileSize((long)(instrumentedFileSize)));
            b.putString("transfer_type", "Download");
            b.putInt("transfer_size", downloadFileSize*100/instrumentedFileSize);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            
            msg = new Message();
            b = new Bundle();// 存放数据
            b.putString("function", "show_short_message");
            b.putString("message", "Start download apk: "+downloadFileName+" from server.");
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            
	        while(num!=-1&&isTransfer.getB()==true) {  
	        	//Log.d("UranineDownloadFile",buffer.toString());
	        	fileInStream.write(buffer,0,num);  
	        	downloadFileSize += num;
	        	transferCount++;
	        	if(transferCount%50==0&&isTransfer.getB()==true){
	        		//Log.d("UranineFileDownload","num: "+num);
		            //Log.d("UranineFileDownload","文件接收了"+(downloadFileSize*100/instrumentedFileSize)+"%");
		            msg = new Message();
		            b = new Bundle();// 存放数据
		            b.putString("function", "transfer_file");
		            b.putInt("location", location);
		            b.putString("filesize", FileTool.getFileSize((long)(instrumentedFileSize)));
		            b.putString("transfer_type", "Download");
		            b.putInt("transfer_size", downloadFileSize*100/instrumentedFileSize);
		            msg.setData(b);
		            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
	        	}
	        	num = 0;  
	            if (fileFromServer != null) {  
	            	num = fileFromServer.read(buffer);  
	            }  
	        }
	        if(isTransfer.getB()==false){
            	msg = new Message();
	            b = new Bundle();// 存放数据
	            b.putString("function", "show_short_message");
	            b.putString("message", "Stop downloading apk: "+downloadFileName+" from server.");
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
            }else{
    	        msg = new Message();
                b = new Bundle();// 存放数据
                b.putString("function", "transfer_file");
                b.putInt("location", location);
                b.putString("filesize", FileTool.getFileSize((long)(instrumentedFileSize)));
                b.putString("transfer_type", "Download");
                b.putInt("transfer_size", downloadFileSize*100/instrumentedFileSize);
                msg.setData(b);
                AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
            }
	        fileInStream.close();  
	        fileFromServer.close();
	        //Log.d("UranineDownload",downloadFile.getName());
	        //Log.d("UranineDownload",""+downloadFile.length());
	        //Log.d("AndroidClient","@@@文件接收完成"+downloadFile.getAbsolutePath()); 
	        if(downloadFile.length()==instrumentedFileSize){
	        	msg = new Message();
	            b = new Bundle();// 存放数据
	            b.putString("function", "show_short_message");
	            b.putString("message", "Dwnloading apk: "+downloadFileName+" success, please install it.");
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
	        	msg = new Message();
	            b = new Bundle();// 存放数据
	            b.putString("function", "open_file");
	            b.putString("filename", downloadFile.getName());
	            //b.putLong("filesize", instrumentFileLength);
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	        }
	        isTransfer.setB(false);
	        return ;
	        //return convertStreamToString(response.getEntity().getContent());
	    }catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putString("function", "show_message");
			b.putString("message", "Network error when downloading "+downloadFileName +" ! FileNotFoundException");
			//b.putLong("filesize", instrumentFileLength);
			msg.setData(b);
			AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			isTransfer.setB(false);
	        return ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putString("function", "show_message");
			b.putString("message", "Network error when downloading "+downloadFileName +" ! IOException");
			//b.putLong("filesize", instrumentFileLength);
			msg.setData(b);
			AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			isTransfer.setB(false);
	        return ;
		}catch (Exception e) {
	        //if (Constants.DEBUG) e.printStackTrace();
	    	e.printStackTrace();
			Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when downloading "+downloadFileName +" ! Exception");
            //b.putLong("filesize", instrumentFileLength);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	    	isTransfer.setB(false);
	        return ;
	    }
	}
}
