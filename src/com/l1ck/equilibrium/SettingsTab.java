package com.l1ck.equilibrium;

import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class SettingsTab extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingstab);

		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		// Create an Intent for the regular live wallpaper preferences activity
		intent = new Intent().setClass(this, Settings.class);

		// Initialize a TabSpec and set the intent
		spec = tabHost.newTabSpec("TabTitle").setContent(intent);
		spec.setIndicator("TabTitle");

		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
		
		AdManager.setTestDevices(new String[] {
				AdManager.TEST_EMULATOR,	// Android emulator
				//"54C99084BAD7C6DA232F0DD4215BA36D", // Tattoo
		});
		AdView adView = (AdView)this.findViewById(R.id.ad);
		adView.requestFreshAd();
		adView.setRequestInterval(60);
	}

}
