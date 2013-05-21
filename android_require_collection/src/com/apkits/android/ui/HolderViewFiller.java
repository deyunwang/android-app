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

import java.util.List;

import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListAdapter;

/**
 * 可继承自AbsListView进行子项填充的工具类。主要用于ListView，GridView等填充。
 * @author wangdeyun
 *
 * @param <T>
 */
public class HolderViewFiller<T> {

	/** 从XML中创建对象 **/
	private LayoutInflater mInflater;
	
	/** View创建器 **/
	private ViewCreator<T> mCreator;

	public HolderViewFiller(LayoutInflater inflater, ViewCreator<T> creator) {
		mInflater = inflater;
		mCreator = creator;
	}

	/**
	 * </br><b>description :</b>将数据更新到View中
	 * </br><b>time :</b> 2012-7-18 下午7:41:55
	 * @param view
	 * @param data
	 */
	public void update(AbsListView view, List<T> data) {
		HolderAdapter<T> holderAdapter = exportAdapter(view);
		if (null != holderAdapter) {
			holderAdapter.update(data);
		}
	}

	/**
	 * </br><b>description :</b>添加数据集 
	 * </br><b>time :</b> 2012-7-18 下午8:16:38
	 * 
	 * @param view
	 * @param set
	 */
	public void add(AbsListView view, List<T> set) {
		HolderAdapter<T> holderAdapter = exportAdapter(view);
		if (null != holderAdapter) {
			holderAdapter.add(set);
		}
	}

	/**
	 * </br><b>title : </b> 添加数据 
	 * </br><b>description :</b>添加数据 
	 * </br><b>time :</b> 2012-7-18 下午8:16:55
	 * 
	 * @param view
	 * @param item
	 */
	public void add(AbsListView view, T item) {
		HolderAdapter<T> holderAdapter = exportAdapter(view);
		if (null != holderAdapter) {
			holderAdapter.add(item);
		}
	}

	@SuppressWarnings("unchecked")
	public HolderAdapter<T> exportAdapter(AbsListView view) {
		ListAdapter adapter = view.getAdapter();
		HolderAdapter<T> holderAdapter = null;
		try {
			holderAdapter = (null == adapter) ? null : (HolderAdapter<T>) adapter;
			if (null == holderAdapter) {
				holderAdapter = new HolderAdapter<T>(mInflater, mCreator);
				view.setAdapter(holderAdapter);
			}
		} catch (ClassCastException ex) {
			throw new IllegalArgumentException(
					String.format("Adapter in View(%s) is not a HolderAdapter!", view.getClass().getName()));
		}
		return holderAdapter;
	}
}
