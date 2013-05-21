/**
 * Copyright (C) 2012 IQBankV2 Project
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Gallery;

/**
 * @author wangdeyun
 *
 */
public class GalleryEx extends Gallery {

	private float mTouchBeganX;
	// 移动结束时的坐标
	private float mTouchEndX;
	// 按下去时选中的项的下标
	private int mSelectedIndex;

	private float mxwidth;

	private OnKeyListener mOnKeyListener;

	/**
	 * </br><b>description : </b> TODO
	 * 
	 * @param context
	 * @param attrs
	 */
	public GalleryEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * </br><b>description : </b> TODO
	 * 
	 * @param context
	 */
	public GalleryEx(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Gallery#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// 解决重叠问题
		setSpacing(10);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (MotionEvent.ACTION_DOWN == action) {
			mTouchBeganX = event.getX();
			mTouchEndX = 0;
			mSelectedIndex = getSelectedItemPosition();
		} else if (action == MotionEvent.ACTION_MOVE) {
			if (mSelectedIndex == 0 || mSelectedIndex == getCount() - 1) {
				float offsetX = event.getX();
				mTouchEndX = offsetX - mTouchBeganX;
				if (mSelectedIndex == 0) {
					if (mTouchEndX > 0 && mTouchEndX < getWidth()
							&& offsetX > mTouchBeganX - mxwidth) {
						getSelectedView().startAnimation(genAnimation());
					} else {
						mTouchEndX = 0;
						mSelectedIndex = getSelectedItemPosition();
					}
				} else {
					if (mTouchEndX < 0 && mTouchEndX > -getWidth() * 2
							&& offsetX < mTouchBeganX + mxwidth) {
						View selectedView = getSelectedView();
						if (selectedView != null) {
							selectedView.startAnimation(genAnimation());
						}
					} else {
						mTouchEndX = 0;
						mSelectedIndex = getSelectedItemPosition();
					}
				}
			}
		} else if (action == MotionEvent.ACTION_UP) {
			if ((mSelectedIndex == 0 || mSelectedIndex == getCount() - 1)) {
				int index = getSelectedItemPosition();
				if (index == 0 || index == getCount() - 1) {
					if (mTouchEndX != 0) {
						Animation translate = new TranslateAnimation(
								mTouchEndX, 0, 0, 0);
						translate.setDuration(250);
						translate.setFillAfter(true);
						View selectedView = getSelectedView();
						if (selectedView != null) {
							selectedView.startAnimation(translate);
						}
					}
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * <b>description :</b> 动画 </br><b>time :</b> 2012-8-19 下午1:02:32
	 * 
	 * @return
	 */
	private Animation genAnimation() {
		TranslateAnimation translate = new TranslateAnimation(mTouchEndX,
				mTouchEndX, 0, 0);
		translate.setDuration(25);
		translate.setFillAfter(true);
		return translate;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent = e2.getX() > e1.getX() ? KeyEvent.KEYCODE_DPAD_LEFT
				: KeyEvent.KEYCODE_DPAD_RIGHT;
		return onKeyDown(kEvent, null);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mOnKeyListener != null
				&& mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void setOnKeyListener(OnKeyListener onKeyListener) {
		mOnKeyListener = onKeyListener;
	}

}
