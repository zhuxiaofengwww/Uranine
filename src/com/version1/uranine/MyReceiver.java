package com.version1.uranine;


import com.version1.uranine.database.Query;

import android.animation.AnimatorSet.Builder;
import android.app.AlertDialog;
import android.content.BroadcastReceiver; 
import android.content.Context; 
import android.content.DialogInterface;
import android.content.Intent; 
import android.content.IntentFilter;
import android.os.Bundle; 

import com.version1.uranine.database.DBOpenHelper;

import com.version1.uranine.database.Query;
//import android.telephony.SmsMessage; 

import android.util.Log;
import android.widget.Toast; 


public class MyReceiver extends BroadcastReceiver 
{ 
	private boolean instrumented=false;
	private Query db_s3;
	
    public MyReceiver() {
        Log.v("BROADCAST_TAG", "myBroadCast");
    }

  @Override 
  public void onReceive(Context context, Intent intent) 
  { 
    // TODO Auto-generated method stub 
	  String message = intent.getStringExtra("message"); 
	  Log.v("BROADCAST_TAG", "onReceive"+ intent+" Message:"+message);
	  dialog(context);
//	  IntentFilter filter = new IntentFilter(); 
//      filter.addAction(ACTION); 
//      filter.setPriority(Integer.MAX_VALUE); 
//      registerReceiver(MyReceiver, filter);	  

//    if (intent.getAction().equals(mACTION)) 
//    { 
//
//	  
//      StringBuilder sb = new StringBuilder(); 
//
//      Bundle bundle = intent.getExtras(); 
//
//      if (bundle != null) 
//      { 
//    	  String info = intent.getDataString();
//
//    	  dialog(context);
//    	  
//      }    
//
//      Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();       
//      
//      Intent i = new Intent(context, MainTabActivity.class); 
//
//      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//      context.startActivity(i); 
//    } 
  } 
  protected void dialog(Context context) {

      AlertDialog.Builder builder = new AlertDialog.Builder(context);

      builder.setMessage("Are you sure to allow this actioin?");

      builder.setTitle("Notification");

      builder.setPositiveButton("Allow",

              new android.content.DialogInterface.OnClickListener() {

                  @Override

                  public void onClick(DialogInterface dialog, int which) {

                      dialog.dismiss();


                  }

              });

      builder.setNegativeButton("Deny",

              new android.content.DialogInterface.OnClickListener() {

                  @Override

                  public void onClick(DialogInterface dialog, int which) {

                      dialog.dismiss();

                  }

              });
      }
 
//  private void showDialog(String mess){  
//      new AlertDialog.Builder(MainTabActivity.class)
//    	.setTitle("Decision")  
//    	.setMessage(mess)  
//    	.setNegativeButton("Allow",new DialogInterface.OnClickListener(){       
//            public void onClick(DialogInterface dialog, int which){
//
//            }  
//        })  
//        .show();  
//  }
} 


