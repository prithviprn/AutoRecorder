package ua.moskovkin.autorecorder;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends SingleFragmentActivity implements RecorderListFragment.Callbacks {
    public static File appFolder;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mDrawerList;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (NavigationView) findViewById(R.id.left_drawer);
        mDrawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                FragmentManager fm = getSupportFragmentManager();
                switch (id) {
                    case R.id.all_recordings_drawer_item: {
                        AllRecorderListFragment fragment = new AllRecorderListFragment();
                        if (fm.findFragmentByTag("player") != null) {
                            fm.beginTransaction().hide(fm.findFragmentByTag("player")).commit();
                        }
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fragment_container, fragment, "allListFragment")
                                .commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    }
                    case R.id.incoming_drawer_item: {
                        IncomingRecorderListFragment fragment = new IncomingRecorderListFragment();
                        if (fm.findFragmentByTag("player") != null) {
                            fm.beginTransaction().hide(fm.findFragmentByTag("player")).commit();
                        }
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fragment_container, fragment, "incomingListFragment")
                                .commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    }
                    case R.id.outgoing_drawer_item: {
                        OutgoingRecorderListFragment fragment = new OutgoingRecorderListFragment();
                        if (fm.findFragmentByTag("player") != null) {
                            fm.beginTransaction().hide(fm.findFragmentByTag("player")).commit();
                        }
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fragment_container, fragment, "outgoingListFragment")
                                .commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    }
                    case R.id.by_contact_drawer_item: {
                        RecorderListFragment fragment = new RecorderListFragment();
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.fragment_container, fragment, "listFragment")
                                .commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
