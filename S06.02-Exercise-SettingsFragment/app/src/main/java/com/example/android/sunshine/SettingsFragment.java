package com.example.android.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i=0; i< count;i++){
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)){
                setPreferenceSummary(p,sharedPreferences.getString(p.getKey(),""));
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference){
            ListPreference listPreference= (ListPreference)preference;
            setPreferenceSummary(preference, listPreference.getValue());
        }
        else if (preference instanceof EditTextPreference){
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            setPreferenceSummary(preference, editTextPreference.getText());
        }

    }

    public void setPreferenceSummary(Preference preference, Object value){
        String stringValue = value.toString();
        if (preference instanceof ListPreference){
            ListPreference listPreference= (ListPreference)preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(listPreference.getEntries()[index]);

        }
        else {
            preference.setSummary(stringValue);
        }

    }

    @Override
    public void onStart() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
}
