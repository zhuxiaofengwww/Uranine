package com.version1.uranine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.version1.uranine.domain.UpdateInfo;
import com.version1.uranine.engine.UpdateInfoParser;
import com.version1.uranine.utils.DownLoadUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	public static final int PARSE_SUCCESS = 10;
	public static final int PARSE_ERROR = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int NETWORK_ERROR = 14;
	public static final int DOWNLOAD_SUCCESS=15;
	public static final int DOWNLOAD_ERROR=16;
	protected static final String TAG = "SplashActivity";
	private TextView tv_splash_version;
	private RelativeLayout rl_splash_main;
	private UpdateInfo updateInfo;
	
	private ProgressDialog pd;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARSE_SUCCESS:
				// it's latest version
				if (getVersion().equals(updateInfo.getVersion())) {
					loadMainUI();
				} else {
					// need update
					showUpdateDialog();
				}

				break;

			case PARSE_ERROR:
				Toast.makeText(getApplicationContext(), "PARSE ERROR,....", 0)
						.show();
				loadMainUI();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "SERVER ERROR", 0).show();
				loadMainUI();
				break;
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "URL ERROR", 0).show();
				loadMainUI();
				break;
			case NETWORK_ERROR:
				//Toast.makeText(getApplicationContext(), "NETWORK ERROR", 0).show();
				loadMainUI();
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "DOWNLOAD ERROR", 0).show();
				loadMainUI();
				break;
			case DOWNLOAD_SUCCESS:
				File file = (File) msg.obj;
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				startActivity(intent);
				finish();
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("Version" + getVersion());
		rl_splash_main = (RelativeLayout) findViewById(R.id.rl_splash_main);
		new Thread(new CheckVersionTask()).start();

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(2000);
		rl_splash_main.startAnimation(aa);

	}

	private String getVersion() {

		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// todo: can't reach
			return "";
		}

	}

	private class CheckVersionTask implements Runnable {

		public void run() {
			//connect sever to get update info
			SharedPreferences sp  = getSharedPreferences("config", MODE_PRIVATE);
			boolean update = sp.getBoolean("update", true);
			if(!update){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				loadMainUI();
				return ;
			}
			
			long startTime = System.currentTimeMillis();
			Message msg = Message.obtain();
			// get the update version
			try {
				URL url = new URL(getResources().getString(R.string.serverurl));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(1000);

				int code = conn.getResponseCode();
				if (code == 200) {
					InputStream is = conn.getInputStream();
					// parse xml
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					if (updateInfo != null) {
						// success
						msg.what = PARSE_SUCCESS;
					} else {
						// fail
						msg.what = PARSE_ERROR;
					}
				} else {
					msg.what = SERVER_ERROR;
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (NotFoundException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				msg.what = NETWORK_ERROR;
			} finally {
				long endtime = System.currentTimeMillis();
				long dtime = endtime - startTime;
				if (dtime < 2000) {
					try {
						Thread.sleep(2000 - dtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				handler.sendMessage(msg);
			}

		}

	}

	/**
	 * load MainUI
	 */
	private void loadMainUI() {

		Intent intent = new Intent(this, MainTabActivity.class);
//		Intent intent = new Intent(this, UninstallApp.class);		
		startActivity(intent);
		finish();
	}

	/**
	 * showUpdateDialog
	 */
	protected void showUpdateDialog() {

		Log.i(TAG, "display updateInfo");
		AlertDialog.Builder buidler = new Builder(this);
		buidler.setIcon(R.drawable.icon);
		buidler.setTitle("New Version Available");
		buidler.setMessage(updateInfo.getDescription());
		buidler.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				loadMainUI();
			}
		});
		buidler.setPositiveButton("Update", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "Download:" + updateInfo.getPath());
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					
					pd = new ProgressDialog(SplashActivity.this);
					pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					pd.show();
					
					final File file = new File(Environment.getExternalStorageDirectory(),DownLoadUtil.getFileName(updateInfo.getPath()));
					new Thread() {
						public void run() {
							File downloadfile = DownLoadUtil.downLoad(updateInfo.getPath(),
									file.getAbsolutePath(), pd);
							Message msg = Message.obtain();
							if(downloadfile!=null){
								//DOWNLOAD SUCCESS....
								msg.what= DOWNLOAD_SUCCESS;
								msg.obj = downloadfile;
							}else{
								//DOWNLOAD ERROR
								msg.what= DOWNLOAD_ERROR;
							}
							handler.sendMessage(msg);
							pd.dismiss();
						};
					}.start();
				}else{
					Toast.makeText(getApplicationContext(), "Not enough space in SD card", 0).show();
					loadMainUI();
				}

			}
		});
		buidler.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});
		// buidler.create().show();
		buidler.show();
	}
}