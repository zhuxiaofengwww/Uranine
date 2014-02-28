package com.version1.uranine.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.version1.uranine.AppManagerActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author ggy. Upload apk to the server
 *
 */

public class FileUploadThread implements Runnable {
	File uploadFile = null;
	String regId = null;
	static String uploadURL = CommonUtilities.SERVER_URL + "/upload_file";
	//static String uploadURL = "http://10.0.2.2:8000/upload_file";
	static boolean isLoginAccount = false;
	BooleanTool isTransfer = null;
	String appName = "";
	String appVersion = "";
	String deviceID = "";
	int startPosition = 0;
	int location = 0;
	
	final String TAG = "uploadFile";
    final int TIME_OUT = 10*1000;   //超时时间
    final String CHARSET = "utf-8"; //设置编码
	
	public FileUploadThread(String appName, String appVersion, String deviceID, File uploadFile,String regId, boolean isLoginAccount,BooleanTool isTransfer,int location, int startPosition){
		this.appName=appName;
		this.appVersion=appVersion;
		this.deviceID=deviceID;
		this.uploadFile=uploadFile;
		this.regId = regId;
		this.isLoginAccount = isLoginAccount;
		this.startPosition = startPosition;
		this.isTransfer = isTransfer;
		this.location = location;
	}
	
//	public static String uploadFile(File uploadFile) {
//
//	}
	
