package ua.moskovkin.autorecorder.preference;

import android.preference.PreferenceActivity;

import java.util.List;

import ua.moskovkin.autorecorder.R;

public class SettingActivity extends PreferenceActivity {


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}
