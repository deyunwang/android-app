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
package com.apkits.android.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * ShareDatabase辅助工具类
 * @author wangdeyun
 *
 */
public class ShareDB {

	/** SharedPreferences实例 **/
	private SharedPreferences mShareDB;
	
	/**
	 * </br><b>description : </b>	初始化，建立一个ShareDatabase
	 * @param context				Android环境上下文
	 * @param dbName				Database名字
	 */
	public ShareDB(Context context, String dbName) {
		mShareDB = context.getSharedPreferences(dbName, Context.MODE_PRIVATE);
	}

	/**
	 * <b>description :</b>		将值保存到ShareDatabase中。只支持基本类型。
	 * </br><b>time :</b>		2012-7-8 下午4:52:04
	 * @param key				KEY
	 * @param val				VALUE
	 */
	public void save(String key, Object val) {
		Editor editor = mShareDB.edit();
		if(val instanceof String){
			editor.putString(key, val.toString());
		}else if(val instanceof Integer){
			editor.putInt(key, (Integer)val);
		}else if(val instanceof Float){
			editor.putFloat(key, (Float)val);
		}else if(val instanceof Long){
			editor.putLong(key, (Long)val);
		}else if(val instanceof Boolean){
			editor.putBoolean(key, (Boolean)val);
		}else{
			throw new IllegalArgumentException("SharedPreferences do not support this Type!");
		}
		editor.commit();
	}
	
	/**
	 * <b>description :</b>		取出Int类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:06
	 * @param key				KEY
	 * @return					如果数据不存在，返回0。
	 */
	public int getInt(String key){
		return getInt(key, 0); 
	}
	
	/**
	 * <b>description :</b>		取出Int类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:11
	 * @param key				KEY
	 * @param deftVal			当数据不存在时返回的默认值
	 * @return					如果数据不存在，返回设定的默认值。
	 */
	public int getInt(String key,int deftVal){
		return mShareDB.getInt(key, deftVal);
	}
	
	/**
	 * <b>description :</b>		取出Boolean类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:15
	 * @param key				KEY
	 * @return					如果数据不存在，返回false。
	 */
	public boolean getBoolean(String key){
		return getBoolean(key, false);
	}

	/**
	 * <b>description :</b>		取出Boolean类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:19
	 * @param key				KEY
	 * @param defValue			默认值
	 * @return					如果数据不存在，返回设定的默认值。
	 */
	public boolean getBoolean(String key,boolean defValue){
		return mShareDB.getBoolean(key, defValue);
	}
	
	/**
	 * <b>description :</b>		取出String类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:23
	 * @param key				KEY
	 * @return					如果数据不存在，返回null。
	 */
	public String getString(String key){
		return getString(key, null);
	}
	
	/**
	 * <b>description :</b>		取出String类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:27
	 * @param key				KEY
	 * @param defValue			默认值
	 * @return					如果数据不存在，返回设定的默认值。
	 */
	public String getString(String key,String defValue){
		return mShareDB.getString(key, defValue);
	}
	
	/**
	 * <b>description :</b>		取出Long类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:31
	 * @param key				KEY
	 * @return					如果数据不存在，返回设定的0。
	 */
	public long getLong(String key){
		return getLong(key, 0L);
	}
	
	/**
	 * <b>description :</b>		取出Long类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:34
	 * @param key				KEY
	 * @param defValue			默认值
	 * @return					如果数据不存在，返回设定的值。
	 */
	public long getLong(String key,Long defValue){
		return mShareDB.getLong(key, defValue);
	}
	
	/**
	 * <b>description :</b>		取出Float类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:38
	 * @param key				KEY
	 * @return					如果数据不存在，返回0
	 */
	public float getFloat(String key){
		return getFloat(key, 0.0f);
	}
	
	/**
	 * <b>description :</b>		取出Float类型数据
	 * </br><b>time :</b>		2012-8-22 下午10:47:42
	 * @param key				KEY
	 * @param defValue			默认值
	 * @return					如果数据不存在，返回设定的值
	 */
	public float getFloat(String key,Float defValue){
		return mShareDB.getFloat(key, defValue);
	}
}