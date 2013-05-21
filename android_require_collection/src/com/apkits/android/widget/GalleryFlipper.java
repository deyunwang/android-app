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
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * 支持通过Adapter设置View数量的Gallery控件。
 * 使用HolderAdatper可以自动缓存View的创建，减少View的创建与回收，提升性能。
 * 如果需要处理Gallery的触控过程，请设置OnFlipperListener。
 * @author wangdeyun
 *
 */
public class GalleryFlipper extends FrameLayout {

	/**
	 * </br><b>name : </b>		OnFlipperListener
	 * </br><b>description :</b>Flipper的滚动回调
	 * </br>@author : 			桥下一粒砂
	 * </br><b>e-mail : </b>	chenyoca@gmail.com
	 * </br><b>weibo : </b>		@桥下一粒砂
	 * </br><b>date : </b>		2012-8-5 下午12:29:14
	 *
	 */
	public interface OnFlipperListener {
		/**
		 * </br><b>title : </b>		拉动第一个View
		 * </br><b>description :</b>从右向左滑动为前进方向。在第一个页面向右拉动。
		 * </br><b>time :</b>		2012-8-5 下午12:48:29
		 */
		void pullingHead();

		/**
		 * </br><b>title : </b>		拉动最后一个View
		 * </br><b>description :</b>从右向左滑动为前进方向。在最后一个页面向左拉动。
		 * </br><b>time :</b>		2012-8-5 下午12:49:22
		 */
		void pullingTrail();

		/**
		 * </br><b>title : </b>		切换到上一个View
		 * </br><b>description :</b>从右向左滑动为前进方向。切换到上一个View。
		 * </br><b>time :</b>		2012-8-5 下午12:50:54
		 */
		void movePrevious();

		/**
		 * </br><b>title : </b>		切换到下一个View
		 * </br><b>description :</b>从右向左滑动为前进方向。切换到下一个View。
		 * </br><b>time :</b>		2012-8-5 下午12:50:54
		 */
		void moveNext();
	}

	// Constants

	private final int SWIPE_MIN_DISTANCE = 200;

	/**
	 * 滑动超过多少时，才跳转到下一帧
	 */
	private final int SWIPE_MAX_OFF_PATH = 250;// 250

	/**
	 * 滑动加速度。 指快速滑动时，会跳转的加速度阀值。
	 */
	private final int SWIPE_THRESHOLD_VELOICTY = 600;

	private int mViewPaddingWidth = 0;
	private int mAnimationDuration = 320;
	private float mSnapBorderRatio = 0.5f;
	private boolean mIsGalleryCircular = true;

	private int mGalleryWidth = 0;
	private boolean mIsTouched = false;
	private boolean mIsDragging = false;
	private float mCurrentOffset = 0.0f;
	private long mScrollTimestamp = 0;
	private int mFlingDirection = 0;
	private int mCurrentPosition = 0;
	private int mCurrentViewNumber = 0;

	private Context mContext;
	private Adapter mAdapter;
	private FlingGalleryView[] mViewCaches;
	private FlingGalleryAnimation mAnimation;
	private GestureDetector mGestureDetector;
	private Interpolator mDecelerateInterpolater;

	private OnFlipperListener mListener;

	public GalleryFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GalleryFlipper(Context context) {
		super(context);
		init(context);
	}
	
	/**
	 * </br><b>title : </b>		设置滑动事件监听器
	 * </br><b>description :</b>事件监听
	 * </br><b>time :</b>		2012-8-5 下午12:29:52
	 * @param listener
	 */
	public void setOnFlipperListener(OnFlipperListener listener) {
		mListener = listener;
	}

	private void init(Context context) {
		mContext = context;
		mAdapter = null;

		mViewCaches = new FlingGalleryView[3];
		mViewCaches[0] = new FlingGalleryView(0, this);
		mViewCaches[1] = new FlingGalleryView(1, this);
		mViewCaches[2] = new FlingGalleryView(2, this);
		mAnimation = new FlingGalleryAnimation();
		mGestureDetector = new GestureDetector(new FlingGestureDetector());
		mDecelerateInterpolater = AnimationUtils.loadInterpolator(mContext,
				android.R.anim.decelerate_interpolator);
	}

