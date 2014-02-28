package com.version1.uranine.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import com.version1.uranine.domain.AppInfo;

/**
 * get info of all installed apps
 * @author Administrator
 *
 */
public class AppInfoProvider {

	public static List<AppInfo> getAppInfos(Context context){
		//1.get info of all installed apps
		PackageManager pm = context.getPackageManager();
//		List<PackageInfo> signs = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);		
		List<PackageInfo>  packinfos =	pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();

		for(PackageInfo packinfo: packinfos){
			AppInfo appinfo = new AppInfo();
			String packname = packinfo.packageName;
			appinfo.setPackname(packname);
			String version = packinfo.versionName;
			appinfo.setVersion(version);
			Drawable icon = packinfo.applicationInfo.loadIcon(pm);
			appinfo.setIcon(icon);
			String name = packinfo.applicationInfo.loadLabel(pm).toString();
			appinfo.setName(name);
			String  signature ="";
			try {
				PackageInfo signs = pm.getPackageInfo(packname, PackageManager.GET_SIGNATURES);
				signature=signs.signatures[0].toString();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  			
//			Signature[] signs = packinfo.signatures;            
//			Signature sign = signs[0];    
//			
//			String signature = sign.toString(); 
			
//			String signature = packinfo.pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
			appinfo.setSignature(signature);	
 			
			
			//apk path
			String appPath=packinfo.applicationInfo.sourceDir;
			appinfo.setAppPath(appPath);
			
			
			if((packinfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) !=0){
				//installed in Rom
				appinfo.setInrom(false);
			}else{
				appinfo.setInrom(true);
			}
			if( (packinfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1 ){
				appinfo.setUserapp(false);
			}else{
				appinfo.setUserapp(true);
			}
			appInfos.add(appinfo);
		}
		return appInfos;
	}
}
