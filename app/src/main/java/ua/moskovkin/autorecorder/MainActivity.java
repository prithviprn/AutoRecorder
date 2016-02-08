package ua.moskovkin.autorecorder;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.File;

public class MainActivity extends SingleFragmentActivity implements RecorderListFragment.Callbacks {
    public static File appFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appFolder = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        if(!appFolder.exists()) {
            try {
                appFolder.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Fragment createFragment() {
        return new RecorderListFragment();
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
        if (fragment != null && fragment.isVisible()) {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, new RecorderListFragment())
                    .commit();
        } else {
            super.onBackPressed();
        }
    }
}
