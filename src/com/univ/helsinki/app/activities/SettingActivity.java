package com.univ.helsinki.app.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.univ.helsinki.app.R;

public class SettingActivity extends  PreferenceActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);
 
    }
}