	// public void setPaddingWidth(int viewPaddingWidth) {
	// mViewPaddingWidth = viewPaddingWidth;
	// }

	public void setAnimationDuration(int animationDuration) {
		mAnimationDuration = animationDuration;
	}

	//
	// public void setSnapBorderRatio(float snapBorderRatio) {
	// mSnapBorderRatio = snapBorderRatio;
	// }
	
	/**
	 * </br><b>title : </b>		TODO
	 * </br><b>description :</b>设置是否循环显示Gallery。
	 * </br><b>time :</b>		2012-8-5 下午12:30:13
	 * @param isGalleryCircular
	 */
	public void setCircular(boolean isGalleryCircular) {
		if (mIsGalleryCircular != isGalleryCircular) {
			mIsGalleryCircular = isGalleryCircular;
			if (mCurrentPosition == getFirstPosition()) {
				mViewCaches[getPrevViewNumber(mCurrentViewNumber)]
						.recycleView(getPrevPosition(mCurrentPosition));
			}

			if (mCurrentPosition == getLastPosition()) {
				mViewCaches[getNextViewNumber(mCurrentViewNumber)]
						.recycleView(getNextPosition(mCurrentPosition));
			}

		}
	}

	public int getFilpperCount() {
		return (mAdapter == null) ? 0 : mAdapter.getCount();
	}

	private int getFirstPosition() {
		return 0;
	}

	private int getLastPosition() {
		return (getFilpperCount() == 0) ? 0 : getFilpperCount() - 1;
	}

	private int getPrevPosition(int relativePosition) {
		int prevPosition = relativePosition - 1;

		if (prevPosition < getFirstPosition()) {
			prevPosition = getFirstPosition() - 1;
			if (mIsGalleryCircular == true) {
				prevPosition = getLastPosition();
			}
		}
		return prevPosition;
	}

	private int getNextPosition(int relativePosition) {
		int nextPosition = relativePosition + 1;
		if (nextPosition > getLastPosition()) {
			nextPosition = getLastPosition() + 1;
			if (mIsGalleryCircular == true) {
				nextPosition = getFirstPosition();
			}
		}
		return nextPosition;
	}

	private int getPrevViewNumber(int relativeViewNumber) {
		return (relativeViewNumber == 0) ? 2 : relativeViewNumber - 1;
	}

	private int getNextViewNumber(int relativeViewNumber) {
		return (relativeViewNumber == 2) ? 0 : relativeViewNumber + 1;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// Calculate our view width
		mGalleryWidth = right - left;
		if (changed == true) {
			// Position views at correct starting offsets
			mViewCaches[0].setOffset(0, 0, mCurrentViewNumber);
			mViewCaches[1].setOffset(0, 0, mCurrentViewNumber);
			mViewCaches[2].setOffset(0, 0, mCurrentViewNumber);
		}
	}

	/**
	 * 设置数据适配器
	 * 
	 * @param adapter
	 */
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(new FlipperObserver());

		mCurrentPosition = 0;
		mCurrentViewNumber = 0;
		// Load the initial views from adapter
		mViewCaches[0].recycleView(mCurrentPosition);
		mViewCaches[1].recycleView(getNextPosition(mCurrentPosition));
		mViewCaches[2].recycleView(getPrevPosition(mCurrentPosition));

