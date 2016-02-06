package ua.moskovkin.autorecorder;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class RecorderFragment extends Fragment {
    private static final String ARG_RECORDER_DIR_ID = "recorder_dir_id";
    private RecyclerView mRecyclerView;
    private RecorderAdapter mAdapter;
    private String recorderDirName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recorderDirName = (String) getArguments().getSerializable(ARG_RECORDER_DIR_ID);
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
        ArrayList<HashMap<String, String>> mRecorderDir = new RecordScanner().getDirList().get(recorderDirName);

        if (mAdapter == null) {
            mAdapter = new RecorderAdapter(mRecorderDir);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecordList(mRecorderDir);
            mAdapter.notifyDataSetChanged();
        }
    }

    public static RecorderFragment newInstance(String dirId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECORDER_DIR_ID, dirId);
        RecorderFragment fragment = new RecorderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class RecorderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDurationTextView;
        private TextView mFileSIzeTextView;
        private ImageView mCallState;
        private String mPath;
        private String mRecord;

        public RecorderHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.recorder_list_item);
            mDurationTextView = (TextView) itemView.findViewById(R.id.duration_text_view);
            mCallState = (ImageView) itemView.findViewById(R.id.call_state_icon);
            mFileSIzeTextView = (TextView) itemView.findViewById(R.id.file_size_text_view);
        }

        public void bindRecorderItem(String singleRecordTitle, String path) {
            mRecord = singleRecordTitle;
            mPath = path;
            mTitleTextView.setText(mRecord);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
            int hours = (int) (duration / 3600);
            int minutes = (int) ((duration / 60) - (hours * 60));
            int seconds = (int) (duration - (hours * 3600) - (minutes * 60));
            mDurationTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            if (singleRecordTitle.startsWith("I")) {
                mCallState.setImageResource(android.R.drawable.sym_call_incoming);
            } else {
                mCallState.setImageResource(android.R.drawable.sym_call_outgoing);
            }
            File file = new File(path);
            String fileSize = String.valueOf(file.length()/1024);
            mFileSIzeTextView.setText(fileSize + " kB");
        }

        @Override
        public void onClick(View v) {
//            String dir = getArguments().getString(ARG_RECORDER_DIR_ID);
//            CallRecorder recorder = new CallRecorder(MainActivity.appFolder + File.separator + dir + File.separator + mRecord + ".3gp");
//            recorder.startPlaying();
            Uri uri = Uri.parse(mPath);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setDataAndType(uri,"video/3gpp");
            startActivity(i);
        }
    }

    private class RecorderAdapter extends RecyclerView.Adapter<RecorderHolder> {

        private ArrayList<HashMap<String, String>> recorderList;

        private RecorderAdapter(ArrayList<HashMap<String, String>> recorderList) {
            this.recorderList = recorderList;
        }

        @Override
        public RecorderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recorder_item, parent, false);

            return new RecorderHolder(view);
        }

        @Override
        public void onBindViewHolder(RecorderHolder holder, int position) {
            String singleRecord = recorderList.get(position).get("songTitle");
            String singleRecordPath = recorderList.get(position).get("songPath");
            holder.bindRecorderItem(singleRecord, singleRecordPath);
        }

        @Override
        public int getItemCount() {
            return recorderList.size();
        }

        public void setRecordList(ArrayList<HashMap<String, String>> recorderList) {
            this.recorderList = recorderList;
        }
    }
}
