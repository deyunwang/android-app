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
package com.apkits.android.resource;

import java.io.IOException;
import java.io.InputStream;

import com.apkits.android.common.ISUtil;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * 读取Assets文件夹内的
 * @author wangdeyun
 *
 */
public class ResourceReader {

	/**
	 * </br><b>title : </b>		以InputStream方式读取文件
	 * </br><b>description :</b>以InputStream方式读取Assets文件夹内的文件。
	 * @param c Android环境上下文
	 * @param fileInAssets Assets内的文件
	 * @return IntpuStream对象
	 * @throws IOException 
	 */
	public static InputStream readAssetsAsInputStream(Context c,String fileInAssets) throws IOException{
		return c.getAssets().open(fileInAssets);
	}
	
	/**
	 * </br><b>title : </b>		将res文件读为Drawable对象
	 * </br><b>description :</b>将res文件读为Drawable对象
	 * </br><b>time :</b>		2012-7-15 下午9:13:24
	 * @param c
	 * @param resId
	 * @return
	 */
	public static Drawable readResAsDrawable(Context c,int resId){
		return c.getResources().getDrawable(resId);
	}
	
	/**
	 * </br><b>title : </b>		将res文件读为Bitmap对象
	 * </br><b>description :</b>将res文件读为Bitmap对象
	 * </br><b>time :</b>		2012-7-15 下午9:19:28
	 * @param c
	 * @param resId
	 * @return
	 */
	public static Bitmap readResAsBitmap(Context c,int resId){
		return BitmapFactory.decodeResource(c.getResources(), resId);
	}
	
	/**
	 * <b>description :</b>		读取RAW中的文件为字符串
	 * </br><b>time :</b>		2012-9-14 上午10:08:00
	 * @param c
	 * @param resId
	 * @return
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	public static String readRawAsString(Context c,int resId) throws NotFoundException, IOException{
		return ISUtil.toString(c.getResources().openRawResource(resId));
	}
}
