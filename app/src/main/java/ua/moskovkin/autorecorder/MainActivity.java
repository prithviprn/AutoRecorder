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
        if (fragment != null && fragment.isVisible()) {
            RecorderListFragment recorderListFragment = new RecorderListFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_right_start, R.anim.fragment_slide_right_end)
                    .replace(R.id.fragment_container, recorderListFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }
}
