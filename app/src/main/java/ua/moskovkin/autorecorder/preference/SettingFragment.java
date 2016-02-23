package ua.moskovkin.autorecorder.preference;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.File;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int DIR_CHOSEN = 177;
    private Preference dirPreference;
    private Preference askForPinPreference;
    private SharedPreferences defaultPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defaultPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String settings = getArguments().getString("category");
        if ("general".equals(settings)) {
            addPreferencesFromResource(R.xml.general_settings);
            askForPinPreference = findPreference("pass_protection");
            askForPinPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, Object newValue) {
                    if ((boolean) newValue) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        final View dialogView = inflater.inflate(R.layout.set_password, null);
                        final EditText valueView = (EditText) dialogView.findViewById(R.id.new_pass);;

                        builder.setView(dialogView)
                                .setTitle(getString(R.string.set_pass_title))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int id) {
                                        defaultPreference.edit().putString("password", valueView.getText().toString()).commit();
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
            dirPreference = findPreference("app_save_path");
            dirPreference.setSummary(defaultPreference.getString("app_save_path",
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + getString(R.string.app_name)));
            dirPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivityForResult(new Intent(getActivity(), DirChooserActivity.class), DIR_CHOSEN);
                    return false;
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIR_CHOSEN) {

            if (resultCode == Activity.RESULT_OK) {
                dirPreference.setSummary(data.getStringExtra("path"));
                dirPreference.getEditor().putString("app_save_path", data.getStringExtra("path")).commit();
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
