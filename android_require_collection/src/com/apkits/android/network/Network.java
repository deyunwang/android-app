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
package com.apkits.android.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * 网络检测
 * @author wangdeyun
 *
 */
public class Network {

	/**
	 * 网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		return !(networkinfo == null || !networkinfo.isAvailable());
	}
	
	/**
	 * Wifi是否启用
	 * @param c
	 * @return
	 */
	public static boolean isWIFIActivate(Context c) {
		return ((WifiManager) c.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
	}
	
	/**
	 * 修改WIFI状态
	 * @param c
	 * @param status
	 */
	public static void changeWIFIStatus(Context c, boolean status) {
		((WifiManager) c.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(status);
	}
	
	/**
	 * 提示打开网络设置
     * @param c
     * @param status
     */
	public static void showNetworkSetting(final Activity activity){
        AlertDialog.Builder builder = new Builder(activity);
        builder.setTitle("网络设置提示")
            .setMessage("网络连接不可用,是否进行设置?")
            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
             }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
             })
             .show();
	}
}
