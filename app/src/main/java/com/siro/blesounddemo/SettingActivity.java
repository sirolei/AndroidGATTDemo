package com.siro.blesounddemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by siro on 2016/1/25.
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (null == savedInstanceState){
            getFragmentManager().beginTransaction().add(R.id.container, new SettingFragment()).commit();
        }
    }

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

       @Override
       public void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           addPreferencesFromResource(R.xml.pref_settings);
           SharedPreferences defaultSpf = PreferenceManager.getDefaultSharedPreferences(getActivity());
           String mtuDefault = getString(R.string.pref_mtu_default);
           String mtuSize = defaultSpf.getString(getString(R.string.pref_mtu_key), mtuDefault);
           Preference mtuPref = findPreference(getString(R.string.pref_mtu_key));
           bindPref(mtuPref);
           onPreferenceChange(mtuPref,mtuSize);
       }

        private void bindPref(Preference pref){
            pref.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof EditTextPreference){
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(newValue.toString());
            }
            return true;
        }
    }

}
