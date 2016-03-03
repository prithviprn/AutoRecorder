package ua.moskovkin.autorecorder.preference;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int DIR_CHOSEN = 177;
    private Preference dirPreference;
    private Preference askForPinPreference;
    private SharedPreferences defaultPreference;
    private ListPreference minDurationPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defaultPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String settings = getArguments().getString("category");
        if ("general".equals(settings)) {
            addPreferencesFromResource(R.xml.general_settings);
            askForPinPreference = findPreference(Constants.SETTING_PASS_PROTECTION_KEY);
            askForPinPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, Object newValue) {
                    if ((boolean) newValue) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        final View dialogView = inflater.inflate(R.layout.set_password, null);
                        final EditText newPin = (EditText) dialogView.findViewById(R.id.new_pass);
                        final EditText repeatPin = (EditText) dialogView.findViewById(R.id.repeat_pass);

                        builder.setView(dialogView)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int id) {
                                        if (newPin.getText().toString().equals(repeatPin.getText().toString())) {
                                            defaultPreference.edit().putString("password", newPin.getText().toString()).commit();
                                        } else {
                                            ((SwitchPreference) preference).setChecked(false);
                                            dialog.cancel();
                                            Toast.makeText(getActivity(), R.string.pin_not_match, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((SwitchPreference) preference).setChecked(false);
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                    }
                    return true;
                }
            });
        } else if ("recording".equals(settings)) {
            addPreferencesFromResource(R.xml.recording_settings);
            dirPreference = findPreference(Constants.SETTING_APP_SAVE_PATH_KEY);
            dirPreference.setSummary(defaultPreference.getString(Constants.SETTING_APP_SAVE_PATH_KEY,
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + getString(R.string.app_name)));
            dirPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivityForResult(new Intent(getActivity(), DirChooserActivity.class), DIR_CHOSEN);
                    return false;
                }
            });

            minDurationPreference = (ListPreference) findPreference(Constants.SETTING_MIN_RECORD_DURATION_KEY);
            minDurationPreference.setTitle(getString(R.string.min_rec_duration)
                    + " (" + defaultPreference.getString(Constants.SETTING_MIN_RECORD_DURATION_KEY, "0")
                    + ")");
            minDurationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setTitle(getString(R.string.min_rec_duration)
                            + " (" + newValue + ")");
                    return true;
                }
            });
        } else if ("cloud".equals(settings)) {
            addPreferencesFromResource(R.xml.cloud_settings);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIR_CHOSEN) {

            if (resultCode == Activity.RESULT_OK) {
                dirPreference.setSummary(data.getStringExtra("path"));
                dirPreference.getEditor().putString(Constants.SETTING_APP_SAVE_PATH_KEY,
                        data.getStringExtra("path")).commit();
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
