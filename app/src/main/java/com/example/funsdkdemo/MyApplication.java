package com.example.funsdkdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.bang.demo.loglibrary.LogUtil;
import com.example.download.XDownloadFileManager;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;


public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.init(BuildConfig.DEBUG,getPackageName());
		registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
		/**
		 * 以下是FunSDK初始化
		 */
		FunSupport.getInstance().init(this);
		
		/**
		 * 以下是网络图片下载等的本地缓存初始化,可以加速图片显示,和节省用户流量
		 * 跟FunSDK无关,只跟com.example.download内容相关
		 */
		String cachePath = FunPath.getCapturePath();
		XDownloadFileManager.setFileManager(
				cachePath, 				// 缓存目录
				20 * 1024 * 1024		// 20M的本地缓存空间
				);
	}

	public void exit() {

		FunSupport.getInstance().term();
	}

	private  ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}

		@Override
		public void onActivityStarted(Activity activity) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}

		@Override
		public void onActivityResumed(Activity activity) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}

		@Override
		public void onActivityPaused(Activity activity) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}

		@Override
		public void onActivityStopped(Activity activity) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			LogUtil.e(activity.getClass().getSimpleName() + ".java");
		}
	};

}
