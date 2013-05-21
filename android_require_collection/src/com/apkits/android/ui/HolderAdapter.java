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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现HolderView模式的Adapter
 * @author wangdeyun
 *
 * @param <E>
 */
public class HolderAdapter<E> extends AbstractAdapter<E> {

	/**
	 * </br><b>description : </b>	创建对象
	 * @param inflater
	 * @param creator
	 */
	public HolderAdapter(LayoutInflater inflater, ViewCreator<E> creator) {
		super(inflater, creator);
	}

	/**
	 * </br><b>name : </b>		ViewHolder
	 * </br><b>description :</b>一个持有View引用对象的静态类，用以减少View的创建次数
	 * </br>@author : 			桥下一粒砂
	 * </br><b>e-mail : </b>	chenyoca@gmail.com
	 * </br><b>weibo : </b>		@桥下一粒砂
	 * </br><b>date : </b>		2012-7-14 上午12:31:56
	 *
	 */
	private static class ViewHolder{
		public View view;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Holder View模式实现
		if (convertView == null) {
			ViewHolder holder = new ViewHolder();
			convertView = mCreator.createView(mInflater, position,getItem(position));
			holder.view = convertView;
			convertView.setTag(holder);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			//释放当前的View的数据
			mCreator.releaseView(convertView,  getItem(position));
			//将新数据更新到HodlerView中
			mCreator.updateView(holder.view, position, getItem(position));
		}
		return convertView;
	}
}
