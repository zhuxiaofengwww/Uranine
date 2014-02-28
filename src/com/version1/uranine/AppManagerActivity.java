package com.version1.uranine;


import static com.version1.uranine.utils.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.version1.uranine.utils.CommonUtilities.EXTRA_MESSAGE;
import static com.version1.uranine.utils.CommonUtilities.SENDER_ID;
import static com.version1.uranine.utils.CommonUtilities.SERVER_URL;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Text;

import android.R.integer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.version1.uranine.domain.AppInfo;
import com.version1.uranine.engine.AppInfoProvider;
import com.version1.uranine.utils.BooleanTool;
import com.version1.uranine.utils.FileDownloadThread;
import com.version1.uranine.utils.FileInstrumentThread;
import com.version1.uranine.utils.FileTool;
import com.version1.uranine.utils.ServerUtilities;


import com.version1.uranine.database.DBOpenHelper;

import com.version1.uranine.database.Query;

@SuppressLint("UseSparseArrays")
public class AppManagerActivity extends Activity {
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_appmanger;
	private View ll_loading;
	private Button instrument;
//	private ImageButton app_back;
	private List<AppInfo> userAppInfos=new ArrayList<AppInfo>();;
//	private List<AppInfo> systemAppInfos; 
	private AppManagerAdapter adapter=new AppManagerAdapter();
	private List<AppInfo> appinfos= new ArrayList<AppInfo>();
//	private CheckBox cb_setting_view_status;
	
	private TextView tv_app_manager_status;
	private Query db_s2;	
	private boolean uninstrumented=true;
	
	//write by guanyu
	private String regId = "";
	public static MyHandler myHandler;
    AsyncTask<Void, Void, Void> mRegisterTask;
    private Context appContext = null;
    boolean isLoginAccount=false;
    static String deviceId = "";
    BooleanTool[] isTransfer = new BooleanTool[1];
    private List<Integer> loc_bar = new ArrayList<Integer>();
    private List<ProgressBar> appTransferProgress = new ArrayList<ProgressBar>();
    private List<TextView> appTransferText = new ArrayList<TextView>();
    public final static int maxTransferNum=3;
    public static int transferingNum=0;
 
//    private List<ProgressBar> appTransferProgress = null;
//    private List<TextView> appTransferText = null;
    //String saveFilePath = "/Uranine/download/";
	//********

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_app_manager);
		
//       requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //define customized title 
       setContentView(R.layout.activity_app_manager); 
//       getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_manager_title);//assignment of customized layout
	       
		ll_loading = findViewById(R.id.ll_loading);
		db_s2 = new Query(this);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_appmanger = (ListView) findViewById(R.id.lv_appmanger);
//		app_back=(ImageButton) findViewById(R.id.app_back);
		instrument=(Button) findViewById(R.id.tv_app_instrument);
		tv_avail_rom.setText("Available memory:" + getAvailRom());
		tv_avail_sd.setText("Available SD:" + getAvailSD());
		tv_app_manager_status = (TextView) findViewById(R.id.tv_app_manager_status);
//		cb_setting_view_status = (CheckBox)findViewById(R.id.cb_setting_view_status);
		fillData();
		
		
		//write by guanyu, get the gcm_id********************************
		
//		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
//		List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager  
//                .getRunningAppProcesses(); 
//		for(ActivityManager.RunningAppProcessInfo apl:appProcessList){
//			Log.e("RunningAppInfo","pid: "+apl.pid+" processname: "+apl.processName);
//			Log.e("RunningAppInfo","pid: "+apl.pkgList.toString());
//		}
		
		appContext=this;
		//this is used to start GCM ,if you don't want to use it, just comment it
