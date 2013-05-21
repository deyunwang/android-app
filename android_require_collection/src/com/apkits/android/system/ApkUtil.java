/**
 * Copyright (C) 2012 ToolkitForAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apkits.android.system;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

/**
 * APK工具
 * @author wangdeyun
 *
 */
public class ApkUtil {

	/**
	 * <b>description :</b>		判断APK包是否安装
	 * </br><b>time :</b>		2012-8-11 下午8:39:44
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean exist(Context context, String packageName) {
		if (null == packageName || "".equals(packageName)) {
			throw new IllegalArgumentException("Package name cannot be null or empty !");
		}
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,PackageManager.GET_UNINSTALLED_PACKAGES);
			return null != info;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * <b>description :</b>		安装APK
	 * </br><b>time :</b>		2012-8-11 下午8:40:01
	 * @param activity
	 * @param apkFile
	 */
	public static void install(Activity activity, File apkFile) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
		activity.startActivity(intent);
	}
	
	/**
	 * <b>description :</b>		启动一个Intent
	 * </br><b>time :</b>		2012-8-11 下午8:48:59
	 * @param activity
	 * @param packageName
	 */
	public static void call(Activity activity,String packageName){
		Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
		if( null != intent ){
			activity.startActivity(intent);
		}
	}
}
