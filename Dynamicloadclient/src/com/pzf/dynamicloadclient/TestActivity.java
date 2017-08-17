package com.pzf.dynamicloadclient;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class TestActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button button = new Button(mProxyActivity);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		button.setBackgroundColor(Color.YELLOW);
		button.setText("’‚ «≤‚ ‘“≥√Ê");
		setContentView(button);
	}
}
