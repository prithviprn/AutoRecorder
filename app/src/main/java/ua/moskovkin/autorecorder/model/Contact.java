package ua.moskovkin.autorecorder.model;

import android.net.Uri;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

public class Contact {
    private UUID id;
    private String contactNumber;
    private String contactName;
    private String contactImageUri;
    private ArrayList<Record> records;

    public Contact() {}

    public Contact(String contactNumber, String contactName, String contactImageUri) {
        id = UUID.randomUUID();
        this.contactNumber = contactNumber;
        this.contactName = contactName;
        this.contactImageUri = contactImageUri;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactImageUri() {
        return contactImageUri;
    }

    public void setContactImageUri(String contactImageUri) {
        this.contactImageUri = contactImageUri;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }
}
