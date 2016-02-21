package ua.moskovkin.autorecorder.preference;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import ua.moskovkin.autorecorder.R;

public class DirChooserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private String startPath;
    private ArrayList<File> dirList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dir_chooser_layout);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        startPath = Environment.getExternalStorageDirectory().getPath();
        dirList = getDirlist(startPath);

        listView = (ListView) findViewById(R.id.list_view);
        DirAdapter adapter = new DirAdapter(this, dirList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dirList = getDirlist(dirList.get(position).getPath());
                DirAdapter adapter = new DirAdapter(context, dirList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
    
    private ArrayList<File> getDirlist(String path) {
        ArrayList<File> resultArrayList = new ArrayList<>();
        File upDir;
        String previusPath;
        if (path.endsWith("..")) {
            previusPath = path.substring(0, path.length() - 3);
            upDir = new File(previusPath).getParentFile();
        } else {
            upDir = new File(path);
        }
        try {
            for (File dir : upDir.listFiles()) {
                if (dir.canRead()) {
                    if (dir.isDirectory() && !dir.getName().contains(".")) {
                        resultArrayList.add(dir);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!upDir.getAbsolutePath().equals("/")) {
            resultArrayList.add(new File(upDir.getAbsolutePath(), ".."));
        }
        Collections.sort(resultArrayList);

        return resultArrayList;
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
