package com.gtug.wallpaper;

import com.gtug.wallpaper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DemoSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    getPreferenceManager().setSharedPreferencesName(DemoService.SHARED_PREFS);
	    addPreferencesFromResource(R.xml.settings);
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	}
	
	@Override
	protected void onDestroy() {
	    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    super.onDestroy();
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}
}