	@Override
	public void run() {
		
	    /**
	     * android上传文件到服务器
	     * @param file  需要上传的文件
	     * @param RequestURL  请求的rul
	     * @return  返回响应的内容
	     */
		String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n"; 
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        String SEPARETOR = "~";
        int onceUploadSize = 500*1024;
        
        try {
        	
	        if(uploadFile!=null)
	        {
	        	while(AppManagerActivity.transferingNum>=AppManagerActivity.maxTransferNum&&isTransfer.getB()){
	        		Log.e("UploadingNum",AppManagerActivity.transferingNum+"");
	        		Thread.sleep(5000);
	        	}
	        	if(isTransfer.getB()==false){
	        		return;
	        	}
	        	AppManagerActivity.transferingNum++;
	        	
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
	        	
                final long uploadFileSize=uploadFile.length();
                long uploadedSize=startPosition;
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                msg = new Message();
	            b = new Bundle();// 存放数据
	            b.putString("function", "transfer_file");
	            b.putInt("location", location);
	            b.putString("filesize", FileTool.getFileSize(uploadFileSize));
	            b.putString("transfer_type", "Upload");
	            b.putInt("transfer_size", (int)(uploadedSize*100/uploadFileSize));
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
	            
	            msg = new Message();
	            b = new Bundle();// 存放数据
	            b.putString("function", "show_short_message");
	            b.putString("message", "Start uploading app: "+appName+" to server");
	            msg.setData(b);
	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
	            
                while(uploadedSize<uploadFileSize&&isTransfer.getB()==true){

                    Log.d("UploadStart",""+uploadedSize);
                    Log.d("UploadStart",""+startPosition);
                    
    	            URL url = new URL(uploadURL);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	            conn.setReadTimeout(TIME_OUT);
    	            conn.setConnectTimeout(TIME_OUT);
    	            conn.setDoInput(true);  //允许输入流
    	            conn.setDoOutput(true); //允许输出流
    	            conn.setUseCaches(false);  //不允许使用缓存
    	            conn.setRequestMethod("POST");  //请求方式
    	            conn.setRequestProperty("Charset", CHARSET);  //设置编码
    	            conn.setRequestProperty("connection", "keep-alive");   
    	            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY); 
    	            
                    DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                    StringBuffer sb = new StringBuffer();
                    //sb.append("app_name="+ appName.toString() + "&app_version="+ appVersion.toString());
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);
                    /**
                     * 这里重点注意：
                     * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名的   比如:abc.png  
                     */
                    if(regId!=""){
                    	deviceID="";
                    }
                    String fileinfo = appName+SEPARETOR+appVersion+SEPARETOR+deviceID+SEPARETOR+uploadFile.length()+SEPARETOR+regId+SEPARETOR+".apk";
                    Log.e("UploadFile",fileinfo);
                    
                    sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+fileinfo+"\""+LINE_END); 
                    sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
                    //InputStream is = new FileInputStream(uploadFile);
                    
    	            RandomAccessFile fileOutStream;
                    fileOutStream = new RandomAccessFile(uploadFile, "r");
                    fileOutStream.seek(startPosition);
                    byte[] bytes = new byte[8192];
                    int len = 0;
//                    int transferCount = 0;
                    while(((len=fileOutStream.read(bytes))!=-1)&&(isTransfer.getB()==true)&&(uploadedSize-startPosition<onceUploadSize)&&(uploadedSize<uploadFileSize))
                    {
                        dos.write(bytes, 0, len);
                        uploadedSize += len; 
//                        transferCount++;
//                        if(transferCount%30==0){
            	        	Log.d("UranineFileUpload","uploadedSize: "+uploadedSize);
            	            Log.d("UranineFileUpload","文件上传了"+(uploadedSize*100/uploadFileSize)+"%");
//            	            msg = new Message();
//        		            b = new Bundle();// 存放数据
//        		            b.putString("function", "transfer_file");
//        		            b.putInt("location", location);
//        		            b.putString("filesize", FileTool.getFileSize((long)(uploadFileSize)));
//        		            b.putString("transfer_type", "Upload");
//        		            b.putInt("transfer_size", (int)(uploadedSize*100/uploadFileSize));
//        		            msg.setData(b);
//        		            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
//        	        	}
                    }
                    startPosition=(int)uploadedSize;
                    
                    dos.write(LINE_END.getBytes());
                    byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                    dos.write(end_data);
                    dos.flush();
                    dos.close();
                    fileOutStream.close();
                    /**
                     * 获取响应码  200=成功
                     * 当响应成功，获取响应的流  
                     */
                    int res = conn.getResponseCode();  
                    //Log.e(TAG, "response code:"+res);
                    if(res==200)
                    {
                        Log.e(TAG, "upload file size: "+ uploadFile.length());
                        Log.e(TAG, "request success");
                        InputStream input =  conn.getInputStream();
                        StringBuffer sb1= new StringBuffer();
                        int ss ;
                        while((ss=input.read())!=-1)
                        {
                            sb1.append((char)ss);
                        }
                        result = sb1.toString();
                        
                        if(isTransfer.getB()==false){
                        	msg = new Message();
        		            b = new Bundle();// 存放数据
        		            b.putString("function", "show_short_message");
        		            b.putString("message", "Stop uploading app: "+appName+" to server.");
        		            msg.setData(b);
        		            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
                        }
                        
                        if(result.equalsIgnoreCase("upload success!")){
                        	Log.d("UranineFileUpload","num: "+uploadedSize);
            	            Log.d("UranineFileUpload","文件上传了"+(uploadedSize*100/uploadFileSize)+"%");
            	            if(isTransfer.getB()==true){
            	            	msg = new Message();
            		            b = new Bundle();// 存放数据
            		            b.putString("function", "transfer_file");
            		            b.putInt("location", location);
            		            b.putString("filesize", FileTool.getFileSize((long)(uploadFileSize)));
            		            b.putString("transfer_type", "Upload");
            		            b.putInt("transfer_size", (int)(uploadedSize*100/uploadFileSize));
            		            msg.setData(b);
            		            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
            	            }            	            
        		            if(uploadedSize==uploadFileSize){
            		            msg = new Message();
            		            b = new Bundle();// 存放数据
            		            b.putString("function", "show_short_message");
            		            b.putString("message", "Upload app: "+appName+" finished.");
            		            msg.setData(b);
            		            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
                                startPosition=(int)uploadedSize;
                                Log.e(TAG, "result : "+ result);	
        		            }
                        }else if(isTransfer.getB()==true){
                        	msg = new Message();
                            b = new Bundle();// 存放数据
                            b.putString("function", "show_message");
                            b.putString("message", "Network error when uploading "+appName+" ! Bad request");
                            //b.putLong("filesize", instrumentFileLength);
                            msg.setData(b);
                            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
                            //Log.e(TAG, "request error");
                            break;
                        }
                        
//            			Log.d("UranineUpload",result);
//            	        if((regId=="")&&(result.equalsIgnoreCase("upload success!"))&&(isTransfer.getB()==true)&&(uploadedSize==uploadFileSize)){
//                            isTransfer.setB(true);
//            	        	String instrumentFileName = appName+"_"+appVersion+".urn.apk";
//            	        	//String downloadURL="http://10.0.2.2:8000/get_instrument_file";
//            	        	String getInstrumentURL= CommonUtilities.SERVER_URL + "/get_instrument_file";
//            	        	String getInstrumentFileURL = getInstrumentURL + "?filename="+instrumentFileName;
//            		    	Log.d("UranineUpload",getInstrumentFileURL);
//            	            HttpGet httpGet = new HttpGet(getInstrumentFileURL);
//            	            String fileExist = "No";
//            	        	for(int i=0;i<24;i++)
//            	        	{
//            	        		Thread.sleep(300000);
//            	        		Log.d("UranineUpload","request instrumented file"+instrumentFileName);
//            	        		HttpResponse getFileResponse = new DefaultHttpClient().execute(httpGet);
//            	    	        if (getFileResponse.getStatusLine().getStatusCode() != 200) {
//            	    	        	//return "Network error";
//            	    	        	return;
//            	    	        }
//            	    	        fileExist = EntityUtils.toString(getFileResponse.getEntity());
//            	    	        if(fileExist.contains("Yes")){
//            	    	        	int filesize = Integer.parseInt(fileExist.substring("Yes".length()));
//            	    	        	FileDownloadThread fileDownloadThread = new FileDownloadThread(instrumentFileName,FileTool.saveFilePath,isTransfer,location,filesize);
//            						new Thread(fileDownloadThread).start();
//            						break;
//            	    	        }
//            	    	        
//            	    	        //InputStream fileFromServer = fileData.getContent();
//            	    	        //downloadFileName = downloadFileName.substring(4);
//            	        	}
//            	        	
//            	        }
                        
                    }else{
                    	msg = new Message();
                        b = new Bundle();// 存放数据
                        b.putString("function", "show_message");
                        b.putString("message", "Network error when uploading "+appName+" ! Bad request");
                        //b.putLong("filesize", instrumentFileLength);
                        msg.setData(b);
                        AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
                        //Log.e(TAG, "request error");
                        break;
                    }
                }
    	        isTransfer.setB(false);
                AppManagerActivity.transferingNum--;
	        }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when uploading "+appName+" ! MalformedURLException");
            //b.putLong("filesize", instrumentFileLength);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            isTransfer.setB(false);
            AppManagerActivity.transferingNum--;
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when uploading "+appName+" ! IOException");
            //b.putLong("filesize", instrumentFileLength);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            isTransfer.setB(false);
            AppManagerActivity.transferingNum--;
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when uploading "+appName+" ! InterruptedException");
            //b.putLong("filesize", instrumentFileLength);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        	isTransfer.setB(false);
        	AppManagerActivity.transferingNum--;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "Network error when uploading "+appName+" ! Exception");
            //b.putLong("filesize", instrumentFileLength);
            msg.setData(b);
            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        	isTransfer.setB(false);
        	AppManagerActivity.transferingNum--;
		}
        
        isTransfer.setB(false);
        return ;
		
		// TODO Auto-generated method stub 	 
