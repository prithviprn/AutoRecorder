package ua.moskovkin.autorecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;

import ua.moskovkin.autorecorder.fragments.AllRecorderListFragment;
import ua.moskovkin.autorecorder.fragments.IncomingRecorderListFragment;
import ua.moskovkin.autorecorder.fragments.OutgoingRecorderListFragment;
import ua.moskovkin.autorecorder.fragments.RecorderFragment;
import ua.moskovkin.autorecorder.fragments.RecorderListFragment;
import ua.moskovkin.autorecorder.preference.SettingActivity;

public class MainActivity extends SingleFragmentActivity implements RecorderListFragment.Callbacks {
    public static File appFolder;
    private final static int PASS_REQUEST = 443;
    private Toolbar toolbar;
    private ToggleButton mToggleButton;
    private TextView mToggleStatusTextView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mDrawerList;
    private View drawerHeader;
    private FragmentManager fm;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("pass_protection", false)) {
            Intent i = new Intent(this, PassActivity.class);
            startActivityForResult(i, PASS_REQUEST);
        }
        fm = getSupportFragmentManager();
        appFolder = new File(settings.getString("app_save_path",
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + getString(R.string.app_name)));
        if(!appFolder.exists()) {
            try {
                appFolder.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (NavigationView) findViewById(R.id.left_drawer);
        mDrawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (fm.findFragmentByTag("player") != null) {
                    fm.beginTransaction().remove(fm.findFragmentByTag("player")).commit();
                }
                switch (id) {
                    case R.id.on_off_recording_toggle_button: {

                    }
                    case R.id.all_recordings_drawer_item: {
                        replaceRecorderListFragment(new AllRecorderListFragment(), "allListFragment");
                        break;
                    }
                    case R.id.incoming_drawer_item: {
                        replaceRecorderListFragment(new IncomingRecorderListFragment(), "incomingListFragment");
                        break;
                    }
                    case R.id.outgoing_drawer_item: {
                        replaceRecorderListFragment(new OutgoingRecorderListFragment(), "outgoingListFragment");
                        break;
                    }
                    case R.id.by_contact_drawer_item: {
                        replaceRecorderListFragment(new RecorderListFragment(), "contactsListFragment");
                        break;
                    }
                    case R.id.settings: {
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    }
                    case R.id.exit: {
                        finish();
                        break;
                    }
                }
                return true;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        drawerHeader = mDrawerList.getHeaderView(0);
        mToggleStatusTextView = (TextView) drawerHeader.findViewById(R.id.toggle_status_text_view);
        mToggleButton = (ToggleButton) drawerHeader.findViewById(R.id.on_off_recording_toggle_button);
        mToggleButton.setChecked(settings.getBoolean(Constants.IS_RECORDING_ON, false));
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean(Constants.IS_RECORDING_ON, isChecked).commit();
                if (isChecked) {
                    mToggleStatusTextView.setText(R.string.enabled);
                } else {
                    mToggleStatusTextView.setText(R.string.disabled);
                }
            }
        });
        if (mToggleButton.isChecked()) {
            mToggleStatusTextView.setText(R.string.enabled);
        } else {
            mToggleStatusTextView.setText(R.string.disabled);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
        }
    }

    @Override
    protected Fragment createFragment() {
        return new AllRecorderListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onContactSelected(String contactNumber) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Fragment newDetail = RecorderFragment.newInstance(contactNumber);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_left_start, R.anim.fragment_slide_left_end)
                    .replace(R.id.fragment_container, newDetail, "detailFragment")
                    .commit();
        } else {
            Fragment newDetail = RecorderFragment.newInstance(contactNumber);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        RecorderFragment fragment = (RecorderFragment) getSupportFragmentManager().findFragmentByTag("detailFragment");
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else if (fragment != null && fragment.isVisible()) {
            RecorderListFragment recorderListFragment = new RecorderListFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_right_start, R.anim.fragment_slide_right_end)
                    .replace(R.id.fragment_container, recorderListFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    private void replaceRecorderListFragment(Fragment fragment, String tag) {
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