//		checkNotNull(SERVER_URL, "SERVER_URL");
//        checkNotNull(SENDER_ID, "SENDER_ID");
//        // Make sure the device has the proper dependencies.
//        GCMRegistrar.checkDevice(this);
//        // Make sure the manifest was properly set - comment out this line
//        // while developing the app, then uncomment it when it's ready.
//        GCMRegistrar.checkManifest(this);
//        //setContentView(R.layout.activity_main);
//        registerReceiver(mHandleMessageReceiver,
//                new IntentFilter(DISPLAY_MESSAGE_ACTION));
//        regId = GCMRegistrar.getRegistrationId(this);
//        //Log.d("regId","regId:"+regId);
//        //GCMRegistrar.unregister(this);
//        //GCMRegistrar.register(this, SENDER_ID);
//        //Log.d("regId","regId2:"+regId);
//        if (regId.equals("")) {
//            // Automatically registers application on startup.
//            GCMRegistrar.register(this, SENDER_ID);
//            //Log.d("Demo","try register");
//            //Log.d("regId","regId:"+regId);
//        } else {
//        	// Device is already registered on GCM, check server.
//            if (GCMRegistrar.isRegisteredOnServer(this)) {
//                // Skips registration.
//                //Log.d("isRegisteredOnServer",""+R.string.already_registered);
//            } else {
//                // Try to register again, but not in the UI thread.
//                // It's also necessary to cancel the thread onDestroy(),
//                // hence the use of AsyncTask instead of a raw thread.
//                final Context context = this;
//                mRegisterTask = new AsyncTask<Void, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        boolean registered =
//                                ServerUtilities.register(context, regId);
//                        // At this point all attempts to register with the app
//                        // server failed, so we need to unregister the device
//                        // from GCM - the app will try to register again when
//                        // it is restarted. Note that GCM will send an
//                        // unregistered callback upon completion, but
//                        // GCMIntentService.onUnregistered() will ignore it.
//                        if (!registered) {
//                            GCMRegistrar.unregister(context);
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        mRegisterTask = null;
//                    }
//
//                };
//                mRegisterTask.execute(null, null, null);
//            }
//        }
      //above is used to start GCM ,if you don't want to use it, just comment it
        
        
		
        final Account[] accounts = AccountManager.get(this).getAccounts();
        if(accounts.length==0)
        {
        	//Log.d("Account","is null");
        	isLoginAccount=false;
        }else{
        	isLoginAccount=true;
        }
//        for (Account account : accounts) {
//        	Log.d("AccountName",account.name);
//        }
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
//        	saveFilePath = FileTool.getSDPath()+saveFilePath;
//        }
        
		myHandler = new MyHandler();
		//used to get an unique deviceId to identify user
		deviceId = getDeviceId();
		//*************************
		

//		app_back.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
		instrument.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ 
					showDialog("Please insert SD card!");
				}else{
					//this is used to start GCM ,if you don't want to use it, just comment it
//					if(regId==""&&isLoginAccount==true){
//						regId = GCMRegistrar.getRegistrationId(appContext);
//					}
//					if(regId==""){
//						regId = GCMRegistrar.getRegistrationId(appContext);
//					}
					//above is used to start GCM ,if you don't want to use it, just comment it
					boolean choosedApp = false;
					Iterator it = adapter.getIsSelected().entrySet().iterator(); 
					while (it.hasNext()) { 
						Map.Entry entry = (Map.Entry) it.next(); 
						Object key = entry.getKey(); 
						Object value = entry.getValue(); 
						int location=((Integer) key).intValue();
						String Apkpath="";
						String appRealName = "";
						String appPackName="";
						String appVersion = "";
						String Signature="";
						if((Boolean) value){
							System.out.println("Selected apks are: "+"key=" + key + " value=" + value);	
							if (location<userAppInfos.size())
							{
								choosedApp = true;
//								Apkpath=userAppInfos.get(location).getAppPath();
//								System.out.println("Selected paths are: "+userAppInfos.get(location).getAppPath());
//								
//								Signature=userAppInfos.get(location).getSignature();
//								System.out.println("Signature: "+Signature);
//								
//								String name=userAppInfos.get(location).getPackname();
								
								//db_s2.addApp(name, 0);
															
								//write by guanyu	
								Apkpath=userAppInfos.get(location).getAppPath();
								//Log.e("AppManager","AppPath:"+Apkpath);
								appPackName=userAppInfos.get(location).getPackname();
								appVersion=userAppInfos.get(location).getVersion();
								appRealName = userAppInfos.get(location).getName();
								if(Apkpath.contains(".urn.apk")){
									showDialog("APP: "+appRealName+" is instrumented!");
								}else{
									if(isTransfer[location].getB()==false){
										isTransfer[location].setB(true);
										FileInstrumentThread fileInstrumentThread = new FileInstrumentThread(Apkpath,appRealName,appPackName,appVersion,regId,deviceId,isLoginAccount,isTransfer[location],location);
										new Thread(fileInstrumentThread).start();
									}else{
										isTransfer[location].setB(false);
									}						
								}								
								//**************
								
							}
//							else
//								Apkpath=systemAppInfos.get(location-userAppInfos.size()).getAppPath();
//								System.out.println("Selected paths are: "+systemAppInfos.get(location-userAppInfos.size()).getAppPath());
						}
					}
					if(choosedApp==false){
						showDialog("Please choose app to instrument!");
					}
					//GCMRegistrar.unregister(appContext);
					//showDialog("Upload the apps");
				}
			}
		});
		
		
		lv_appmanger.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}

			/**
			 * 
			 * @param view
			 * @param firstVisibleItem 
			 *            .
			 * @param visibleItemCount
			 * @param totalItemCount
			 */
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int position = lv_appmanger.getFirstVisiblePosition();
//				if (userAppInfos != null&& systemAppInfos!=null) {
				if (userAppInfos != null) {					
					if (position < userAppInfos.size()) {
						tv_app_manager_status.setText("User Apps amount: "
								+ userAppInfos.size());
					} else {
//						tv_app_manager_status.setText("System Apps amount: "
//								+ systemAppInfos.size());
					}
				}
			}
		});
		lv_appmanger.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) arg1.getTag(); 
				holder.checkbox.toggle();  

				adapter.getIsSelected().put(arg2, holder.checkbox.isChecked());
			}
		});

	}
    private void showDialog(String mess){  
        new AlertDialog.Builder(this).setTitle("Message")  
            .setMessage(mess)  
            .setNegativeButton("okay",new DialogInterface.OnClickListener(){       
                public void onClick(DialogInterface dialog, int which){}  
            })  
            .show();  
    }
	/**
	 * fillData
	 */
	private void fillData() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				appinfos = AppInfoProvider
						.getAppInfos(getApplicationContext());
				//show installed app's info
				userAppInfos = new ArrayList<AppInfo>();
