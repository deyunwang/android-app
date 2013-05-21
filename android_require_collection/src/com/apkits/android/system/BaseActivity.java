/**
 * Copyright (C) 2012 TookitForAndroid Project
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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Activity特性辅助工具
 * @author wangdeyun
 *
 */
public class BaseActivity {

	/** Activity引用  */
	public Activity self;
	
	/** Context引用 */
	public Context context;
	
	/**  双击退出模块 */
	private DClickExit mDClickExit;
	
	/** Activity栈 **/
	private static Stack<Activity> ActivityStack = new Stack<Activity>();
	
	/**
	 * @param self
	 */
	public BaseActivity(Activity act){
		self = act;
		context = self;
	}
	
	/**
	 * 启用双击返回键退出程序
	 */
	public void enabledDClickExit(){
		mDClickExit = new DClickExit(self);
	}

	/**
	 * 检查按键
	 * @param keyCode
	 * @return
	 */
	public boolean checkExist(int keyCode) {
		if( null != mDClickExit ){
			return mDClickExit.doubleClickExit(keyCode);
		}else{
			return false;
		}
	}
	
	/**
	 * 将Activity压力栈中
	 * @param activity
	 */
	public static void pushActivity(Activity activity){
		ActivityStack.add(activity);
	}
	
	/**
	 * 将顶端的Activity弹出栈顶
	 * @return
	 */
	public static Activity popActivity(){
		return ActivityStack.lastElement();
	}
	
	/**
	 * Activity栈回退到某个Activity
	 * @param target
	 */
	public static void rollbackTo(Class<? extends Activity> target){
		for(Activity activity : ActivityStack){
			if(activity.getClass().equals(target)){
				//已经是目标Activity，退出循环
				break;
			}else{
				//不是目标栈，关闭它
				activity.finish();
			}
		}
	}
	
	/**
	 * 设置Activity全屏显示。
	 * @param activity 			Activity引用
	 * @param isFull 			true为全屏，false为非全屏
	 */
	public static void setFullScreen(Activity activity,boolean isFull){
		hideTitleBar(activity);
		Window window = activity.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		if (isFull) {
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
			window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.setAttributes(params);
			window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}
	
	/**
	 * 获取系统状态栏高度
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity){
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
		    int dpHeight = Integer.parseInt(field.get(obj).toString());
		    int pxHeight = activity.getResources().getDimensionPixelSize(dpHeight);
		    return pxHeight;
		} catch (Exception e1) {
		    e1.printStackTrace();
		    return 0;
		} 
		
	}
	
	/**
	 * 隐藏Activity的系统默认标题栏
	 * @param activity Activity对象
	 */
	public static void hideTitleBar(Activity activity){
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	/**
	 * 强制设置Actiity的显示方向为垂直方向。
	 * @param activity 			Activity对象
	 */
	public static void setScreenVertical(Activity activity){
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	/**
	 * 强制设置Actiity的显示方向为横向。
	 * @param activity 			Activity对象
	 */
	public static void setScreenHorizontal(Activity activity){
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	/**
     * 隐藏软件输入法
	 * @param activity
	 */
	public static void hideSoftInput(Activity activity){
	    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	/**
	 * 使UI适配输入法
	 * @param activity
	 */
	public static void adjustSoftInput(Activity activity) {
		activity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	/**
	 * 跳转到某个Activity
	 * @param activity			本Activity
	 * @param targetActivity	目标Activity的Class
	 */
	public static void switchTo(Activity activity,Class<? extends Activity> targetActivity){
		switchTo(activity, new Intent(activity,targetActivity));
	}
	
	/**
	 * 根据给定的Intent进行Activity跳转
	 * @param activity			Activity对象
	 * @param intent			要传递的Intent对象
	 */
	public static void switchTo(Activity activity,Intent intent){
		activity.startActivity(intent);
		activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}
	
	/**
	 * 带参数进行Activity跳转
	 * @param activity			Activity对象
	 * @param targetActivity	目标Activity的Class
	 * @param params			跳转所带的参数
	 */
	public static void switchTo(Activity activity,Class<? extends Activity> targetActivity,
	        Map<String,Object> params){
			Intent intent = new Intent(activity,targetActivity);
			if( null != params ){
				for(Map.Entry<String, Object> entry : params.entrySet()){
					IntentUtil.setValueToIntent(intent, entry.getKey(), entry.getValue());
				}
			}
			switchTo(activity, intent);
	}
	
	/**
	 * 显示Toast消息，并保证运行在UI线程中
	 * @param activity
	 * @param message
	 */
	public static void show(final Activity activity,final String message){
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
