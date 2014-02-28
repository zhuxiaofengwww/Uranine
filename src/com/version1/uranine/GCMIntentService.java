/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.version1.uranine;

import static com.version1.uranine.utils.CommonUtilities.SENDER_ID;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.version1.uranine.R;
import com.version1.uranine.utils.FileDownloadThread;
import com.version1.uranine.utils.FileTool;
import com.version1.uranine.utils.ServerUtilities;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.d(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(R.string.gcm_registered));
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.d(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.d(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "Received message");
        String msg = intent.getExtras().getString( "msg" );
        String filename = intent.getExtras().getString( "filename" );
        int filesize = Integer.parseInt(intent.getExtras().getString( "filesize" ));
        Log.d("receive msg:", msg);
        Log.d("receive filename:", filename);
        Log.d("receive filesize:", ""+filesize);
        if(msg.equalsIgnoreCase("success")&&(AppManagerActivity.myHandler!=null)){
        	
        	Message message = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "download_file");
            b.putString("filename", filename);
            b.putInt("filesize",filesize);
            //b.putLong("filesize", instrumentFileLength);
            message.setData(b);
            AppManagerActivity.myHandler.sendMessage(message); // 向Handler发送消息,更新UI
        	
//        	Log.d("start download file:", ""+filename);
//        	FileDownloadThread fileDownloadThread = new FileDownloadThread(filename,FileTool.saveFilePath,filesize);
//    		new Thread(fileDownloadThread).start();		
        }else if(msg.equalsIgnoreCase("failed")&&(AppManagerActivity.myHandler!=null)){
        	Message message = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putString("function", "show_message");
            b.putString("message", "file: "+filename+"can't be instrument!");
            //b.putLong("filesize", instrumentFileLength);
            message.setData(b);
            AppManagerActivity.myHandler.sendMessage(message); // 向Handler发送消息,更新UI
        }
        
		
        //displayMessage(context, message+"\n"+msg+"\n"+filename+"\n");
        // notifies user
        //generateNotification(context, message);
    }

	public void openFile(File file) {			
        // TODO Auto-generated method stub
        Log.d("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
        startActivity(intent);            
	}
    
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.d(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        //displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.d(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.d(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, AppManagerActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
