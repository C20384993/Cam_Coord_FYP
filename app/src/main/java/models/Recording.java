package models;
//Data Model, used when sending a POST request to save a recording to SQL.
public class Recording {
    private String recordingname;
    private int userid;
    private String relativefilepath;
    private int cameraid;

    public String getRecordingname() {
        return recordingname;
    }

    public void setRecordingname(String recordingname) {
        this.recordingname = recordingname;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getRelativefilepath() {
        return relativefilepath;
    }

    public void setRelativefilepath(String relativefilepath) {
        this.relativefilepath = relativefilepath;
    }

    public int getCameraid() {
        return cameraid;
    }

    public void setCameraid(int cameraid) {
        this.cameraid = cameraid;
    }

}
