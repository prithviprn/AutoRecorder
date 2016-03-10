package ua.moskovkin.autorecorder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.ArrayList;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recorder_list_recycler_view);
        LinearLayoutManager mLayout = new LinearLayoutManager(getActivity());
        mLayout.setReverseLayout(true);
        mLayout.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayout);
        mRecyclerView.setHasFixedSize(true);

        updateUI();

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

    private class RecorderAdapter extends RecyclerView.Adapter<RecorderHolder> {
        private static final int CONTENT_HOLDER = 1;
        private static final int AD_HOLDER = 2;
        private ArrayList<Record> records = new ArrayList<>();
        private boolean isConnected;

        private RecorderAdapter(ArrayList<Record> records) {
            this.records = records;
            isConnected = isOnline();
        }

        @Override
        public RecorderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecorderHolder holder;
            View view;
            if (viewType == AD_HOLDER) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                view = layoutInflater.inflate(R.layout.ad_list_item, parent, false);

                holder = new RecorderHolder(view);
            } else {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                view = layoutInflater.inflate(R.layout.all_recorder_item, parent, false);
                holder = new RecorderHolder(view);
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecorderHolder holder, int position) {
            if (holder.getItemViewType() == CONTENT_HOLDER) {
                holder.bindRecorderItem(records.get(position));
            } else {
                holder.bindAdItem();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 10 == 0 && isConnected)
                return AD_HOLDER;
            return CONTENT_HOLDER;
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public void setRecordList(ArrayList<Record> records) {
            this.records = records;
        }

        public void deleteRecord(Record record) {
            records.remove(record);
            notifyDataSetChanged();
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    private class RecorderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mDurationTextView;
        private TextView mFileSIzeTextView;
        private ImageView mContactImage;
        private ImageView mCallState;
        private ImageView mIsFavorite;
        private ImageView mDeleteRecord;
        private Record record;
        private PublisherAdView adView;

        public RecorderHolder(View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.publisherAdView) != null) {
                adView = (PublisherAdView) itemView.findViewById(R.id.publisherAdView);
            } else {
                itemView.setOnClickListener(this);
                mTitleTextView = (TextView) itemView.findViewById(R.id.recorder_list_item);
                mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
                mDurationTextView = (TextView) itemView.findViewById(R.id.duration_text_view);
                mContactImage = (ImageView) itemView.findViewById(R.id.contact_image_view);
                mCallState = (ImageView) itemView.findViewById(R.id.call_state_icon);
                mFileSIzeTextView = (TextView) itemView.findViewById(R.id.file_size_text_view);
                mIsFavorite = (ImageView) itemView.findViewById(R.id.is_favorite);
                mDeleteRecord = (ImageView) itemView.findViewById(R.id.delete_record_image_view);
            }
        }

        public void bindRecorderItem(final Record record) {

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
            mIsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (record.getInFavorite() == 0) {
                        mIsFavorite.setImageResource(R.mipmap.ic_star_black);
                        record.setInFavorite(1);
                        dbHelper.changeIsFavoriteState(record, 1);
                    } else {
                        mIsFavorite.setImageResource(R.mipmap.ic_star_border);
                        record.setInFavorite(0);
                        dbHelper.changeIsFavoriteState(record, 0);
                    }
                }
            });
            if (record.getInFavorite() == 0) {
                mIsFavorite.setImageResource(R.mipmap.ic_star_border);
            } else {
                mIsFavorite.setImageResource(R.mipmap.ic_star_black);
            }
            mDeleteRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteRecord(record);
                    Utils.deleteRecord(record);
                    mAdapter.deleteRecord(record);
                }
            });
        }

        public void bindAdItem() {
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        @Override
        public void onClick(View v) {
            Utils.playRecord(record.getRecordPath(), settings.getBoolean(Constants.SETTING_INTERNAL_PLAYER_KEY, true),
                    getActivity().getSupportFragmentManager(), getActivity());
        }
    }
}