//		try {
//	        HttpPost post = new HttpPost(uploadURL);
//	
//	        MultipartEntity mpEntity = new MultipartEntity();
//	        mpEntity.addPart("file", new FileBody(uploadFile));
//	        //mpEntity.addPart("device_ID", new StringBody(deviceID.toString()));
//	        mpEntity.addPart("app_name", new StringBody(appName.toString()));
//	        mpEntity.addPart("app_version", new StringBody(appVersion.toString()));
//	        mpEntity.addPart("device_id", new StringBody(deviceID.toString()));
//	        mpEntity.addPart("reg_id", new StringBody(regId.toString()));
//	        mpEntity.addPart("file_size", new StringBody(""+uploadFile.length()));
//	        post.setEntity(mpEntity);
//	        Log.d("UranineUpload",""+uploadFile.length());
//	        HttpResponse response = new DefaultHttpClient().execute(post);
//	        if (response.getStatusLine().getStatusCode() != 200) {
//	        	Log.d("UranineUpload","-----failed-----");
//	        	
//	        	Message msg = new Message();
//	            Bundle b = new Bundle();// 存放数据
//	            b.putString("function", "show_message");
//	            b.putString("message", "upload app " +appName);
//	            //b.putLong("filesize", instrumentFileLength);
//	            msg.setData(b);
//	            AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
//	        	
//	        	return ; 
//	        }
//	        String strResult = EntityUtils.toString(response.getEntity());
//			Log.d("UranineUpload",strResult);
//	        if(isLoginAccount==false&&strResult.equalsIgnoreCase("upload success!")){
//	        	String instrumentFileName = appName+"_"+appVersion+".urn.apk";
//	        	//String downloadURL="http://10.0.2.2:8000/get_instrument_file";
//	        	String getInstrumentURL= CommonUtilities.SERVER_URL + "/get_instrument_file";
//	        	String getInstrumentFileURL = getInstrumentURL + "?filename="+instrumentFileName;
//		    	Log.d("UranineUpload",getInstrumentFileURL);
//	            HttpGet httpGet = new HttpGet(getInstrumentFileURL);
//	            String fileExist = "No";
//	        	for(int i=0;i<24;i++)
//	        	{
//	        		Thread.sleep(300000);
//	        		Log.d("UranineUpload","request instrumented file"+instrumentFileName);
//	        		HttpResponse getFileResponse = new DefaultHttpClient().execute(httpGet);
//	    	        if (getFileResponse.getStatusLine().getStatusCode() != 200) {
//	    	        	//return "Network error";
//	    	        	return;
//	    	        }
//	    	        fileExist = EntityUtils.toString(getFileResponse.getEntity());
//	    	        if(fileExist.contains("Yes")){
//	    	        	int filesize = Integer.parseInt(fileExist.substring("Yes".length()));
//	    	        	//FileDownloadThread fileDownloadThread = new FileDownloadThread(instrumentFileName,FileTool.saveFilePath,filesize);
//						//new Thread(fileDownloadThread).start();
//						break;
//	    	        }
//	    	        
//	    	        //InputStream fileFromServer = fileData.getContent();
//	    	        //downloadFileName = downloadFileName.substring(4);
//	        	}
//	        	
//	        }
//	        
//	        return ;
//	        //return convertStreamToString(response.getEntity().getContent());
//	    } catch (Exception e) {
//	        //if (Constants.DEBUG) e.printStackTrace();
//	        return ;
//	    }
	}
}