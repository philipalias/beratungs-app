package com.pages.beratungsapp;

import com.pages.beratungsapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OLD_MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tabs_layout);
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.btn_nberatung:
			startActivity(new Intent(this, TabActivity.class));
			break;
		case R.id.btn_fberatung:
			startActivity(new Intent(this, TabActivity.class));
			break;
		}
	}
}