//				systemAppInfos = new ArrayList<AppInfo>();
//			    appTransferProgress = new ArrayList<ProgressBar>();
//			    appTransferText = new ArrayList<TextView>();
				int status = -1;
				for (AppInfo info : appinfos) {

					if (info.isUserapp()) {
						uninstrumented = true;
						status=db_s2.findStatus(info.getName());
						if(status==0){
							uninstrumented=false;
						}						
						if(uninstrumented){
						
							userAppInfos.add(info);

						}
//						System.out.println("Selected paths are: "+info.getName());
					} else {
//						systemAppInfos.add(info);
					}
//print out apk path					
//					System.out.println("apk path"+info.getAppPath());
				}
				
				//write by guanyu
				//Log.d("appNum: ",userAppInfos.size()+"");
				isTransfer = new BooleanTool[userAppInfos.size()];
				//Log.d("isTransfer: ",isTransfer.length+"");
				for (int j=0;j<isTransfer.length;j++){
					isTransfer[j]=new BooleanTool(false);
				}
				
//			    for(ProgressBar p:appTransferProgress){
//		    	p.setProgress(0);
//			    }
//			    for(TextView t:appTransferText){
//			    	t.setText("Upload %0");
//			    }				
//				for (int i=0;i<isTransfer.length;i++){
//					Log.d("BooleanTool: ",isTransfer[i].toString());
//				}
				//***************
				
				return null;
			}

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				ll_loading.setVisibility(View.INVISIBLE);
				adapter = new AppManagerAdapter();
				lv_appmanger.setAdapter(adapter);
				super.onPostExecute(result);
			}

		}.execute();

	}

	public class AppManagerAdapter extends BaseAdapter {
		private HashMap<Integer,Boolean> isSelected;
	    private HashMap bar_value; 
	    private HashMap tex_value;
//		List<Boolean> mChecked;
		public AppManagerAdapter(){
			isSelected = new HashMap<Integer, Boolean>(); 
		    bar_value = new HashMap<Integer, String>(); 
		    tex_value = new HashMap<Integer, String>();			
//			mChecked= new ArrayList<Boolean>();
			for(int i=0;i<getCount();i++){ 
//				mChecked.add(false); 
				getIsSelected().put(i,false);
				bar_value.put(i, "0");
				tex_value.put(i, "Stop 0%");				
			}
			
		}

		public int getCount() {
//			return userAppInfos.size() + systemAppInfos.size();
			return userAppInfos.size();			
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
				
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_app_item, null);
				holder.iv = (ImageView) view
						.findViewById(R.id.iv_app_item_icon);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_item_location);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_app_item_name);
				holder.checkbox = (CheckBox) view
						.findViewById(R.id.cb_setting_view_status);	
				holder.transfer_progressBar = (ProgressBar) view
						.findViewById(R.id.app_transfer_progressBar);
				holder.transfer_text = (TextView) view
						.findViewById(R.id.app_transfer_text);

