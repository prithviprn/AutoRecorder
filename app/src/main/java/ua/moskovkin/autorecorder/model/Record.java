package ua.moskovkin.autorecorder.model;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

public class Record {
    private UUID id;
    private UUID contactId;
    private String recordNumber;
    private String recordFileName;
    private String recordPath;
    private String fileSize;
    private URI contactImageUri;
    private Date date;
    private boolean isIncoming;
    private int hours;
    private int minutes;
    private int seconds;

    public Record(UUID contactId, String recordNumber, String recordFileName,
                  String recordPath, String fileSize, URI contactImageUri, Date date, boolean isIncoming,
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
    }

    public UUID getId() {
        return id;
    }

    public UUID getContactId() {
        return contactId;
    }

    public void setContactId(UUID contactId) {
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

    public URI getContactImageUri() {
        return contactImageUri;
    }

    public void setContactImageUri(URI contactImageUri) {
        this.contactImageUri = contactImageUri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIsIncoming(boolean isIncoming) {
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
}
