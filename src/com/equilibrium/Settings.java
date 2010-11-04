package com.equilibrium;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.widget.LinearLayout;

public class Settings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_view);
    }

}
