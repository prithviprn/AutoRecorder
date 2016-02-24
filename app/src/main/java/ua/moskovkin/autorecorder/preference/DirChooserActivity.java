package ua.moskovkin.autorecorder.preference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ua.moskovkin.autorecorder.Constants;
import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.fragments.CreateDirDialog;
import ua.moskovkin.autorecorder.utils.CallRecorder;

public class DirChooserActivity extends AppCompatActivity implements CreateDirDialog.OnCompleteListener {
    private Toolbar toolbar;
    private ListView listView;
    private ArrayList<File> dirList;
    private Context context;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dir_chooser_layout);
        context = this;
        data = new Intent();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dirList = new ArrayList<>();
        dirList.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
        toolbar.setSubtitle(getString(R.string.app_name));

        listView = (ListView) findViewById(R.id.list_view);
        DirAdapter adapter = new DirAdapter(this, dirList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dirList.get(position).getName().endsWith("..")) {
                    dirList = getDirlist(Environment.getExternalStorageDirectory().getAbsolutePath());
                } else {
                    dirList = getDirlist(dirList.get(position).getPath());
                }
                DirAdapter adapter = new DirAdapter(context, dirList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
    
    private ArrayList<File> getDirlist(String path) {
        ArrayList<File> resultArrayList = new ArrayList<>();
        File upDir = new File(path);
        toolbar.setTitle(upDir.getName());
        toolbar.setSubtitle(upDir.getPath());
        data.putExtra("path", upDir.getAbsolutePath());
        try {
            for (File dir : upDir.listFiles()) {
                if (dir.isDirectory() && !dir.getName().contains(".") && dir.canWrite()) {
                    resultArrayList.add(dir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!upDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            resultArrayList.add(new File(upDir.getAbsolutePath(), ".."));
        }
        Collections.sort(resultArrayList);

        return resultArrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dir_chooser_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_ok:
                MediaRecorder recorder = new MediaRecorder();
                try {
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(data.getStringExtra("path") + File.separator + "45345362527");
                    recorder.prepare();
                    new File(data.getStringExtra("path"), "45345362527").delete();
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.wrong_dir, Toast.LENGTH_SHORT).show();
                } finally {
                    recorder = null;
                }
                return true;
            case R.id.action_new_dir:
                CreateDirDialog dirDialog = new CreateDirDialog();
                dirDialog.show(getSupportFragmentManager(), "dirDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onComplete(String dirName) {
        File createdDir = new File(toolbar.getSubtitle().toString(), dirName);
        createdDir.mkdir();
        dirList.add(createdDir);
        Collections.sort(dirList);
        DirAdapter adapter = new DirAdapter(context, dirList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class DirAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        ArrayList<File> dirList;

        public DirAdapter(Context context, ArrayList<File> dirList) {
            this.context = context;
            this.dirList = dirList;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dirList.size();
        }

        @Override
        public Object getItem(int position) {
            return dirList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView dirTextView = (TextView) view.findViewById(android.R.id.text1);
            dirTextView.setText(dirList.get(position).getName());

            return view;
        }
    }
}
