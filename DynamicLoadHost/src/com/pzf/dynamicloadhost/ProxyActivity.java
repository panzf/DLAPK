package com.pzf.dynamicloadhost;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ProxyActivity extends Activity {

	private static final String TAG = "ProxyActivity";

	public static final String EXTRA_DEX_PATH = "EXTRA_DEX_PATH";
	public static final String EXTRA_CLASS = "EXTRA_CLASS";

	private static final String FROM = "extra.from";

	private static final int FROM_EXTERNAL = 1;
	private static final int FROM_INTERNAL = 2;

	private String mDexPath;

	private String mClass;

	private AssetManager mAssetManager;

	private Resources mResources;

	@Override
	public AssetManager getAssets() {
		return mAssetManager != null ? mAssetManager : super.getAssets();
	}

	@Override
	public Resources getResources() {
		return mResources != null ? mResources : super.getResources();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proxy);

		mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
		mClass = getIntent().getStringExtra(EXTRA_CLASS);

		if (mClass == null) {
			launchTarget();
		} else {
			launchTarget(mClass);
		}
	}

	private void launchTarget(String clz) {
		Log.i(TAG, "startProxyActivity class:" + clz);
		String dexOutPutDir = getDir("dex", 0).getAbsolutePath();

		ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();

		DexClassLoader dexClassLoader = new DexClassLoader(mDexPath,
				dexOutPutDir, null, localClassLoader);

		try {
			Class<?> loadClass = dexClassLoader.loadClass(clz);
			Constructor<?> localConstructor = loadClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});

			Method setProxy = loadClass.getMethod("setProxy", Activity.class);
			setProxy.setAccessible(true);
			setProxy.invoke(instance, new Object[] { this });

			Method setAssets = loadClass.getMethod("setAssets",
					AssetManager.class);
			setAssets.setAccessible(true);
			setAssets.invoke(instance, new Object[] { mAssetManager });

			Method setResources = loadClass.getMethod("setResources",
					Resources.class);
			setResources.setAccessible(true);
			setResources.invoke(instance, new Object[] { mResources });

			Method onCreate = loadClass.getDeclaredMethod("onCreate",
					new Class[] { android.os.Bundle.class });
			onCreate.setAccessible(true);
			Bundle bundle = new Bundle();
			bundle.putInt(FROM, FROM_EXTERNAL);
			onCreate.invoke(instance, new Object[] { bundle });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mDexPath = intent.getStringExtra(EXTRA_DEX_PATH);
		launchTarget(intent.getStringExtra(EXTRA_CLASS));
	}

	private void launchTarget() {
		PackageInfo pkgInfo = getPackageManager().getPackageArchiveInfo(
				mDexPath, 1);
		if (pkgInfo.activities != null && pkgInfo.activities.length > 0) {
			String activityName = pkgInfo.activities[0].name;
			mClass = activityName;
			try {
				AssetManager assetManager = AssetManager.class.newInstance();
				Method addAssetPath = assetManager.getClass().getMethod(
						"addAssetPath", String.class);
				addAssetPath.invoke(assetManager, mDexPath);
				mAssetManager = assetManager;
				Resources superRes = super.getResources();
				mResources = new Resources(mAssetManager,
						superRes.getDisplayMetrics(),
						superRes.getConfiguration());
			} catch (Exception e) {
				e.printStackTrace();
			}
			launchTarget(mClass);
		}

	}

}
