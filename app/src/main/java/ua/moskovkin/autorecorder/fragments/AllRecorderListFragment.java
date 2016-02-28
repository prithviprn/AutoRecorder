package ua.moskovkin.autorecorder.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.utils.RecordScanner;
import ua.moskovkin.autorecorder.utils.Utils;

public class AllRecorderListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecorderAdapter mAdapter;
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recorder_list_recycler_view);
        LinearLayoutManager mLayout = new LinearLayoutManager(getActivity());
        mLayout.setReverseLayout(true);
        mLayout.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayout);
        mRecyclerView.setHasFixedSize(true);

        updateUIWrapper();

        return view;
    }

    private void updateUI() {
        TreeMap<String, String> allRecords = new RecordScanner(getContext()).getFileList();
        if (mAdapter == null) {
            mAdapter = new RecorderAdapter(allRecords);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            mAdapter.setRecordList(allRecords);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateUIWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<>();

            final List<String> permissionsList = new ArrayList<>();
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("SD Card");
            if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
                permissionsNeeded.add("Read Contacts");
            if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
                permissionsNeeded.add("Record Audio");
            if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
                permissionsNeeded.add("Read Phone State");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = getString(R.string.grant_access_message) + " " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 1);
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 1);
                return;
            }
        }
        updateUI();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private class RecorderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mDurationTextView;
        private TextView mFileSIzeTextView;
        private ImageView mContactImage;
        private ImageView mCallState;
        private String mPath;
        private String mRecord;

        public RecorderHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.recorder_list_item);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mDurationTextView = (TextView) itemView.findViewById(R.id.duration_text_view);
            mContactImage = (ImageView) itemView.findViewById(R.id.contact_image_view);
            mCallState = (ImageView) itemView.findViewById(R.id.call_state_icon);
            mFileSIzeTextView = (TextView) itemView.findViewById(R.id.file_size_text_view);
        }

        public void bindRecorderItem(String singleRecordTitle, String path) {
            mPath = path;
            String[] splitedPath = path.split("/");

            if (!Utils.getContactImage(splitedPath[splitedPath.length - 2], getActivity()).equals("")) {
                Uri uri = Uri.parse(Utils.getContactImage(splitedPath[splitedPath.length - 2], getActivity()));
                mContactImage.setImageURI(uri);
            } else {
                mContactImage.setImageResource(R.drawable.contacts_icon);
            }

            Calendar calendar = Utils.getCalendarFromFile(splitedPath);

            mRecord = singleRecordTitle;
            mPath = path;

            if (Utils.getContactName(splitedPath[splitedPath.length - 2], getActivity()).equals("")) {
                mTitleTextView.setText(splitedPath[splitedPath.length - 2]);
            } else {
                mTitleTextView.setText(Utils.getContactName(splitedPath[splitedPath.length - 2], getActivity()));
            }

            mDateTextView.setText(String.format("%02d %s, %02d:%02d:%02d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)));

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
            int hours = (int) (duration / 3600);
            int minutes = (int) ((duration / 60) - (hours * 60));
            int seconds = (int) (duration - (hours * 3600) - (minutes * 60));
            mDurationTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            if (singleRecordTitle.endsWith("I")) {
                mCallState.setImageResource(android.R.drawable.sym_call_incoming);
            } else {
                mCallState.setImageResource(android.R.drawable.sym_call_outgoing);
            }

            File file = new File(path);
            String fileSize = String.valueOf(file.length()/1024);
            mFileSIzeTextView.setText(String.format("%s kB", fileSize));
        }

        @Override
        public void onClick(View v) {
            Utils.playRecord(mPath, settings.getBoolean("internal_player", true),
                    getActivity().getSupportFragmentManager(), getActivity());
        }
    }

    private class RecorderAdapter extends RecyclerView.Adapter<RecorderHolder> {
        private TreeMap<String, String> allRecords;
        private ArrayList<String> recordNames = new ArrayList<>();
        private ArrayList<String> recordPath = new ArrayList<>();

        private RecorderAdapter(TreeMap<String, String> allRecords) {
            this.allRecords = allRecords;
            for(Map.Entry<String,String> entry : allRecords.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                recordNames.add(key);
                recordPath.add(value);
            }
        }

        @Override
        public RecorderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.all_recorder_item, parent, false);

            return new RecorderHolder(view);
        }

        @Override
        public void onBindViewHolder(RecorderHolder holder, int position) {
            String singleRecord = recordNames.get(position);
            String singleRecordPath = recordPath.get(position);
            holder.bindRecorderItem(singleRecord, singleRecordPath);
        }

        @Override
        public int getItemCount() {
            return allRecords.size();
        }

        public void setRecordList(TreeMap<String, String> allRecords) {
            this.allRecords = allRecords;
        }
    }
}