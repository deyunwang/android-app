package com.apkits.android.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;


/**
 * 启动过程Activity.此Activity继承自BaseActivity，具备处理耗时操作能力。
 * 如果需要在启动过程中执行耗时操作，请覆盖onProcess()方法。
 * 如果在处理过程中需要中断等待，请使用sendProcessed()方法发送处理完成信号。	
 * @author wangdeyun
 *
 */
public abstract class SplashActivity extends Activity{
	
	/** 调试数据输出 **/
	private final static String TAG = "SplashActivity";
	
	/**
	 * <ul>
	 * <li><b>name : </b>		SplashResource		</li>
	 * <li><b>description :</b>	动画资源				</li>
	 * <li><b>author : </b>		桥下一粒砂			</li>
	 * <li><b>e-mail : </b>		chenyoca@gmail.com	</li>
	 * <li><b>weibo : </b>		@桥下一粒砂			</li>
	 * <li><b>date : </b>		2012-8-12 上午9:25:56		</li>
	 * </ul>
	 */
	protected class SplashResource {
		private int imageResId;
		private int life;
		private int startAlpha;
		
		/**
		 * </br><b>description : </b>	TODO
		 * @param imageResId			启动动画的Drawable资源ID
		 * @param life					显示时间(单位是毫秒)
		 * @param startAlpha			从 0 - 10 这是渐变动画的透明度。
		 */
		public SplashResource(int imageResId,int life,int startAlpha){
			this.imageResId = imageResId;
			this.life = life;
			this.startAlpha = startAlpha;
		}
		
		public int getImageResId() {
			return imageResId;
		}
		
		public int getLife() {
			return life;
		}
		
		public int getStartAlpha() {
			return startAlpha;
		}
	}
	
	/**
	 * <ul>
	 * <li><b>name : </b>		Signal		</li>
	 * <li><b>description :</b>	启动过程的消息类型				</li>
	 * <li><b>author : </b>		桥下一粒砂			</li>
	 * <li><b>e-mail : </b>		chenyoca@gmail.com	</li>
	 * <li><b>weibo : </b>		@桥下一粒砂			</li>
	 * <li><b>date : </b>		2012-8-12 上午9:26:59		</li>
	 * </ul>
	 */
	public interface Signal {
		int LOADING = 0;
		int FINISHED = 1;
	}
	
	/** 动画持续时间 */
	private int mDuration = 1500;
	
	/**  第一个动画被显示的时间间隔  */
	private final static int INIT_TIME = 100;
	
	/** 动画资源数组  */
	private List<SplashResource> mSplashParams = new ArrayList<SplashResource>();
	
	private FrameLayout mLayout;
	
	private ImageView mSplashStage;
	
	protected int[] RndSplashArray = new int[]{0};
	
	private Boolean mWaiting = true;
	
	private BaseActivity mBaseInfo;
	
	/**
	 * 设置启动界面背景颜色
	 * 
	 * @return 颜色值，例如0xFF123456（前两位是透明度，后六位是颜色值）
	 */
	protected int getBackground() {
		return 0xFFFFFFFF;
	}
	
	/**
	 * <b>description :</b>		随机启动图片
	 * </br><b>time :</b>		2012-8-12 上午9:31:20
	 * @return
	 */
	protected int getRandResId(){
		return RndSplashArray[Math.abs(new Random().nextInt() % RndSplashArray.length)];
	}
	
	/**
	 * <b>description :</b>		设置动画持续时间
	 * </br><b>time :</b>		2012-8-12 上午9:31:38
	 * @param duration
	 */
	public void setDuration(int duration){
		mDuration = duration;
	}
	
	/**
	 * <b>description :</b>		添加启动动画资源。创建一个资源对象，并添加到List中。
	 * </br><b>time :</b>		2012-8-12 上午9:32:29
	 * @param resources			把创建的动画资源添加到此List对象中
	 */
	protected abstract void addResource(List<SplashResource> resources);
	
