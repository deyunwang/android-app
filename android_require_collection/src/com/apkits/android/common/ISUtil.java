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
package com.apkits.android.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * 对InputStream进行各类型数据的转换。转换类型包括Drawabel,Bitmap,String,byte[]等。
 * @author wangdeyun
 */
public class ISUtil {
	
	/**
	 * <b>description :</b>	将InputStream流转换成BitmapDrawable。BitmapDrawable是Drawable的直接子类，可用于Drawable对象
	 * @param is 			InputStream对象
	 * @return 			BitmapDrawable对象
	 * @throws 			IOException 
	 */
	public static BitmapDrawable toBitmapDrawable(InputStream is) throws IOException{
		BitmapDrawable bitmapDrawable = new BitmapDrawable(is);
		is.close();
		return bitmapDrawable;
	}
	
	/**
	 * </br><b>description :</b>	将InputStream流转换成Bitmap对象。
	 * @param is 					InputStream对象
	 * @return 					Bitmap对象
	 * @throws IOException 
	 */
	public static Bitmap toBitmap(InputStream is) throws IOException{
	    if( null == is) return null;
		return toBitmapDrawable(is).getBitmap();
	}
	
	/**
	 * <b>description :</b>		将InputStream转换成StringBuffer对象。
	 * @param is 				InputStream对象
	 * @return 				StringBuffer对象
	 * @throws IOException 
	 */
	public static StringBuffer toStringBuffer(InputStream is) throws IOException{
	    if( null == is) return null;
		StringBuffer buffer = new StringBuffer();
		byte[] cache = new byte[ 1 * 1024 ];
		for (int len; (len = is.read(cache)) != -1;) {
			buffer.append(new String(cache, 0, len));
		}
		is.close();
		return buffer;
	}
	
	/**
	 * <b>description :</b>	将InputStream转换成String对象。
	 * @param is 					InputStream对象
	 * @return 					String对象
	 * @throws IOException 
	 */
	public static String toString(InputStream is) throws IOException{
	    if( null == is) return null;
		return toStringBuffer(is).toString();
	}
	
	/**
	 * <b>description :</b>		将InputStream转换成字节数组。
	 * @param is 				InputStream对象
	 * @return 				字节数组
	 * @throws IOException 
	 */
	public static byte[] toByteArray(InputStream is) throws IOException{
	    if( null == is) return null;
		byte[] cache = new byte[1 * 1024];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int length; (length = is.read(cache)) != -1;) {
		    buffer.write(cache, 0, length);
        }
		is.close();
		return buffer.toByteArray();
	}
}