package com.l1ck.closetozero;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_view);
        
        /*PreferenceManager.getDefaultSharedPreferences(getBaseContext()).registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
				Log.i("Equilibrium", "Changed: "+key+": "+String.valueOf(sp.getBoolean(key, false)));
			}
	    	
	    });*/
        
        updateSummary();
    }
	
	public void updateSummary() {
        for (int i = 1; i <= 2; i++) {
        	ListPreference type = (ListPreference) findPreference("p"+String.valueOf(i)+"Cpu");
        	ListPreference color = (ListPreference) findPreference("p"+String.valueOf(i)+"Color");
        	if (type != null && color != null) {
        		Preference pf = findPreference("p"+String.valueOf(i));
        		pf.setSummary(type.getEntry() + " - " + color.getEntry());
        	}
        }
	}

}
