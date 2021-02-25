/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.ecs.numbasst.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.ecs.numbasst.manager.BleService;

/**数据存储工具类
 * @must 
 * <br> 1.将fileRootPath中的包名（这里是evlibrary.demo）改为你的应用包名
 * <br> 2.在Application中调用init方法
 */
public class DataKeeper {

	private static final String TAG = "DataKeeper";
	//文件缓存<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	/**TODO 必须将fileRootPath中的包名（这里是evlibrary.demo）改为你的应用包名*/
	public static final String fileRootPath = getSDPath() != null ? (getSDPath() + "/确认仪/") : null;
	public static final String unitPath = fileRootPath + "列尾单元软件/";
	public static final String unit_store = unitPath + "存储单元软件/";
	public static final String unit_display = unitPath + "显示单元软件/";
	public static final String unit_main_control = unitPath + "主控单元软件/";

	public static final String downloadPath = fileRootPath + "下载数据/";
	//文件缓存>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//存储文件的类型<<<<<<<<<<<<<<<<<<<<<<<<<
	public static final int TYPE_FILE_TEMP = 0;								//保存保存临时文件
	public static final int TYPE_FILE_IMAGE = 1;							//保存图片
	public static final int TYPE_FILE_VIDEO = 2;							//保存视频
	public static final int TYPE_FILE_AUDIO = 3;							//保存语音

	//存储文件的类型>>>>>>>>>>>>>>>>>>>>>>>>>

	//不能实例化
	private DataKeeper() {}

	//获取context，获取存档数据库引用
	public static void init(Context context) {
		Log.i(TAG, "init fileRootPath = " + fileRootPath);
		//判断SD卡存在
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(fileRootPath != null) {
				File file = new File(fileRootPath);
				if(!file.exists()) {
					Log.d(TAG,"1 fileRootPath = " +fileRootPath);
					file.mkdirs();
					Log.d(TAG,"2 fileRootPath = " +fileRootPath);
				}
				Log.d(TAG,"SUCCEED fileRootPath = " +fileRootPath);
				file = new File(unitPath);
				if(!file.exists()) {
					file.mkdir();
				}
				file = new File(unit_store);
				if(!file.exists()) {
					file.mkdir();
				}
				file = new File(unit_display);
				if(!file.exists()) {
					file.mkdir();
				}
				file = new File(unit_main_control);
				if(!file.exists()) {
					file.mkdir();
				}

				file = new File(downloadPath);
				if(!file.exists()) {
					file.mkdir();
				}
				//Android8.1手持设备有BUG ，创建完文件 USB 连接PC会没有显示，所以要通知系统去刷新媒体库。
				try {
					MediaScannerConnection.scanFile(context,new String[]{unitPath,unit_store,unit_display,unit_main_control,downloadPath},null,null);
				}catch (Exception e){
					e.printStackTrace();
				}

			}
		}
	}


	//**********外部存储缓存***************
	/**
	 * 存储缓存文件 返回文件绝对路径
	 * @param file
	 * 		要存储的文件
	 * @param type
	 * 		文件的类型
	 *		IMAGE = "imgae";							//图片         
	 *		VIDEO = "video";							//视频        
	 *		VOICE = "voice";							//语音         
	 *		 = "voice";							//语音         
	 * @return	存储文件的绝对路径名
	 * 		若SDCard不存在返回null
	 */
	public static String storeFile(File file, String type) {

		if(!hasSDCard()) {
			return null;
		}
		String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		byte[] data = null;
		try {
			FileInputStream in = new FileInputStream(file);
			data = new byte[in.available()];
			in.read(data, 0, data.length);
			in.close();
		} catch (IOException e) {
			Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
					" } catch (IOException e) {\n" + e.getMessage());
		}
		return storeFile(data, suffix, type);
	}

	/** @return	存储文件的绝对路径名
				若SDCard不存在返回null */
	@SuppressLint("DefaultLocale")
	public static String storeFile(byte[] data, String suffix, String type) {

		if(!hasSDCard()) {
			return null;
		}
		String path = null;

		try {
			FileOutputStream out = new FileOutputStream(path);
			out.write(data, 0, data.length);
			out.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
					" } catch (FileNotFoundException e) {\n" + e.getMessage() + "\n\n >> path = null;");
			path = null;
		} catch (IOException e) {
			Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
					" } catch (IOException e) {\n" + e.getMessage() + "\n\n >> path = null;");
			path = null;
		}
		return path;
	}



	/**若存在SD 则获取SD卡的路径 不存在则返回null*/
	public static String getSDPath(){
		File sdDir = null;
		String path = null;
		//判断sd卡是否存在
		boolean sdCardExist = hasSDCard();
		if (sdCardExist) {
			//获取跟目录
			sdDir = Environment.getExternalStorageDirectory();
			path = sdDir.toString();
		}
		return path;
	}

	/**判断是否有SD卡*/
	public static boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

}