package com.pzf.dynamicloadclient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI(mProxyActivity);
	}

	private void initUI(final Context context) {
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn_client).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityByProxy(TestActivity.class.getName());
				Toast.makeText(mProxyActivity, "this msg form client Activity",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public View findViewById(int id) {
		if (mProxyActivity == this) {
			return super.findViewById(id);
		} else {
			return mProxyActivity.findViewById(id);

		}

	}
}
