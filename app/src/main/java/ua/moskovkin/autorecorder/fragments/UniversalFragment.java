package ua.moskovkin.autorecorder.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.model.Record;
import ua.moskovkin.autorecorder.utils.DBHelper;
import ua.moskovkin.autorecorder.utils.Utils;

public class UniversalFragment extends Fragment {
    private static final String FRAGMENT_TAG = "TAG";
    public static final String ALL_RECORDS_FRAGMENT = "allRecords";
    public static final String INCOMING_RECORDS_FRAGMENT = "incomingRecords";
    public static final String OUTGOING_RECORDS_FRAGMENT = "outgoingRecords";
    private RecyclerView mRecyclerView;
    private RecorderAdapter mAdapter;
    private SharedPreferences settings;
    private DBHelper dbHelper;
    private ArrayList<Record> records;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DBHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder_list, container, false);
        String tag = getArguments().getString(FRAGMENT_TAG);
        switch (tag) {
            case ALL_RECORDS_FRAGMENT: {
                records = dbHelper.getAllRecords();
                break;
            }
            case INCOMING_RECORDS_FRAGMENT: {
                records = dbHelper.getIncomingRecords();
                break;
            }
            case OUTGOING_RECORDS_FRAGMENT: {
                records = dbHelper.getOutgoingRecords();
                break;
            }
        }
        Log.d(Constants.DEBUG_TAG, records.size() + " RECords size");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recorder_list_recycler_view);
        LinearLayoutManager mLayout = new LinearLayoutManager(getActivity());
        mLayout.setReverseLayout(true);
        mLayout.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayout);
        mRecyclerView.setHasFixedSize(true);

        updateUIWrapper();

        return view;
    }

    public static UniversalFragment newInstance(String tag) {
        UniversalFragment fragment = new UniversalFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TAG, tag);
        fragment.setArguments(args);

        return fragment;
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new RecorderAdapter(records);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            mAdapter.setRecordList(records);
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
        private Record record;

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

        public void bindRecorderItem(Record record) {

            this.record = record;

            if (!record.getContactImageUri().equals("")) {
                Uri uri = Uri.parse(record.getContactImageUri());
                mContactImage.setImageURI(uri);
            } else {
                mContactImage.setImageResource(R.drawable.contacts_icon);
            }

            if (Utils.getContactName(record.getRecordNumber(), getActivity()).equals("")) {
                mTitleTextView.setText(record.getRecordNumber());
            } else {
                mTitleTextView.setText(Utils.getContactName(record.getRecordNumber(), getActivity()));
            }

            mDateTextView.setText(record.getDate());

            mDurationTextView.setText(String.format("%02d:%02d:%02d",
                                                                    record.getHours(),
                                                                    record.getMinutes(),
                                                                    record.getSeconds()));

            if (record.isIncoming() == 1) {
                mCallState.setImageResource(android.R.drawable.sym_call_incoming);
            } else {
                mCallState.setImageResource(android.R.drawable.sym_call_outgoing);
            }

            mFileSIzeTextView.setText(String.format("%s kB", record.getFileSize()));
        }

        @Override
        public void onClick(View v) {
            Utils.playRecord(record.getRecordPath(), settings.getBoolean("internal_player", true),
                    getActivity().getSupportFragmentManager(), getActivity());
        }
    }

    private class RecorderAdapter extends RecyclerView.Adapter<RecorderHolder> {
        private ArrayList<Record> records = new ArrayList<>();

        private RecorderAdapter(ArrayList<Record> records) {
            this.records = records;
        }

        @Override
        public RecorderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.all_recorder_item, parent, false);

            return new RecorderHolder(view);
        }

        @Override
        public void onBindViewHolder(RecorderHolder holder, int position) {
            holder.bindRecorderItem(records.get(position));
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public void setRecordList(ArrayList<Record> records) {
            this.records = records;
        }
    }
}