		// Position views at correct starting offsets
		mViewCaches[0].setOffset(0, 0, mCurrentViewNumber);
		mViewCaches[1].setOffset(0, 0, mCurrentViewNumber);
		mViewCaches[2].setOffset(0, 0, mCurrentViewNumber);
	}
	
	/**
	 * </br><b>name : </b>		FlipperObserver
	 * </br><b>description :</b>建立数据观察者
	 * </br>@author : 			桥下一粒砂
	 * </br><b>e-mail : </b>	chenyoca@gmail.com
	 * </br><b>weibo : </b>		@桥下一粒砂
	 * </br><b>date : </b>		2012-8-5 下午12:30:38
	 *
	 */
	private class FlipperObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			mCurrentPosition = 0;
			mCurrentViewNumber = 0;
			mViewCaches[0].recycleView(mCurrentPosition);
			mViewCaches[1].recycleView(getNextPosition(mCurrentPosition));
			mViewCaches[2].recycleView(getPrevPosition(mCurrentPosition));
			movePrevious();
		}
	}

	private int getViewOffset(int viewNumber, int relativeViewNumber) {
		// Determine width including configured padding width
		int offsetWidth = mGalleryWidth + mViewPaddingWidth;
		// Position the previous view one measured width to left
		if (viewNumber == getPrevViewNumber(relativeViewNumber)) {
			return offsetWidth;
		}
		// Position the next view one measured width to the right
		if (viewNumber == getNextViewNumber(relativeViewNumber)) {
			return offsetWidth * -1;
		}
		return 0;
	}

	void movePrevious() {
		// Slide to previous view
		mFlingDirection = 1;
		processGesture();
	}

	void moveNext() {
		// Slide to next view
		mFlingDirection = -1;
		processGesture();
	}

	/**
	 * </br><b>title : </b>		切换View
	 * </br><b>description :</b>切换到某个位置的View
	 * </br><b>time :</b>		2012-8-5 下午12:30:57
	 * @param position
	 */
	public void switchTo(int position) {
		int step = position - mCurrentPosition;
		if (step > 0) {
			for (int i = 0; i < Math.abs(step); i++) {
				moveNext();
			}
		} else if (step < 0) {
			for (int i = 0; i < Math.abs(step); i++) {
				movePrevious();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			movePrevious();
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			moveNext();
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * </br><b>title : </b>		 处理TouchEvent事件
	 * </br><b>description :</b> 需要将TouchEvent传递进来，才能实现滑动效果
	 * </br><b>time :</b>		2012-8-5 下午12:31:20
	 * @param event
	 * @return
	 */
	public boolean onFlipperTouchEvent(MotionEvent event) {
		boolean consumed = mGestureDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mIsTouched || mIsDragging) {
				processScrollSnap();
				processGesture();
			}
		}
		return consumed;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 如果是多点触摸，则把事件释放
		if (isMutiTouch(ev)) {
			onFlipperTouchEvent(ev);
			return super.onInterceptTouchEvent(ev);
		} else {
			if (mIsTouched && mIsDragging) {
				return true;
			} else {
				onFlipperTouchEvent(ev);
				return super.onInterceptTouchEvent(ev);
			}
		}
	}

	/**
	 * 判断是否为多点触摸
	 * 
	 * @param event
	 * @return
	 */
	protected boolean isMutiTouch(MotionEvent event) {
		return event.getPointerCount() > 1;
	}

	void processGesture() {
		int newViewNumber = mCurrentViewNumber;
		int reloadViewNumber = 0;
		int reloadPosition = 0;

		mIsTouched = false;
		mIsDragging = false;
		if (mFlingDirection > 0) {
			if (mCurrentPosition > getFirstPosition()
					|| mIsGalleryCircular == true) {
				// Determine previous view and outgoing view to recycle
				newViewNumber = getPrevViewNumber(mCurrentViewNumber);
				mCurrentPosition = getPrevPosition(mCurrentPosition);
				reloadViewNumber = getNextViewNumber(mCurrentViewNumber);
				reloadPosition = getPrevPosition(mCurrentPosition);
			} else {
				if (null != mListener)
					mListener.pullingHead();
			}
		}

		if (mFlingDirection < 0) {
			if (mCurrentPosition < getLastPosition()
					|| mIsGalleryCircular == true) {
				// Determine the next view and outgoing view to recycle
				newViewNumber = getNextViewNumber(mCurrentViewNumber);
				mCurrentPosition = getNextPosition(mCurrentPosition);
				reloadViewNumber = getPrevViewNumber(mCurrentViewNumber);
				reloadPosition = getNextPosition(mCurrentPosition);
			} else {
				if (null != mListener)
					mListener.pullingTrail();
			}
		}

		if (newViewNumber != mCurrentViewNumber) {
			mCurrentViewNumber = newViewNumber;
			// Reload outgoing view from adapter in new position
			mViewCaches[reloadViewNumber].recycleView(reloadPosition);
		}

		// Ensure input focus on the current view
		mViewCaches[mCurrentViewNumber].requestFocus();

		// Run the slide animations for view transitions
		mAnimation.prepareAnimation(mCurrentViewNumber);
		this.startAnimation(mAnimation);

		// Reset fling state
		mFlingDirection = 0;
	}

	void processScrollSnap() {
		// Snap to next view if scrolled passed snap position
		float rollEdgeWidth = mGalleryWidth * mSnapBorderRatio;
		int rollOffset = mGalleryWidth - (int) rollEdgeWidth;
		int currentOffset = mViewCaches[mCurrentViewNumber].getCurrentOffset();

		if (currentOffset <= rollOffset * -1) {
			// Snap to previous view
			mFlingDirection = 1;
		}

		if (currentOffset >= rollOffset) {
			// Snap to next view
			mFlingDirection = -1;
		}
	}

	private class FlingGalleryView {
		private int mViewNumber;
		private FrameLayout mParentLayout;

		private FrameLayout mInvalidLayout = null;
		private LinearLayout mInternalLayout = null;
		private View mExternalView = null;

		public FlingGalleryView(int viewNumber, FrameLayout parentLayout) {
			mViewNumber = viewNumber;
			mParentLayout = parentLayout;

			// Invalid layout is used when outside gallery
			mInvalidLayout = new FrameLayout(mContext);
			mInvalidLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			// Internal layout is permanent for duration
			mInternalLayout = new LinearLayout(mContext);
			mInternalLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			mParentLayout.addView(mInternalLayout);
		}

		public void recycleView(int newPosition) {
			if (mExternalView != null) {
				mInternalLayout.removeView(mExternalView);
			}

			if (mAdapter != null) {
				if (newPosition >= getFirstPosition() && newPosition <= getLastPosition()) {
					mExternalView = mAdapter.getView(newPosition,mExternalView, mInternalLayout);
				} else {
					mExternalView = mInvalidLayout;
				}
			}

			if (mExternalView != null) {
				mInternalLayout.addView(mExternalView,
						new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
			}
		}

		public void setOffset(int xOffset, int yOffset, int relativeViewNumber) {
			// Scroll the target view relative to its own position relative to
			// currently displayed view
			mInternalLayout.scrollTo(
					getViewOffset(mViewNumber, relativeViewNumber) + xOffset,
					yOffset);
		}

		public int getCurrentOffset() {
			return mInternalLayout.getScrollX();
		}

		public void requestFocus() {
			mInternalLayout.requestFocus();
		}
	}

	/**
	 * 切换动画
	 * 
	 * @author cfuture.chenyoca [桥下一粒砂] (chenyoca@163.com)
	 * @date 2012-3-6
	 */
	private class FlingGalleryAnimation extends Animation {
		private boolean mIsAnimationInProgres;
		private int mRelativeViewNumber;
		private int mInitialOffset;
		private int mTargetOffset;
		private int mTargetDistance;

		public FlingGalleryAnimation() {
			mIsAnimationInProgres = false;
			mRelativeViewNumber = 0;
			mInitialOffset = 0;
			mTargetOffset = 0;
			mTargetDistance = 0;
		}

		public void prepareAnimation(int relativeViewNumber) {
			// If we are animating relative to a new view
			if (mRelativeViewNumber != relativeViewNumber) {
				if (mIsAnimationInProgres == true) {
					// We only have three views so if requested again to animate
					// in same direction we must snap
					int newDirection = (relativeViewNumber == getPrevViewNumber(mRelativeViewNumber)) ? 1
							: -1;
					int animDirection = (mTargetDistance < 0) ? 1 : -1;

					// If animation in same direction
					if (animDirection == newDirection) {
						// Ran out of time to animate so snap to the target
						// offset
						mViewCaches[0].setOffset(mTargetOffset, 0,
								mRelativeViewNumber);
						mViewCaches[1].setOffset(mTargetOffset, 0,
								mRelativeViewNumber);
						mViewCaches[2].setOffset(mTargetOffset, 0,
								mRelativeViewNumber);
					}
				}

				// Set relative view number for animation
				mRelativeViewNumber = relativeViewNumber;
			}

			// Note: In this implementation the targetOffset will always be zero
			// as we are centering the view; but we include the calculations of
			// targetOffset and targetDistance for use in future implementations

			mInitialOffset = mViewCaches[mRelativeViewNumber]
					.getCurrentOffset();
			mTargetOffset = getViewOffset(mRelativeViewNumber,
					mRelativeViewNumber);
			mTargetDistance = mTargetOffset - mInitialOffset;

			// Configure base animation properties
			this.setDuration(mAnimationDuration);
			this.setInterpolator(mDecelerateInterpolater);

			// Start/continued animation
			mIsAnimationInProgres = true;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation transformation) {
			// Ensure interpolatedTime does not over-shoot then calculate new
			// offset
			interpolatedTime = (interpolatedTime > 1.0f) ? 1.0f
					: interpolatedTime;
			int offset = mInitialOffset
					+ (int) (mTargetDistance * interpolatedTime);

			for (int viewNumber = 0; viewNumber < 3; viewNumber++) {
				// Only need to animate the visible views as the other view will
				// always be off-screen
				if ((mTargetDistance > 0 && viewNumber != getNextViewNumber(mRelativeViewNumber))
						|| (mTargetDistance < 0 && viewNumber != getPrevViewNumber(mRelativeViewNumber))) {
					mViewCaches[viewNumber].setOffset(offset, 0,
							mRelativeViewNumber);
				}
			}
		}

		@Override
		public boolean getTransformation(long currentTime,
				Transformation outTransformation) {
			if (super.getTransformation(currentTime, outTransformation) == false) {
				// Perform final adjustment to offsets to cleanup animation
				mViewCaches[0].setOffset(mTargetOffset, 0, mRelativeViewNumber);
				mViewCaches[1].setOffset(mTargetOffset, 0, mRelativeViewNumber);
				mViewCaches[2].setOffset(mTargetOffset, 0, mRelativeViewNumber);

				// Reached the animation target
				mIsAnimationInProgres = false;

				return false;
			}

			// Cancel if the screen touched
			if (mIsTouched || mIsDragging) {
				// Note that at this point we still consider ourselves to be
				// animating
				// because we have not yet reached the target offset; its just
				// that the
				// user has temporarily interrupted the animation with a touch
				// gesture

				return false;
			}

			return true;
		}
	}

	private class FlingGestureDetector extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			// Stop animation
			mIsTouched = true;
			// Reset fling state
			mFlingDirection = 0;
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (e2.getAction() == MotionEvent.ACTION_MOVE) {
				if (mIsDragging == false) {
					// Stop animation
					mIsTouched = true;
					// Reconfigure scroll
					mIsDragging = true;
					mFlingDirection = 0;
					mScrollTimestamp = System.currentTimeMillis();
					mCurrentOffset = mViewCaches[mCurrentViewNumber]
							.getCurrentOffset();
				}

				float maxVelocity = mGalleryWidth
						/ (mAnimationDuration / 1000.0f);
				long timestampDelta = System.currentTimeMillis()
						- mScrollTimestamp;
				float maxScrollDelta = maxVelocity * (timestampDelta / 1000.0f);
				float currentScrollDelta = e1.getX() - e2.getX();

				if (currentScrollDelta < maxScrollDelta * -1)
					currentScrollDelta = maxScrollDelta * -1;
				if (currentScrollDelta > maxScrollDelta)
					currentScrollDelta = maxScrollDelta;
				int scrollOffset = Math.round(mCurrentOffset
						+ currentScrollDelta);

				// We can't scroll more than the width of our own frame layout
				if (scrollOffset >= mGalleryWidth)
					scrollOffset = mGalleryWidth;
				if (scrollOffset <= mGalleryWidth * -1)
					scrollOffset = mGalleryWidth * -1;

				mViewCaches[0].setOffset(scrollOffset, 0, mCurrentViewNumber);
				mViewCaches[1].setOffset(scrollOffset, 0, mCurrentViewNumber);
				mViewCaches[2].setOffset(scrollOffset, 0, mCurrentViewNumber);
			}

			return false;
		}

		/**
		 * 滑动时触发
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) <= SWIPE_MAX_OFF_PATH) {
				if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOICTY) {
					if (mListener != null)
						mListener.movePrevious();
					movePrevious();
				}

				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOICTY) {
					if (mListener != null)
						mListener.moveNext();
					moveNext();
				}
			}

			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// Finalise scrolling
			mFlingDirection = 0;
			processGesture();
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// Reset fling state
			mFlingDirection = 0;
			return false;
		}
	}
}