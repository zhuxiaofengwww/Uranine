package com.version1.uranine;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UninstallApp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello_world);
		
		Button bt=(Button)findViewById(R.id.ok);
		bt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final TextView show=(TextView)findViewById(R.id.show);
				show.setText("Hello Android~" + new java.util.Date());
				uninstallAPK("com.version1.uranine");

			}
		});

	}
	public void uninstallAPK(String packageName){ 
		Uri uri=Uri.parse("package:"+packageName); 
		Intent intent=new Intent(Intent.ACTION_DELETE,uri);
		startActivity(intent);  
	}

	
	 private void openFile(File f) 
	  {
	    Intent intent = new Intent();
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	    
	    /* getMIMEType() */
	    String type = getMIMEType(f);
	    /* intent file MimeType */
	    intent.setDataAndType(Uri.fromFile(f),type);
	    startActivity(intent); 
	  }
	  private String getMIMEType(File f) 
	  { 
	    String type="";
	    String fName=f.getName();

	    String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
	    
	    /* MimeType */
	    if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
	    {
	      type = "audio"; 
	    }
	    else if(end.equals("3gp")||end.equals("mp4"))
	    {
	      type = "video";
	    }
	    else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||end.equals("jpeg")||end.equals("bmp"))
	    {
	      type = "image";
	    }
	    else if(end.equals("apk")) 
	    { 
	      /* android.permission.INSTALL_PACKAGES */ 
	      type = "application/vnd.android.package-archive"; 
	    } 
	    else
	    {
	      type="*";
	    }
	    /*apk */
	    if(end.equals("apk")) 
	    { 
	    } 
	    else 
	    { 
	      type += "/*";  
	    } 
	    return type;  
	  } 

	  /*delete file*/
	  private void delFile(String strFileName) 
	  { 
	    File myFile = new File(strFileName); 
	    if(myFile.exists()) 
	    { 
	      myFile.delete(); 
	    } 
	  }	
		public void installAPK(String packageName){ 
			String str = "/"+packageName; 
			String fileName = Environment.getExternalStorageDirectory() + str; 
			Intent intent = new Intent(Intent.ACTION_VIEW); 
			 intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive"); 
			startActivity(intent);		
		}	  

}
