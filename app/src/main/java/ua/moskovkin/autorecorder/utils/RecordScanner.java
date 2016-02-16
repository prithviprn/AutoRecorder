package ua.moskovkin.autorecorder.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import ua.moskovkin.autorecorder.R;

public class RecordScanner {
    private String MEDIA_PATH;

    public RecordScanner(Context context) {
        MEDIA_PATH = Environment.getExternalStorageDirectory()
                + File.separator
                + context.getString(R.string.app_name);
    }

    public TreeMap<String , String> getFileList() {
        TreeMap<String, String> song = new TreeMap<>();
        File home = new File(MEDIA_PATH);
        File[] subDirs = home.listFiles();
        for (File file : subDirs){
            if (file.listFiles(new FileExtensionFilter()).length > 0) {
                File[] files = file.listFiles(new FileExtensionFilter());
                for (File record : files) {
                    song.put(record.getName().substring(0, (record.getName().length() - 4)), record.getPath());
                }
            }
        }
        return song;
    }

    public TreeMap<String, ArrayList<HashMap<String, String>>> getDirList() {
        File home = new File(MEDIA_PATH);
        TreeMap<String, ArrayList<HashMap<String, String>>> list = new TreeMap<>();
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
        File home = new File(MEDIA_PATH + File.separator + dirName);

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
            return (name.contains("."));
        }
    }
}
