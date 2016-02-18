package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import ua.moskovkin.autorecorder.R;

public class RecordScanner {
    private String mediaPath0;
    private String mediaPath1;
    private File[] dirs;
    private SharedPreferences settings;

    public RecordScanner(Context context) {
        dirs = ContextCompat.getExternalFilesDirs(context, null);
        mediaPath0 = dirs[0].getPath() + File.separator + context.getString(R.string.app_name);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("save_on_sd", false) || dirs.length > 1) {
            mediaPath1 = dirs[1].getPath() + File.separator + context.getString(R.string.app_name);
        }
    }

    public TreeMap<String , String> getFileList() {
        TreeMap<String, String> song = new TreeMap<>();
        File home = new File(mediaPath0);
        File[] subDirs = home.listFiles();
        for (File file : subDirs){
            if (file.listFiles(new FileExtensionFilter()).length > 0) {
                File[] files = file.listFiles(new FileExtensionFilter());
                for (File record : files) {
                    song.put(record.getName().substring(0, (record.getName().length() - 4)), record.getPath());
                }
            }
        }
        if (mediaPath1 != null) {
            File homeEx = new File(mediaPath1);
            File[] subDirsEx = homeEx.listFiles();
            for (File file : subDirsEx) {
                if (file.listFiles(new FileExtensionFilter()).length > 0) {
                    File[] files = file.listFiles(new FileExtensionFilter());
                    for (File record : files) {
                        song.put(record.getName().substring(0, (record.getName().length() - 4)), record.getPath());
                    }
                }
            }
        }
        return song;
    }

    public TreeMap<String, ArrayList<HashMap<String, String>>> getDirList() {
        TreeMap<String, ArrayList<HashMap<String, String>>> list = new TreeMap<>();
        File home = new File(mediaPath0);
        if (home.listFiles() != null) {
            for (File file : home.listFiles()) {
                String dirName = file.getName();
                list.put(dirName, getRecordList(dirName));
            }
        }
        if (mediaPath1 != null) {
            File homeEx = new File(mediaPath1);
            if (homeEx.listFiles() != null) {
                for (File file : homeEx.listFiles()) {
                    String dirName = file.getName();
                    list.put(dirName, getRecordList(dirName));
                }
            }
        }
        return list;
    }

    private ArrayList<HashMap<String, String>> getRecordList(String dirName){
        ArrayList<HashMap<String, String>> recordList = new ArrayList<>();
        File home = new File(mediaPath0 + File.separator + dirName);
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                recordList.add(song);
            }
        }
        if (mediaPath1 != null) {
            File homeEx = new File(mediaPath1 + File.separator + dirName);
            if (homeEx.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : homeEx.listFiles(new FileExtensionFilter())) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                    song.put("songPath", file.getPath());

                    recordList.add(song);
                }
            }
        }
        return recordList;
    }

    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.contains("."));
        }
    }
}
