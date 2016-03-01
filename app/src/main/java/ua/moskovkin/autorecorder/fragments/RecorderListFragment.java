package ua.moskovkin.autorecorder.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.model.Contact;
import ua.moskovkin.autorecorder.utils.DBHelper;

public class RecorderListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecorderListAdapter mAdapter;
    private Callbacks mCallbacks;
    private DBHelper dbHelper;

    public interface Callbacks {
        void onContactSelected(String contactUUID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ArrayList<Contact> contacts = dbHelper.getAllContacts();

        if (mAdapter == null) {
            mAdapter = new RecorderListAdapter(contacts);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecordBase(contacts);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RecorderListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private ImageView mContactImage;
        private Contact contact;

        public RecorderListHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.recorder_list_item);
            mContactImage = (ImageView) itemView.findViewById(R.id.contact_image_category);
        }

        public void bindRecorderDir(Contact contact) {
            this.contact = contact;
            if (contact.getContactName().equals("")) {
                mTitleTextView.setText(contact.getContactNumber());
            } else {
                mTitleTextView.setText(contact.getContactName());
            }
            if (!contact.getContactImageUri().equals("")) {
                Uri uri = Uri.parse(contact.getContactImageUri());
                mContactImage.setImageURI(uri);
            } else {
                mContactImage.setImageResource(R.drawable.contacts_icon);
            }
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onContactSelected(String.valueOf(contact.getId()));
        }
    }

    private class RecorderListAdapter extends RecyclerView.Adapter<RecorderListHolder> {
        private ArrayList<Contact> contacts;

        private RecorderListAdapter(ArrayList<Contact> contacts) {
            this.contacts = contacts;
        }

        @Override
        public RecorderListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recorder_list_item, parent, false);

            return new RecorderListHolder(view);
        }

        @Override
        public void onBindViewHolder(RecorderListHolder holder, int position) {
            holder.bindRecorderDir(contacts.get(position));
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public void setRecordBase(ArrayList<Contact> contacts) {
            this.contacts = contacts;
        }
    }
}
