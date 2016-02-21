package ua.moskovkin.autorecorder.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.File;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Preference dirPreference;
    private SharedPreferences preference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String settings = getArguments().getString("category");
        if ("general".equals(settings)) {
            addPreferencesFromResource(R.xml.general_settings);
        } else if ("recording".equals(settings)) {
            addPreferencesFromResource(R.xml.recording_settings);
            dirPreference = findPreference("app_save_path");
            dirPreference.setSummary(preference.getString("app_save_path",
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + getString(R.string.app_name)));
            dirPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), DirChooserActivity.class));
                    return false;
                }
            });

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}
