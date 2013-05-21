package com.apkits.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * 抽象Adapter类
 * @author wangdeyun
 * @param <E>
 */
public abstract class AbstractAdapter<E> extends BaseAdapter {

	/** 数据缓存 */
	protected List<E> mDataCache;
	
	/** 用于从XML文件中创建Layout */
	protected LayoutInflater mInflater;
	
	/** View创建器 */
	protected ViewCreator<E> mCreator;
	
	/**
	 * </br><b>description : </b>	创建HolderAdapter，需要给定View创建接口。
	 * @param inflater
	 * @param creator
	 */
	public AbstractAdapter(LayoutInflater inflater,ViewCreator<E> creator){
		mInflater = inflater;
		mCreator = creator;
	}

	/**
	 * </br><b>title : </b>		更新数据集
	 * </br><b>description :</b>更新数据集
	 * </br><b>time :</b>		2012-7-10 下午11:06:40
	 * @param data
	 */
	public void update(List<E> data){
		clear();
		mDataCache = data;
		notifyDataSetChanged();
	}
	
	/**
	 * <b>description :</b>		清除缓存数据
	 * </br><b>time :</b>		2012-8-10 下午9:52:56
	 */
	public void clear(){
		if( null != mDataCache ){
			mDataCache.clear();
		}
	}
	/**
	 * <b>description :</b>添加数据集，向数据缓存中添加多个元素。
	 * </br><b>time :</b>		2012-7-17 下午10:19:45
	 * @param set
	 */
	public void add(List<E> set){
	    if( null == mDataCache ) mDataCache = new ArrayList<E>();
	    mDataCache.addAll(set);
	    notifyDataSetChanged();
	}
	
	/**
	 * <b>description :</b>		交换两个元素的位置
	 * </br><b>time :</b>		2012-9-4 下午4:46:33
	 * @param src
	 * @param target
	 */
	public void exchange(int src,int target){
		if (src > target) {
			int temp = target;
			target = src;
			target = temp;
		}
		E endObject = getItem(target);
		E startObject = getItem(src);
		mDataCache.set(src, endObject);
		mDataCache.set(target, startObject);
		notifyDataSetChanged();
		
	}

	/**
	 * </br><b>description :</b>添加数据元素，向数据缓存中添加单个元素。
	 * </br><b>time :</b>		2012-7-17 下午10:19:51
	 * @param item
	 */
	public void add(E item){
	    if( null == mDataCache ) mDataCache = new ArrayList<E>();
	    mDataCache.add(item);
	    notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return null == mDataCache ? 0 : mDataCache.size();
	}

	@Override
	public E getItem(int position) {
		return null == mDataCache ? null : mDataCache.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
