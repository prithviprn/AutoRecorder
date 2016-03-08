package ua.moskovkin.autorecorder.model;

import java.util.UUID;

import ua.moskovkin.autorecorder.utils.Utils;

public class Record implements Comparable<Record>{
    private UUID id;
    private String contactId;
    private String recordNumber;
    private String recordFileName;
    private String recordPath;
    private String fileSize;
    private String contactImageUri;
    private String date;
    private int isIncoming;
    private int hours;
    private int minutes;
    private int seconds;
    private int inFavorite;

    public Record() {}

    public Record(String contactId, String recordNumber, String recordFileName,
                  String recordPath, String fileSize, String contactImageUri, String date, int isIncoming,
                  int hours, int minutes, int seconds) {
        id =UUID.randomUUID();
        this.contactId = contactId;
        this.recordNumber = recordNumber;
        this.recordFileName = recordFileName;
        this.recordPath = recordPath;
        this.fileSize = fileSize;
        this.contactImageUri = contactImageUri;
        this.date = date;
        this.isIncoming = isIncoming;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        inFavorite = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getRecordFileName() {
        return recordFileName;
    }

    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getContactImageUri() {
        return contactImageUri;
    }

    public void setContactImageUri(String contactImageUri) {
        this.contactImageUri = contactImageUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int isIncoming() {
        return isIncoming;
    }

    public void setIsIncoming(int isIncoming) {
        this.isIncoming = isIncoming;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public int getInFavorite() {
        return inFavorite;
    }

    public void setInFavorite(int inFavorite) {
        this.inFavorite = inFavorite;
    }

    @Override
    public int compareTo(Record another) {
        long left = Utils.getCalendarFromFile(getRecordFileName().split("/")).getTimeInMillis();
        long right = Utils.getCalendarFromFile(another.getRecordPath().split("/")).getTimeInMillis();
        return (int) (left - right);
    }
}
