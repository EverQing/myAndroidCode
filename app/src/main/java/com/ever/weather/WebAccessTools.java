package com.ever.weather;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author i-zqluo
 * 网站访问工具类，用于Android的网络访问
 * 
 */
public class WebAccessTools {
	
	/**
	 * 当前的Context上下文对象
	 */
	private Context context;
	/**
	 * 构造一个网站访问工具类
	 * @param context 记录当前Activity中的Context上下文对象
	 */
	public WebAccessTools(Context context) {
		this.context = context;
	}
	
	/**
	 * 根据给定的url地址访问网络，得到响应内容(这里为GET方式访问)
	 * @param url 指定的url地址
	 * @return web服务器响应的内容，为<code>String</code>类型，当访问失败时，返回为null
	 */
	public  String getWebContent(String url) {
		//创建一个URL请求对象
		URL u;
		HttpURLConnection urlConnection;
		String result = "";
		BufferedReader br;
		try{
			u  = new URL(url);
			urlConnection = (HttpURLConnection)u.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(5 * 1000);
//			urlConnection.setRequestProperty("Accept-Language", "zh-CN");
//			urlConnection.setRequestProperty("Charset","UTF-8");
//			urlConnection.setRequestProperty("Connect","Keep-Alive");
			urlConnection.setDoInput(true);

			br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String response;
			while((response = br.readLine()) != null)
				result += response + "\n";

			urlConnection.disconnect();
			return result;
		}catch (IOException e){
			e.printStackTrace();
		}

		return null;
	}


	public String downLoadImg(String path,OutputStream fos){
		//创建一个URL请求对象
		if(path == "")
			return null;
		URL u;
		HttpURLConnection urlConnection;
		String result = "";
		InputStream is;
		//OutputStream os;
		try{
			u  = new URL(path);
//			urlConnection = (HttpURLConnection)u.openConnection();
//			urlConnection.setRequestMethod("GET");
//			urlConnection.setConnectTimeout(5 * 1000);
//			urlConnection.setRequestProperty(
//					"Accept",
//					"image/gif, image/jpeg, image/pjpeg, image/png, "
//							+ "application/x-shockwave-flash, application/xaml+xml, "
//							+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
//							+ "application/x-ms-application, application/vnd.ms-excel, "
//							+ "application/vnd.ms-powerpoint, application/msword, */*");
//			urlConnection.setRequestProperty("Accept-Language", "zh-CN");
//			urlConnection.setRequestProperty("Charset","UTF-8");
//			urlConnection.setRequestProperty("Connection", "Keep-Alive");
//			urlConnection.setDoInput(true);
//
//			is = urlConnection.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
			byte[] buf = new byte[1024];
			int len;
			//String temp;

			is = u.openStream();

			while((len = is.read(buf)) > 0) {    //fos.write(buf,0,len);
				fos.write(buf,0,len);
			}
			fos.flush();

			//urlConnection.disconnect();
			return result;
		}catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
}