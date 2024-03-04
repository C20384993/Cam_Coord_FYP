package models;

public class RecordingRecyclerItem {

    int recordingid;
    String customname;
    String relativefilepath;
    String creationDate;
    int userid;
    int cameraid;

    public RecordingRecyclerItem(String customname, String relativefilepath) {
        this.customname = customname;
        this.relativefilepath = relativefilepath;
    }

    public String getCustomname() {
        return customname;
    }

    public String getRelativefilepath() {
        return relativefilepath;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getRecordingid() {
        return recordingid;
    }
    public int getUserid() {
        return userid;
    }

    public int getCameraid() {
        return cameraid;
    }
}
