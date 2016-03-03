package ua.moskovkin.autorecorder.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.model.Record;
import ua.moskovkin.autorecorder.utils.DBHelper;
import ua.moskovkin.autorecorder.utils.Utils;

public class RecorderFragment extends Fragment {
    private static final String ARG_RECORDER_DIR_ID = "recorder_dir_id";
    private RecyclerView mRecyclerView;
    private RecorderAdapter mAdapter;
    private String contactUUID;
    private SharedPreferences settings;
    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactUUID = (String) getArguments().getSerializable(ARG_RECORDER_DIR_ID);
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DBHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recorder_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        ArrayList<Record> records = dbHelper.getRecordsForContact(contactUUID);
        if (mAdapter == null) {
            mAdapter = new RecorderAdapter(records);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            mAdapter.setRecordList(records);
            mAdapter.notifyDataSetChanged();
        }
    }

    public static RecorderFragment newInstance(String contactUUID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECORDER_DIR_ID, contactUUID);
        RecorderFragment fragment = new RecorderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class RecorderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDurationTextView;
        private TextView mFileSIzeTextView;
        private ImageView mCallState;
        private Record record;

        public RecorderHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.recorder_list_item);
            mDurationTextView = (TextView) itemView.findViewById(R.id.duration_text_view);
            mCallState = (ImageView) itemView.findViewById(R.id.call_state_icon);
            mFileSIzeTextView = (TextView) itemView.findViewById(R.id.file_size_text_view);
        }

        public void bindRecorderItem(Record record) {
            this.record = record;
            mTitleTextView.setText(record.getRecordFileName());

            mDurationTextView.setText(String.format("%02d:%02d:%02d",
                                                                    record.getHours(),
                                                                    record.getMinutes(),
                                                                    record.getSeconds()));
            if (record.isIncoming() == 1) {
                mCallState.setImageResource(android.R.drawable.sym_call_incoming);
            } else {
                mCallState.setImageResource(android.R.drawable.sym_call_outgoing);
            }
            mFileSIzeTextView.setText(record.getFileSize() + " kB");
        }

        @Override
        public void onClick(View v) {
            Utils.playRecord(record.getRecordPath(), settings.getBoolean(Constants.SETTING_INTERNAL_PLAYER_KEY, true),
                    getActivity().getSupportFragmentManager(), getActivity());
        }
    }

    private class RecorderAdapter extends RecyclerView.Adapter<RecorderHolder> {

        private ArrayList<Record> records;

        private RecorderAdapter(ArrayList<Record> records) {
            this.records = records;
        }

        @Override
        public RecorderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recorder_item, parent, false);

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
