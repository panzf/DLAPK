package com.pzf.dynamicloadclient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public abstract class BaseActivity extends Activity {

	private static final String TAG = BaseActivity.class.getSimpleName();

	public static final String EXTRA_DEX_PATH = "EXTRA_DEX_PATH";

	public static final String EXTRA_CLASS = "EXTRA_CLASS";

	private static final String FROM = "extra.from";

	public static final String PROXY_VIEW_ACTION = "com.pzf.dynamicloadhost.VIEW";
	public static final String DEX_PATH = "/mnt/sdcard/plugin.apk";

	private static final int FROM_EXTERNAL = 1;

	private static final int FROM_INTERNAL = 2;

	protected int mFrom = FROM_INTERNAL;

	private Resources mResource;

	private AssetManager mAssetManager;

	protected Activity mProxyActivity;

	@Override
	public AssetManager getAssets() {
		return mAssetManager != null ? mAssetManager : super.getAssets();
	}

	@Override
	public Resources getResources() {
		return mResource != null ? mResource : super.getResources();
	}

	public void setResources(Resources resources) {
		mResource = resources;
	}

	public void setAssets(AssetManager assetManager) {
		mAssetManager = assetManager;
	}

	public void setProxy(Activity proxyActivity) {
		Log.i(TAG, "setProxy: proxyActivity= " + proxyActivity);
		mProxyActivity = proxyActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mFrom = savedInstanceState.getInt(FROM, FROM_INTERNAL);
		}
		if (mFrom == FROM_INTERNAL) {
			super.onCreate(savedInstanceState);
			mProxyActivity = this;
		}
	}

	protected void startActivityByProxy(String className) {
		if (mProxyActivity == this) {
			Intent intent = new Intent();
			intent.setClassName(this, className);
			this.startActivity(intent);
		} else {
			Intent intent = new Intent(PROXY_VIEW_ACTION);
			intent.putExtra(EXTRA_DEX_PATH, DEX_PATH);
			intent.putExtra(EXTRA_CLASS, className);
			mProxyActivity.startActivity(intent);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		if (mProxyActivity == this) {
			super.setContentView(layoutResID);
		} else {
			mProxyActivity.setContentView(layoutResID);
		}
	}

	@Override
	public void setContentView(View view) {
		if (mProxyActivity == this) {
			super.setContentView(view);
		} else {
			mProxyActivity.setContentView(view);
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (mProxyActivity == this) {
			super.setContentView(view, params);
		} else {
			mProxyActivity.setContentView(view, params);
		}
	}

	@Override
	public void addContentView(View view, LayoutParams params) {
		if (mProxyActivity == this) {
			super.addContentView(view, params);
		} else {
			mProxyActivity.addContentView(view, params);
		}
	}
}
