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
package com.apkits.android.resource;

import java.io.File;

import android.os.Environment;

/**
 * SD卡管理器
 * @author wangdeyun
 *
 */
public class SDCard {

	/** SDCard是否可用 **/
	public static boolean IS_MOUNTED = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	
	/** SDCard的根路径 **/
	private static String SDCARD_PATH;
	
	/**
	 * <b>description :</b>		取得SD卡路径，以/结尾
	 * </br><b>time :</b>		2012-9-13 下午3:58:55
	 * @return
	 */
	public static String getSDCardPath(){
		if(!IS_MOUNTED) return null;
		if(null != SDCARD_PATH) return SDCARD_PATH;
		
		File path = Environment.getExternalStorageDirectory(); 
		String SDCardPath = path.getAbsolutePath();
		SDCardPath += SDCardPath.endsWith(File.separator) ? "" : File.separator;
		SDCARD_PATH = SDCardPath;
		return SDCardPath;
	}
}