//				appTransferProgress.add(holder.transfer_progressBar);
//				appTransferText.add(holder.transfer_text);
				
//				holder.tv_version = (TextView) view
//						.findViewById(R.id.tv_app_item_version);
				
				view.setTag(holder);			 
			}
			final int p=position;
			
			AppInfo appinfo;
			if (position < userAppInfos.size()) {
				appinfo = userAppInfos.get(position);				

	//				System.out.println("positionInteger"+positionInteger.intValue());

					if(!loc_bar.contains(position)){
						loc_bar.add(position);
//						System.out.println("appinfo"+position+userAppInfos.size());
						appTransferProgress.add(holder.transfer_progressBar);
						appTransferText.add(holder.transfer_text);	
					}				

				
			} else {
				appinfo =null;
//				int newposition = position - userAppInfos.size();
//				appinfo = systemAppInfos.get(newposition);
			}

			holder.iv.setImageDrawable(appinfo.getIcon());
			if (appinfo.isInrom()) {
				holder.tv_location.setText("Memory");
			} else {
				holder.tv_location.setText("SD card");
			}
			holder.tv_name.setText(appinfo.getName());
			holder.checkbox.setChecked(getIsSelected().get(position));	
//			if(holder.checkbox.isChecked()){
//				holder.transfer_progressBar.setProgress(Integer.parseInt(bar_value.get(position).toString()));
////				System.out.println("(Integer) bar_value.get(position)"+position);
//				holder.transfer_text.setText(tex_value.get(position).toString());				
////				holder.transfer_progressBar.setProgress(appTransferProgress.get(position).getProgress());
////				holder.transfer_text.setText(appTransferText.get(position).getText());				
			if(!bar_value.get(position).toString().equalsIgnoreCase("0")){
				holder.transfer_progressBar.setProgress(Integer.parseInt(bar_value.get(position).toString()));
//				System.out.println("(Integer) bar_value.get(position)"+position);
				holder.transfer_text.setText(tex_value.get(position).toString());				
//				holder.transfer_progressBar.setProgress(appTransferProgress.get(position).getProgress());
//				holder.transfer_text.setText(appTransferText.get(position).getText());				
			}else if(appinfo!=null){
				int appStatus = db_s2.findStatus(appinfo.getName());
				if(appStatus!=-1){
					if(appStatus>100){
						appStatus=appStatus-100;
						holder.transfer_progressBar.setProgress(appStatus);
						holder.transfer_text.setText("Downloaded "+appStatus+"%");
					}else{
						holder.transfer_progressBar.setProgress(appStatus);
						holder.transfer_text.setText("Uploaded "+appStatus+"%");
					}
				}else{
					holder.transfer_progressBar.setProgress(0);
					holder.transfer_text.setText("Stop 0%");
				}
//				holder.transfer_progressBar.setProgress(0);
//				holder.transfer_text.setText("Stop 0%");
			}

			
			return view;
		}
		/**
		 * isChecked
		 * @return
		 */
		public HashMap<Integer,Boolean> getIsSelected() {
			return isSelected;
		}
		/**
		 * setChecked
		 * @param checked
		 */
		public void setIsSelected(HashMap<Integer,Boolean> isSelected) {
			this.isSelected = isSelected;  
		}		

	}

	static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_location;
		CheckBox checkbox;
		ProgressBar transfer_progressBar;
		TextView transfer_text;
