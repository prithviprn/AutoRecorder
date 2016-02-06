package ua.moskovkin.autorecorder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RecordScanner {
    private final String MEDIA_PATH = MainActivity.appFolder.getPath();

    public RecordScanner() {
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
            return (name.endsWith(".3gp") || name.endsWith(".3GP"));
        }
    }
}