	/**
	 * 设置启动动画显示后的UI
	 * @return Activity的Class
	 */
	protected abstract Class<?> nextActivity();
	
	/**
	 * 创建动画Layout
	 * @return 返回创建的Layout
	 */
	private View createSplashLayout() {
		mLayout = new FrameLayout(mBaseInfo.context);
		ViewGroup.LayoutParams layoutParams = 
				new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		mLayout.setLayoutParams(layoutParams);
		mLayout.setBackgroundColor(getBackground());
		mSplashStage = new ImageView(mBaseInfo.context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		mLayout.addView(mSplashStage, params);
		return mLayout;
	}
	
	/**
	 * 显示动画的Handler
	 */
	private Handler mAnimHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == Signal.LOADING){
				int resId = msg.arg1;
				int startAlpha = msg.arg2;
				AlphaAnimation animation = new AlphaAnimation(converToAlpha(startAlpha), 1.0f);
				animation.setDuration(mDuration);
				mSplashStage.setImageResource(resId);
				mSplashStage.startAnimation(animation);
			}else{
				shouldSkipToNext();
				mBaseInfo.self.startActivity(new Intent(mBaseInfo.self,nextActivity()));
				mBaseInfo.self.finish();
			}
			super.handleMessage(msg);
		}
		
	};

	/**
	 * 显示动画
	 */
	private void showSplash() {
		int life = 0;
		int maxIndex = mSplashParams.size()-1;
		for(int i=0;i <= mSplashParams.size(); i++){
			if(i <= maxIndex){
				if( i == 0){
					life += INIT_TIME;
				}else{
					life += mSplashParams.get(i - 1).getLife();
				}
				Message msg = new Message();
				msg.what = Signal.LOADING;
				msg.arg1 =  mSplashParams.get(i).getImageResId();
				msg.arg2 = mSplashParams.get(i).getStartAlpha();
				mAnimHandler.sendMessageDelayed(msg,life);
			}else{
				life += mSplashParams.get(maxIndex).getLife();
				Message msg = new Message();
				msg.what = Signal.FINISHED;
				mAnimHandler.sendMessageDelayed(msg,life);
			}
		}
	}
	
	/**
	 * 转换透明度
	 * @param val 10进制的透明度。（0-10）
	 * @return 转换成 0.0f - 1.0f
	 */
	private float converToAlpha(int val) {
		return (float) (Math.max(0, Math.min(val, 10)) / 10.0);
	}
	
	/**
	 * OnCreate过程不可被覆盖
	 */
	@Override
	final protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaseInfo = new BaseActivity(this);
		BaseActivity.hideTitleBar(mBaseInfo.self);
		BaseActivity.setFullScreen(mBaseInfo.self, true);
		setContentView(createSplashLayout());
		addResource(mSplashParams);
		onSplash();
		showSplash();
	}
	
	
	
	/**
	 * 在动画显示过程前执行方法
	 */
	private void onSplash(){
		beforeProcess();
		new Thread(new Runnable(){
			@Override
			public void run() {
				Looper.prepare();
				onProcess();
				sendProcessed();
			}
		}).start();
	}
	
	/**
	 * 在开始执行处理过程前，此方法被调用
	 */
	protected void beforeProcess(){}
	
	/**
	 * 在方法在一个非UI线程中执行。
	 * 如果需要在启动过程中执行比较耗时的操作，请覆盖这个方法。
	 * 如果此方法执行时间比启动动画长，则动画会显示最后一帧，等待此方法返回。
	 * 否则，播放完动画后，直接跳到下一个Activity。
	 */
	protected void onProcess(){}
	
	/**
	 * 是否可以跳转到下一个UI
	 * 在此方法没有返回之前，动画会一直显示，并等待此方法返回
	 */
	private void shouldSkipToNext(){
		while(mWaiting){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Log.e(TAG,e.getMessage());
				mWaiting = false;
			}
		}
	}
	
	/**
	 * 发送完成处理消息。
	 */
	protected final void sendProcessed(){
		synchronized (mWaiting) {
			mWaiting = false;
		}
	}
	

}