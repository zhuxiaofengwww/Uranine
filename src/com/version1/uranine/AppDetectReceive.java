package com.version1.uranine;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/**
 * 
 * @author ggy used to detect the install and uninstall of app
 *
 */
public class AppDetectReceive extends BroadcastReceiver { 
	
	public AppDetectReceive(){
		Log.e("AppDetectReceive","Start detect");
	}
	
	@Override 
    public void onReceive(Context context, Intent intent) { 
            
    	if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){ 
    		//Toast.makeText(context, "有应用被添加", Toast.LENGTH_LONG).show(); 
    		if(AppManagerActivity.myHandler!=null){
        		Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("function", "install_file");
                msg.setData(b);
                AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI	
    		}
    	} 
    	else  if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){ 
    		//Toast.makeText(context, "有应用被删除", Toast.LENGTH_LONG).show(); 
    		if(AppManagerActivity.myHandler!=null){
        		Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putString("function", "uninstall_file");
                msg.setData(b);
                AppManagerActivity.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
    		}
        } 
    	/*   else  if(Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())){
                Toast.makeText(context, "有应用被改变", Toast.LENGTH_LONG).show();
        }*/ 
    	else  if(Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){ 
    		//Toast.makeText(context, "有应用被替换", Toast.LENGTH_LONG).show(); 
        } 
    	/* else  if(Intent.ACTION_PACKAGE_RESTARTED.equals(intent.getAction())){
                Toast.makeText(context, "有应用被重启", Toast.LENGTH_LONG).show();
        }*/ 
          /*  else  if(Intent.ACTION_PACKAGE_INSTALL.equals(intent.getAction())){
                Toast.makeText(context, "有应用被安装", Toast.LENGTH_LONG).show();
        }*/ 
    }     
} 	