//		TextView tv_version;
	}

	/**
	 * getAvailSD
	 * 
	 * @return
	 */
	private String getAvailSD() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize(); // get the size of each available data set
		long availableBlocks = stat.getAvailableBlocks();// get the number of available data sets

		long size = blockSize * availableBlocks;
		return Formatter.formatFileSize(this, size);
	}

	/**
	 * getAvailRom
	 * 
	 * @return
	 */
	private String getAvailRom() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = blockSize * availableBlocks;
		return Formatter.formatFileSize(this, size);
	}
	

	/**
	 * write by guanyu
	 * 
	 * @return
	 */
	private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
    
    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
        }
    };
	
    /**
     * Used to get deviceId, which is used to represent User
     * */
    private String getDeviceId(){
    	TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
    	
        UUID deviceUuId = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuId.toString();
    }
    
    /**
     * 接受消息,处理消息 ,此Handler会与当前主线程一块运行
     * */
    public class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle b = msg.getData();
            String function = "";
            function = b.getString("function");
            Log.d("Myhandler",function);
			if(function.equalsIgnoreCase("open_file")){
				String filename = "";
				filename = b.getString("filename");
				//Log.d("Myhandler","open_file "+filename);
				String savePath="";
				savePath=FileTool.saveFilePath;
				savePath+=filename;
				//Log.d("Myhandler",filename);
				File installFile = new File(savePath);
				String packageName="";
				String appPackName = "";
				String appVersion ="";
				for (int i=0;i<userAppInfos.size();i++){
					appPackName = userAppInfos.get(i).getPackname();
					appVersion = userAppInfos.get(i).getVersion();
					if(appPackName==null){
						appPackName = "null";
					}
					if(appVersion==null){
						appVersion = "null";
					}
					if(filename.contains(appPackName)&&filename.contains(appVersion)){
						packageName= userAppInfos.get(i).getPackname();
						//Log.d("Myhandler",packageName);
						break;
					}
				}
				//openFile(installFile);
				uninstallAPK(packageName,installFile);
			}else if(function.equalsIgnoreCase("install_file")){
				System.out.println("AppManger install_file");
				
				List<AppInfo> oldUserAppInfos = new ArrayList<AppInfo>();
				for(AppInfo app:userAppInfos){
					oldUserAppInfos.add(app);
				}
				for(int j=0;j<isTransfer.length;j++){
					isTransfer[j].setB(false);
					adapter.bar_value.put(j, "0");
					adapter.tex_value.put(j, "Stop 0%");				
				}
				
//			    for(ProgressBar p:appTransferProgress){
//			    	p.setProgress(0);
//			    }
//			    for(TextView t:appTransferText){
//			    	t.setText("Upload %0");
//			    }
				List<AppInfo> newAppInfos = new ArrayList<AppInfo>();
				List<AppInfo> newUserAppInfos = new ArrayList<AppInfo>();
				newAppInfos = AppInfoProvider
						.getAppInfos(getApplicationContext());
				for (AppInfo info : newAppInfos) {

					if (info.isUserapp()) {
						uninstrumented = true;
						int status=db_s2.findStatus(info.getName());
						if(status==0){
							uninstrumented=false;
						}
						
						if(uninstrumented){
						
							newUserAppInfos.add(info);
						}
					}
				}			
//				
//				isTransfer = new BooleanTool[userAppInfos.size()];
//				for (int i=0;i<isTransfer.length;i++){
//					isTransfer[i]=new BooleanTool(false);
//				}
////				showDialog("You have installed a new app, you can instrument it!");
//				Log.e("AppManager","UserAppNum"+userAppInfos.size());
//				Log.e("AppManager","NewUserAppNum"+oldUserAppInfos.size());
				if(newUserAppInfos.size()>oldUserAppInfos.size()){
					int num1=0,num2=0;
					for (num1=0;num1<newUserAppInfos.size();num1++) {
						for (num2=0;num2<oldUserAppInfos.size();num2++) {
							if(newUserAppInfos.get(num1).getPackname().contains(oldUserAppInfos.get(num2).getPackname())&&newUserAppInfos.get(num1).getName().contains(oldUserAppInfos.get(num2).getName())){
								break;
							}
						}
						if(num2==oldUserAppInfos.size()){
							AppInfo newAppInfo = newUserAppInfos.get(num1);
							//Log.e("AppManager","APP: "+newAppInfo.getName()+" has been install!");
							
							if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ 
								showDialog("Please insert SD card!");
							}else{
								final String apkPath=newAppInfo.getAppPath();
								//Log.e("AppManager","AppPath:"+Apkpath);
								final String appName=newAppInfo.getPackname();
								final String appVersion=newAppInfo.getVersion();
								final String appRealName = newAppInfo.getName();
								final int location1 = num1;
								
								String savePath="";
								savePath=FileTool.saveFilePath;
								savePath+=appName+"_"+appVersion+".urn.apk";
								File installFile = new File(savePath);
								if(!installFile.exists()){
									AlertDialog alertDialog = new AlertDialog.Builder(AppManagerActivity.this). 
											setIcon(R.drawable.ic_launcher).
							                setTitle("Instrument?"). 
							                setMessage("APP: "+ newAppInfo.getName()+ " has installed. Do you want to instrument now?"). 
							                setIcon(R.drawable.ic_launcher). 
							                setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
							                    
							                    @Override 
							                    public void onClick(DialogInterface dialog, int which) { 
							                        // TODO Auto-generated method stub  
													if(apkPath.contains(".urn.apk")){
														showDialog("APP: "+appRealName+" is instrumented!");
													}else{
														if(isTransfer[location1].getB()==false){
															isTransfer[location1].setB(true);
															FileInstrumentThread fileInstrumentThread = new FileInstrumentThread(apkPath,appRealName,appName,appVersion,regId,deviceId,isLoginAccount,isTransfer[location1],location1);
															new Thread(fileInstrumentThread).start();
														}else{
															isTransfer[location1].setB(false);
														}						
													}								
							                    } 
							                }). 
							                setNegativeButton("No", new DialogInterface.OnClickListener() { 
							                     
							                    @Override 
							                    public void onClick(DialogInterface dialog, int which) { 
							                        // TODO Auto-generated method stub  
							                    	
							                    } 
							                }). 
							                create(); 
							        alertDialog.show(); 
								}else{
									if(db_s2.findStatus(appRealName)!=0){
										db_s2.deleteStatus(appRealName);
										db_s2.addApp(appRealName, 0);	
										Message newmsg = new Message();
						                Bundle newb = new Bundle();
						                newb.putString("function", "refresh_page");
						                newmsg.setData(newb);
						                MainPage.myHandler.sendMessage(newmsg);
									}
								}
							}							
						}
					}
				}
				fillData();
