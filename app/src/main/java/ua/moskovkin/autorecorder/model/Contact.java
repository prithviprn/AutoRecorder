package ua.moskovkin.autorecorder.model;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

public class Contact {
    private UUID id;
    private String contactNumber;
    private String contactName;
    private URI contactImageUri;
    private ArrayList<File> records;

    public Contact(String contactNumber, String contactName, URI contactImageUri, ArrayList<File> records) {
        id = UUID.randomUUID();
        this.contactNumber = contactNumber;
        this.contactName = contactName;
        this.contactImageUri = contactImageUri;
        this.records = records;
    }

    public UUID getId() {
        return id;
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

    public URI getContactImageUri() {
        return contactImageUri;
    }

    public void setContactImageUri(URI contactImageUri) {
        this.contactImageUri = contactImageUri;
    }

    public ArrayList<File> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<File> records) {
        this.records = records;
    }
}
