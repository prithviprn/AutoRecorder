package ua.moskovkin.autorecorder.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;

import java.io.File;

import ua.moskovkin.autorecorder.R;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    SwitchPreference sdCardToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String settings = getArguments().getString("category");
        if ("general".equals(settings)) {
            addPreferencesFromResource(R.xml.general_settings);
        } else if ("recording".equals(settings)) {
            addPreferencesFromResource(R.xml.recording_settings);

            sdCardToggle = (SwitchPreference) findPreference("save_on_sd");
            File[] dirs = ContextCompat.getExternalFilesDirs(getActivity(), null);
            if (dirs.length > 1) {
                sdCardToggle.setEnabled(true);
            } else {
                sdCardToggle.setEnabled(false);
            }
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