//				appTransferProgress.clear();
//				appTransferText.clear();
				adapter.notifyDataSetChanged();
			}else if(function.equalsIgnoreCase("uninstall_file")){
//				int location=0;
//				for(location=0;location<isTransfer.length;location++){
//					isTransfer[location].setB(false);
//				}
//				adapter.notifyDataSetChanged();
//				showDialog("You have installed a new app, you can instrument it!");
				System.out.println("AppManger uninstall_file");
				for(int j=0;j<isTransfer.length;j++){
					isTransfer[j].setB(false);
					adapter.bar_value.put(j, "0");
					adapter.tex_value.put(j, "Stop 0%");			
				}
				List<AppInfo> newUserAppInfos;
				List<AppInfo> newAppInfos;
				boolean uninstrumented = true;
				newAppInfos = AppInfoProvider
						.getAppInfos(getApplicationContext());
				//show installed app's info
				newUserAppInfos = new ArrayList<AppInfo>();
//				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : newAppInfos) {
					if (info.isUserapp()) {	
						uninstrumented = true;
						int status=db_s2.findStatus(info.getName());
						if(status==0){
							uninstrumented=false;
						}						
						if(uninstrumented){
							newUserAppInfos.add(info);
						}
					}
				}
				//Log.e("AppManager","UserAppNum"+userAppInfos.size());
				//Log.e("AppManager","NewUserAppNum"+newUserAppInfos.size());
				int num1=0,num2=0;
				for (num1=0;num1<userAppInfos.size();num1++) {
					for (num2=0;num2<newUserAppInfos.size();num2++) {
						if(userAppInfos.get(num1).getPackname().contains(newUserAppInfos.get(num2).getPackname())&&userAppInfos.get(num1).getName().contains(newUserAppInfos.get(num2).getName())){
							break;
						}
					}
					if(num2==newUserAppInfos.size()){
						//Log.e("AppManager","APP: "+userAppInfos.get(num1).getName()+" has been uninstall!");
						String savePath="";
						savePath=FileTool.saveFilePath;
						savePath+=userAppInfos.get(num1).getPackname()+"_"+userAppInfos.get(num1).getVersion()+".urn.apk";
						//Log.e("AppManager","Apk path: "+savePath);
						File installFile = new File(savePath);
						//Log.e("AppManager","Apk exist: "+installFile.exists());
						if(installFile.exists()){
							openFile(installFile);
						}
						break;
					}
				}				
				
				ArrayList<String> instrumentedAppNames = db_s2.findAllApp();
				boolean appExists = false;
				for(String appNames:instrumentedAppNames){
					System.out.println("Instrumented app: "+appNames);
					appExists = false;
					for (AppInfo info : newAppInfos) {
						if(info.isUserapp()&&info.getName().equalsIgnoreCase(appNames)){
							appExists = true;
						}
					}
					if(appExists==false){
						db_s2.deleteStatus(appNames);
						System.out.println("DeleteAppInfo"+appNames);
						Message newmsg = new Message();
		                Bundle newb = new Bundle();
		                newb.putString("function", "refresh_page");
		                newmsg.setData(newb);
		                MainPage.myHandler.sendMessage(newmsg);
					}
				}
				
