/**
 * Copyright (C) 2012 TookitForAndroid Project
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
package com.apkits.android.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * HTTP连接辅助
 * @author wangdeyun
 *
 */
public class Http {
	
	/**
	 * 通用post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream post(String url,Map<String,String> params) 
	        throws MalformedURLException,IOException{  
        return post(url,params,null);
    }
	
	/**
	 * 通用post请求
	 * @param url				POST请求URL
	 * @param params			POST请求参数
	 * @param data				发送到服务器的位数组
	 * @return					服务器响应流
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream post(String url,Map<String,String> params,byte[] data) 
	        throws MalformedURLException, IOException{
		if( null != params ){
			StringBuffer sb = new StringBuffer();  
			for(Map.Entry<String,String> entry:params.entrySet()){  
	            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()));  
	            sb.append("&");  
	        }
			String param = sb.deleteCharAt(sb.length()-1).toString(); 
			 return connect("POST",new URL(url),param.getBytes(),data);
		}else{
			 return connect("POST",new URL(url),null,data);
		}
	}

	/**
	 * 向服务器发送POST请求，并接收服务器输出流
	 * @param method			请求方法名
	 * @param url				POST请求URL
	 * @param datas				发送到服务器的位数组列表
	 * @return					服务器响应流
	 * @throws IOException
	 */
	private static InputStream connect(String method,URL url,byte[]... datas) 
	        throws IOException{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod(method);
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(6 * 1000);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Charset", "UTF-8");
		OutputStream outputStream = conn.getOutputStream();
		for (byte[] data : datas) {
			outputStream.write(data);
		}
		outputStream.close();
		return conn.getInputStream();
	}
	
	/**
	 * 向服务器发送GET请求，并接收服务器输出流
	 * @param url				GET请求URL
	 * @param params			发送到服务器的参数
	 * @return					服务器响应流
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream get(String url,Map<String,String> params) 
	        throws MalformedURLException, IOException{
		if( null != params ){
			StringBuffer sb = new StringBuffer();  
			for(Map.Entry<String,String> entry:params.entrySet()){  
	            sb.append(entry.getKey()).append("=").append(entry.getValue());  
	            sb.append("&");  
	        }
			String param = sb.deleteCharAt(sb.length()-1).toString();  
	        return connect("GET",new URL(url),param.getBytes());
		}
		return connect("GET",new URL(url),new byte[]{});
	}
	
	/**
	 * 向服务器发送GET请求，并接收服务器输出流
	 * @param url				GET请求URL
	 * @return					服务器响应流
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream get(String url) throws MalformedURLException, IOException{
		return get(url,null);
	}
}