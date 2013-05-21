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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.apkits.android.common.CommonRegex;
import com.apkits.android.common.ISUtil;
import com.apkits.android.encrypt.HashEncrypt;
import com.apkits.android.encrypt.HashEncrypt.CryptType;
import com.apkits.android.resource.BitmapUtil;

/**
 * 继承自ImageView，支持自动加载网络图片，并具有缓存图片功能。
 * 控件首先检查缓存（应用私有目录）中是否存在URL所指向的图片，如果存在，则直接读取缓存中的图片。
 * 如果不存在，则创建一个线程下载网络图片。
 * @author wangdeyun
 *
 */
public class WebImageView extends ImageView {

	/** 图片的最小宽高 */
	public static final int MIN_WIDTH_HEIGHT = 10;
	
	/** 调试信息输出 */
	private static final String TAG = "WebImageView";
	
	/** Andorid环境上下文 */
	private Context mContext;
	
	/** 重置图片大小  */
	private int[] mResize = {-1,-1};
	
	/** 默认图片  */
	private int mDefaultImageRes = 0;
	
	public interface State { 
	    int Success = 202;
	    int Error = 404;
	}
	
	/** 下载更新回调 */
	private Handler mDownloadCallback = new Handler(){
		@Override
		public void handleMessage(Message msg) {
		    if( msg.what == State.Error){
		        Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
		        return;
		    }
			Bitmap image = null;
			try {
				image = ISUtil.toBitmap(mContext.openFileInput(msg.obj.toString()));
			} catch (IOException e) {
				Log.e(TAG,"Cannot convert file to image !");
				e.printStackTrace();
				return;
			} 
			image = resize(image);
			if( null != image ){
			    WebImageView.this.invalidate();
			    AlphaAnimation ani = new AlphaAnimation(0.3f, 1.0f);
			    ani.setDuration(500);
				WebImageView.this.setImageBitmap(image);
				WebImageView.this.setAnimation(ani);
			}
		}
	};
	
	/**
	 * <b>description :</b>		调整图片大小
	 * </br><b>time :</b>		2012-8-16 下午7:40:12
	 * @param img
	 * @return
	 */
	private Bitmap resize(Bitmap img){
		if( null != img && mResize[0] >= MIN_WIDTH_HEIGHT && mResize[1] >= MIN_WIDTH_HEIGHT){
			return BitmapUtil.extract(img, mResize[0], mResize[1]);
		}else{
			return img;
		}
	}
	
	/**
	 * <b>description :</b>设置图片大小。强制大小，不按比例缩放。图片宽高必须大小10。
	 * </br><b>time :</b>		2012-8-4 下午4:42:50
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width,int height){
		if( width < MIN_WIDTH_HEIGHT || height < MIN_WIDTH_HEIGHT ){
			throw new IllegalArgumentException(
					String.format("Image size width or height must greater than %d !",MIN_WIDTH_HEIGHT));
		}else{
			mResize = new int[]{width,height};
		}
	}
	
	/**
     * </br><b>description : </b>   设置默认图片
     * @param context
     * @param attrs
     */
	public void setDefaultImage(int drawableResId){
	    mDefaultImageRes = drawableResId;
	}
	
	/**
	 * </br><b>description : </b>	在XML中构建
	 * @param context
	 * @param attrs
	 */
	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	/**
	 * </br><b>description : </b>	在Java代码中构建
	 * @param context
	 */
	public WebImageView(Context context) {
		super(context);
		mContext = context;
	}
	
	/**
	 * </br><b>description :</b>从网络地址上加载图片。首先从缓存文件夹中读取，再从网络下载。
	 * </br><b>time :</b>		2012-8-4 下午4:03:19
	 * @param url
	 */
	public void fetchFromUrl(final String url){
	    if(!CommonRegex.matcherRegex("[\\w\\p{P}]*\\.[jpngifJPNGIF]{3,4}", url)){
	        if(mDefaultImageRes != 0) setImageResource(mDefaultImageRes);
            WebImageView.this.invalidate();
	        return;
	    }
	    final String tempFile = HashEncrypt.encode(CryptType.SHA1, url);
		//是否在缓存
		if(mContext.getFileStreamPath(tempFile).exists()){
			Message msg = new Message();
			msg.what = State.Success;
			msg.obj = tempFile;
			mDownloadCallback.sendMessage(msg);
		}else{
			//下载
			new Thread(new Runnable(){
				@Override
				public void run() {
				    Message msg = new Message();
				    msg.what = State.Success;
					try {
					    HttpGet httpRequest = new HttpGet(url);
					    HttpClient httpclient = new DefaultHttpClient();
					    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
					    HttpEntity entity = response.getEntity();
					    BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
					    InputStream is = bufferedHttpEntity.getContent();
						FileOutputStream os = mContext.openFileOutput(tempFile, Context.MODE_PRIVATE);
						byte[] cache = new byte[ 1 * 1024 ]; 
						for(int len = 0;(len = is.read(cache)) != -1;){
						    os.write(cache, 0, len);
						}
						os.close();
						is.close();
						msg.obj = tempFile;
					} catch (IOException e) {
						msg.obj = e.getMessage();
						msg.what = State.Error;
						e.printStackTrace();
					} finally{
                        mDownloadCallback.sendMessage(msg);
					}
				}
			}).start();
		}
	}
}
