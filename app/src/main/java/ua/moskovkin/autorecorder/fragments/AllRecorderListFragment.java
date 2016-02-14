package ua.moskovkin.autorecorder.fragments;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.utils.RecordScanner;

public class AllRecorderListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecorderAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recorder_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recorder_list_recycler_view);
        LinearLayoutManager mLayout = new LinearLayoutManager(getActivity());
        mLayout.setReverseLayout(true);
        mLayout.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayout);

        updateUI();

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

    private String getContactName(final String phoneNumber) {
        Uri uri = Uri.parse("content://com.android.contacts/phone_lookup");
        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};

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

    private String getContactImage(final String phoneNumber) {
        Uri uri = Uri.parse("content://com.android.contacts/phone_lookup");
        String[] projection = new String[] {ContactsContract.PhoneLookup.PHOTO_URI};

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        String contactImageUri = "";

        if (cursor.moveToFirst())
        {
            if (cursor.getString(0) != null)
                contactImageUri = cursor.getString(0);
        }

        cursor.close();

        return contactImageUri;
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

            if (!getContactImage(splitedPath[splitedPath.length - 2]).equals("")) {
                Uri uri = Uri.parse(getContactImage(splitedPath[splitedPath.length - 2]));
                mContactImage.setImageURI(uri);
            }

            Calendar calendar = new GregorianCalendar(
                    Integer.parseInt(splitedPath[splitedPath.length - 1].substring(0, 4)),
                    Integer.parseInt(splitedPath[splitedPath.length - 1].substring(4, 6)) - 1,
                    Integer.parseInt(splitedPath[splitedPath.length - 1].substring(6, 8)),
                    Integer.parseInt(splitedPath[splitedPath.length - 1].substring(8, 10)),
                    Integer.parseInt(splitedPath[splitedPath.length - 1].substring(10, 12)),
                    Integer.parseInt(splitedPath[splitedPath.length - 1].substring(12, 14)));

            mRecord = singleRecordTitle;
            mPath = path;

            if (getContactName(splitedPath[splitedPath.length - 2]).equals("")) {
                mTitleTextView.setText(splitedPath[splitedPath.length - 2]);
            } else {
                mTitleTextView.setText(getContactName(splitedPath[splitedPath.length - 2]));
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
//            String dir = getArguments().getString(ARG_RECORDER_DIR_ID);
//            CallRecorder recorder = new CallRecorder(MainActivity.appFolder + File.separator + dir + File.separator + mRecord + ".3gp");
//            recorder.startPlaying();
//            Uri uri = Uri.parse(mPath);
//            Intent i = new Intent(Intent.ACTION_VIEW, uri);
//            i.setDataAndType(uri,"video/3gpp");
//            startActivity(i);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            CustomAudioPlayer player = new CustomAudioPlayer();
            player.setPath(mPath);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_up_start,R.anim.fragment_slide_up_end)
                    .replace(R.id.player_container, player, "player").commit();
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
