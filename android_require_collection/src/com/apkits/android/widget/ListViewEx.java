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
package com.apkits.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 展开其高度的ListView
 * @author wangdeyun
 *
 */
public class ListViewEx extends ListView {

	public ListViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ListViewEx(Context context) {
		super(context);
	}
	
	protected int mCurrentPage = 1;
	
	/**
	 * @ClassName: OnPullNPushListener 
	 * @Description: 下拉或者上拉时回调的接口
	 * @author yongjia.chen
	 * @date 2012-8-20 下午3:41:46
	 *
	 */
	public interface OnPullNPushListener {
	    /**
	     * @Title: pullAtBottom
	     * @Description: 如果下拉更新成功，返回true。
	     * @return
	     */
	    boolean pullAtBottom();
	    
	    /**
	     * @Title: pushAtTop
	     * @Description: TODO
	     */
	    void pushAtTop();
	}
	
	/**
	 * <b>description :</b>		将ListView的高度全部展开
	 * </br><b>time :</b>		2012-8-16 下午10:27:45
	 */
	public void expandHeight() {
		int totalHeight = 0;
		ListAdapter adapter = getAdapter();
		for (int i = 0, len = adapter.getCount(); i < len; i++) {
			View listItem = adapter.getView(i, null, this);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.height = totalHeight + (this.getDividerHeight() * (this.getCount() - 1));
		setLayoutParams(params);
	}
	
	/**
	 * @Title: enablePullNPush
	 * @Description: 监听上拉和下拉
	 * @param listener
	 */
	public void enablePullNPush(final OnPullNPushListener listener){
	    setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if( OnScrollListener.SCROLL_STATE_IDLE == scrollState ){
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        if(listener.pullAtBottom()) mCurrentPage++;
                    }else{
                        listener.pushAtTop();
                    }
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
            }
        });
	}
	
	/**
	 * @Title: getPageCount
	 * @Description: 取得页码
	 * @return
	 */
	public int getPageCount() {
	    return mCurrentPage;
	}
	
	/**
	 * @Title: setPageCount
	 * @Description: 设置页码
	 * @param pageNum
	 */
	public void setPageCount(int pageNum){
	    mCurrentPage = pageNum;
	}
	
}
