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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/** 
 * @ClassName: SingleSortBar 
 * @Description: TODO
 * @author yongjia.chen
 * @date 2012-8-17 上午9:28:14
 *  
 */

public class SingleSortBar extends LinearLayout {

    public SingleSortBar(Context context) {
        super(context);
    }

    public SingleSortBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /** 按钮列表 */
    private List<Button> mSortButtons = new ArrayList<Button>();
    
    /** 数据列表 */
    private List<String>[] mDataArrays;
    
    /** 被选择的索引 */
    private int[] mSelectedIndexs;
    
    /** 提示标题 */
    private static final String TITLE = "请选择：";
    
    /** 选择监听回调 */
    private OnSortItemSelectedListener mListener;
    
    /**
     * @ClassName: OnSortItemSelectedListener 
     * @Description: TODO
     * @author yongjia.chen
     * @date 2012-8-17 下午2:49:08
     *
     */
    public interface OnSortItemSelectedListener{
        /**
         * @Title: isSelecteEnable
         * @Description: 工具条是否可用
         * @return
         */
        boolean isSelecteEnable();
        /**
         * @Title: onCurrentSelected
         * @Description: 当前选择
         * @param column    所在排序工具条的列
         * @param index     所在列的序号
         * @param data      所在列的数据
         */
        void onCurrentSelected(int column,int index,String data);
        
        /**
         * @Title: totalSelection
         * @Description: 全部的选择
         * @param selectedIndexs
         * @param data
         */
        void totalSelection(int[] selectedIndexs,String[] data);
    };
    
    /**
     * @ClassName: ItemClickListener 
     * @Description: 点击处理
     * @author yongjia.chen
     * @date 2012-8-17 下午2:38:49
     */
    private class ItemClickListener implements DialogInterface.OnClickListener{
        private int mIndex = 0;
        public ItemClickListener(int index){
            mIndex = index;
        }
        @Override
        public void onClick(DialogInterface dialog, int which){
            mSelectedIndexs[mIndex] = which;
            String data = mDataArrays[mIndex].get(which);
            mSortButtons.get(mIndex).setText(data);
            dialog.dismiss();
            mListener.onCurrentSelected(mIndex,which, data);
            String[] datas = new String[mDataArrays.length];
            for(int i=0;i<mDataArrays.length;i++){
                datas[i] = mDataArrays[i].get(mSelectedIndexs[i]);
            }
            mListener.totalSelection(mSelectedIndexs,datas);
        }
    };
    
    /**
     * @Title: init
     * @Description:    初始化SortBar
     * @param columnCount      按钮数量     
     * @param bgResId
     */
    @SuppressWarnings("unchecked")
    public void init(int columnCount,OnSortItemSelectedListener listener){
        mListener = listener;
        mDataArrays = new List[columnCount];
        for(int i=0;i<columnCount;i++){
            mDataArrays[i] = new ArrayList<String>();
        }
        mSelectedIndexs = new int[columnCount];
        
        for(int i=0;i<columnCount;i++){
            final Button item = new Button(getContext());
            LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
            item.setEllipsize(TruncateAt.END);
            item.setSingleLine();
            mSortButtons.add(item);
            addView(item,param);
            final int position = i;
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mListener.isSelecteEnable()) return;
                    List<String> data = mDataArrays[position];
                    String[] cache = new String[data.size()]; 
                    data.toArray(cache);
                    AlertDialog ad = new AlertDialog.Builder(getContext())
                        .setTitle(TITLE) 
                        .setSingleChoiceItems(cache,mSelectedIndexs[position],new ItemClickListener(position))
                        .create();  
                   ad.show();
                }
            });
        }
    }
    
   /**
    * @Title: create
    * @Description: 创建指定索引的SortBar数据条目
    * @param bgResId    按钮背景资源ID
    * @param columnIndex    创建在哪一列上
    * @param data           数据
    * @param defSelectedIndex   默认选择哪一项
    */
    public void create(int bgResId,int columnIndex,List<String> data,int defSelectedIndex){
        if( columnIndex < 0 || columnIndex >= mSelectedIndexs.length ) {
            throw new IllegalArgumentException("Out of index for SingleSortBar items!");
        }
        mDataArrays[columnIndex].addAll(data);
        Button item = mSortButtons.get(columnIndex);
        item.setText(data.get(defSelectedIndex));
        item.setBackgroundResource(bgResId);
        mSelectedIndexs[columnIndex] = defSelectedIndex;
    }
    
    /**
     * @Title: create
     * @Description: 创建指定索引的SortBar数据条目
     * @param bgResId    按钮背景资源ID
     * @param columnIndex    创建在哪一列上
     * @param data           数据
     */
     public void create(int bgResId,int columnIndex,List<String> data){
         create(bgResId,columnIndex,data,0);
     }
    
    /**
     * @Title: select
     * @Description: 选择某一项
     * @param column
     * @param selectedIndex
     */
    public void select(int column, int selectedIndex){
        Button item = mSortButtons.get(column);
        item.setText(mDataArrays[column].get(selectedIndex));
        mSelectedIndexs[column] = selectedIndex;
    }
}
