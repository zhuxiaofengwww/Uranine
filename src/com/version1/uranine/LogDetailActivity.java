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

public class LogDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_detail);
		
		Button bt=(Button)findViewById(R.id.ok);
		bt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final TextView show=(TextView)findViewById(R.id.show);
				show.setText("Hello Android~" + new java.util.Date());
				uninstallAPK("com.xiaofeng.helloworld");
				installAPK("com.xiaofeng.helloworld");
			}
		});

	}
	public void uninstallAPK(String packageName){ 
		Uri uri=Uri.parse("package:"+packageName); 
		Intent intent=new Intent(Intent.ACTION_DELETE,uri);
		startActivity(intent);  
	}
	public void installAPK(String packageName){ 
		String str = "/com.xiaofeng.helloworld.apk"; 
		String fileName = Environment.getExternalStorageDirectory() + str; 
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		 intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive"); 
		startActivity(intent);		
	}

}
