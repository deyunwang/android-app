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
package com.apkits.android.ui;

import java.util.LinkedList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 常用Adapter
 * @author wangdeyun
 * @param <T>
 */
public class CommonAdapter<T> extends AbstractAdapter<T> {

	/** View缓存 **/
	private List<View> mViewCache = new LinkedList<View>();
	
	/**
	 * </br><b>description : </b>	创建对象
	 * @param inflater
	 * @param creator
	 */
	public CommonAdapter(LayoutInflater inflater, ViewCreator<T> creator) {
		super(inflater, creator);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int pos, View currentView, ViewGroup parent) {
		if(null == currentView){
			View view = mCreator.createView(mInflater, pos, mDataCache.get(pos));
			mViewCache.add(view);
			return view;
		}else{
			return currentView;
		}
		
	}

}
