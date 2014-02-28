package com.version1.uranine.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.provider.SyncStateContract.Constants;
import android.util.Log; 


public class FileTool {
	public static String saveFilePath = getSDPath()+"/Uranine/download/";
    public static String getSDPath(){
    	File sdDir = null;
    	boolean sdCardExist = Environment.getExternalStorageState()  
                            .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
    	if(sdCardExist)  
    	{                              
    		sdDir = Environment.getExternalStorageDirectory();//获取跟目录
    	}  
        return sdDir.toString();   
    }	
    
    public static String getDataPath(){
    	File path = Environment.getDataDirectory();
        return path.toString();   
    }	
    
    public static String getAvailPath(){
    	if(Environment.getExternalStorageState()  
                .equals(android.os.Environment.MEDIA_MOUNTED)){
    		return getSDPath();
    	}else{
    		return getDataPath();
    	}
    }	
    
    public static String getFileSize(Long filesize){
    	String size="";
    	Double fileSize;
    	DecimalFormat df=new DecimalFormat(".#");
    	if(filesize>=1024&&filesize<1024*1024){
    		fileSize=filesize/1024.0 + 0.05;
	    	size=df.format(fileSize)+" kB";
    	}else if(filesize>=1024*1024&&filesize<1024*1024*1024){
    		fileSize=filesize/1024.0/1024.0 + 0.05;
	    	size=df.format(fileSize)+ " MB";
    	}else if(filesize>=1024*1024*1024){
    		fileSize=filesize/1024.0/1024.0/1024.0 + 0.05;
        	size=df.format(fileSize)+ " GB";
    	}else{
    		size+=filesize+" B";	
    	}
        return size;   
    }	
    
    
	public static String uploadFile(String filepath) {
	    try {
	    	String url = "http://10.0.2.2:8000/upload_file";
	        //client.getParams().setParameter("http.socket.timeout", 90000); // 90 second
	        HttpPost post = new HttpPost(url);
	
	        MultipartEntity mpEntity = new MultipartEntity();
	        mpEntity.addPart("image", new FileBody(new File(filepath), "image/jpeg"));
	        post.setEntity(mpEntity);
	        //post.addHeader("server_id", String.valueOf(server_id));
	
	        //HttpResponse response = Connector.client.execute(post);
	        HttpResponse response = new DefaultHttpClient().execute(post);
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	return "false"; 
	        }
	        String strResult = EntityUtils.toString(response.getEntity()); 
	        return strResult;
	        //return convertStreamToString(response.getEntity().getContent());
	    } catch (Exception e) {
	        //if (Constants.DEBUG) e.printStackTrace();
	        return "false";
	    }
	}
    
    
    @SuppressWarnings("deprecation")
	public void doFileUpload(String path){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String urlString = "http://http://127.0.0.1:8000/";   // server ip
        try
        {
        //------------------ CLIENT REQUEST
        FileInputStream fileInputStream = new FileInputStream(new File(path) );
         // open a URL connection to the Servlet
         URL url = new URL(urlString);
         // Open a HTTP connection to the URL
         conn = (HttpURLConnection) url.openConnection();
         // Allow Inputs
         conn.setDoInput(true);
         // Allow Outputs
         conn.setDoOutput(true);
         // Don't use a cached copy.
         conn.setUseCaches(false);
         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+"    ");
         dos = new DataOutputStream( conn.getOutputStream() );
         dos.writeBytes(lineEnd);
         dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + path + "\"" + lineEnd);
         dos.writeBytes(lineEnd);

         // create a buffer of maximum size
         bytesAvailable = fileInputStream.available();
         bufferSize = Math.min(bytesAvailable, maxBufferSize);
         buffer = new byte[bufferSize];

         // read file and write it into form...
         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         while (bytesRead > 0)
         {
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }

         // send multipart form data necesssary after file data...
         dos.writeBytes(lineEnd);
         dos.writeBytes(lineEnd);

         // close streams
         Log.e("Debug","File is written");
         fileInputStream.close();
         dos.flush();
         dos.close();
        }
        catch (MalformedURLException ex)
        {
             Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe)
        {
             Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }

        //------------------ read the SERVER RESPONSE
        try {
              inStream = new DataInputStream ( conn.getInputStream() );
              String str;
              while (( str = inStream.readLine()) != null)
              {
                   Log.e("Debug","Server Response "+str);
              }
              inStream.close();
        }
        catch (IOException ioex){
             Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
    }

}
