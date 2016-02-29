package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import ua.moskovkin.autorecorder.R;

public class RecordScanner {
    private String mediaPath;
    private SharedPreferences settings;
    private DBHelper dbHelper;
    private Context context;

    public RecordScanner(Context context) {
        this.context = context;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        mediaPath = settings.getString("app_save_path",
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + context.getString(R.string.app_name));
        dbHelper = new DBHelper(context);
    }

    public TreeMap<String, ArrayList<HashMap<String, String>>> getDirList() {
        TreeMap<String, ArrayList<HashMap<String, String>>> list = new TreeMap<>();
        File home = new File(mediaPath);
        if (home.listFiles() != null) {
            for (File file : home.listFiles()) {
                String dirName = file.getName();
                list.put(dirName, getRecordList(dirName));
            }
        }
        return list;
    }

    private ArrayList<HashMap<String, String>> getRecordList(String dirName){
        ArrayList<HashMap<String, String>> recordList = new ArrayList<>();
        File home = new File(mediaPath + File.separator + dirName);
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                recordList.add(song);
            }
        }
        return recordList;
    }

    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if (name.endsWith(".3gp")) return true;
            else if (name.endsWith(".mp4")) return true;
            else if (name.endsWith(".amr")) return true;
            else if (name.endsWith(".aac")) return true;
            else if (name.endsWith(".webm")) return true;

            return false;
        }
    }
}