//				int oldsize=userAppInfos.size();
				fillData();
				adapter.notifyDataSetChanged();
				//openFile(installFile);
				//uninstallAPK(packageName,installFile);
			}else if(function.equalsIgnoreCase("show_message")){
				String message = b.getString("message");
				showDialog(message);
			}else if(function.equalsIgnoreCase("show_short_message")){
				String message = b.getString("message");
				Toast.makeText(AppManagerActivity.this, message , Toast.LENGTH_SHORT).show();
			}else if(function.equalsIgnoreCase("download_file")){
				String filename = b.getString("filename");
				int filesize = b.getInt("filesize");
				//Log.d("Myhandler","downloadFile "+filename);
				int location=0;
				for (location=0;location<userAppInfos.size();location++){
					if(filename.contains(userAppInfos.get(location).getPackname())&&filename.contains(userAppInfos.get(location).getVersion())){
						if(isTransfer[location].getB()==false){
							isTransfer[location].setB(true);
							//Log.d("start download file:", ""+filename);
				        	FileDownloadThread fileDownloadThread = new FileDownloadThread(filename,FileTool.saveFilePath,isTransfer[location],location,filesize);
				    		new Thread(fileDownloadThread).start();	
							break;	
						}
					}
				}
			}else if(function.equalsIgnoreCase("transfer_file")){
	            int location = b.getInt("location");
//				System.out.println("transfer_file_location"+location);
	            String filesize = "";
	            filesize = b.getString("filesize");
	            String transferType = "";
	            transferType = b.getString("transfer_type");
	            int transferSize = b.getInt("transfer_size");
//	            int precentage=(int)(100*transferSize/Double.parseDouble(filesize));
	            if(location<appTransferText.size()){
//	            if(precentage<=100){	            
//					appTransferProgress.get(location).setProgress(transferSize);
//					appTransferProgress.get(location).setMax(100);
//					System.out.println("transfer_file_location"+location);
//					appTransferText.get(location).setText(transferType+" "+transferSize+"% "+filesize);
					adapter.bar_value.put(location, transferSize);
					adapter.tex_value.put(location, transferType+" "+transferSize+"% "+filesize);
					int status = transferSize;
					if(transferType.equalsIgnoreCase("Download")){
						status+=100;
					}
					//db_s2.updateApp(userAppInfos.get(location).getName(), status);
					db_s2.deleteStatus(userAppInfos.get(location).getName());
					db_s2.addApp(userAppInfos.get(location).getName(), status);
	            }
			}
        }
    }
    
	public void openFile(File file) {			
        // TODO Auto-generated method stub
        //Log.d("Install", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
        startActivity(intent);            
	}
    
	public void uninstallAPK(String packageName, File installFile){ 
//		openFile(installFile);
		//Log.d("Uninstall", installFile.getName());
		Uri uri=Uri.parse("package:"+packageName); 
		Intent intent=new Intent(Intent.ACTION_DELETE,uri);
		startActivity(intent);  
	}
}