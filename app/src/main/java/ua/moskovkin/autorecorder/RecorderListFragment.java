package ua.moskovkin.autorecorder;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecorderListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecorderListAdapter mAdapter;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onContactSelected(String contactNumber);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recorder_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void updateUI() {
        RecordScanner scanner = new RecordScanner(getContext());
        Map<String, ArrayList<HashMap<String, String>>> recordBase = scanner.getDirList();

        if (mAdapter == null) {
            mAdapter = new RecorderListAdapter(recordBase);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecordBase(recordBase);
            mAdapter.notifyDataSetChanged();
        }
    }

    private String getContactName(final String phoneNumber) {
        Uri uri = Uri.parse("content://com.android.contacts/phone_lookup");
        String[] projection = new String[] { "display_name" };

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);
        }

        cursor.close();

        return contactName;
    }

    private class RecorderListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private String mRecord;

        public RecorderListHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.recorder_list_item);
        }

        public void bindRecorderDir(String singleRecord) {
            mRecord = singleRecord;
            if (getContactName(singleRecord).equals("")) {
                mTitleTextView.setText(singleRecord);
            } else {
                mTitleTextView.setText(getContactName(singleRecord));
            }
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onContactSelected(mRecord);
        }
    }

    private class RecorderListAdapter extends RecyclerView.Adapter<RecorderListHolder> {

        private Map<String, ArrayList<HashMap<String, String>>> recordBase;
        private ArrayList<String> recordsDirName;

        private RecorderListAdapter(Map<String, ArrayList<HashMap<String, String>>> recordBase) {
            this.recordBase = recordBase;
            recordsDirName = new ArrayList<>();
            for(String key : recordBase.keySet()) {
                recordsDirName.add(key);
            }
        }

        @Override
        public RecorderListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recorder_list_item, parent, false);

            return new RecorderListHolder(view);
        }

        @Override
        public void onBindViewHolder(RecorderListHolder holder, int position) {
            String singleRecord = recordsDirName.get(position);
            holder.bindRecorderDir(singleRecord);
        }

        @Override
        public int getItemCount() {
            return recordBase.size();
        }

        public void setRecordBase(Map<String, ArrayList<HashMap<String, String>>> recordBase) {
            this.recordBase = recordBase;
        }
    }
}
