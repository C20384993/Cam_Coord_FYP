package models;
//When getting recording info from db, it will have a fileid field.
//Sending recording info doesn't include a fileid, as the db auto-increments the field.
public class RecordingResponse {

    private int recordingid;
    private String customname;
    private String creationdate;
    private String relativefilepath;
    private int userid;
    private int cameraid;

    public int getRecordingid() {
        return recordingid;
    }

    public void setRecordingid(int recordingid) {
        this.recordingid = recordingid;
    }

    public String getCustomname() {
        return customname;
    }

    public void setCustomname(String customname) {
        this.customname = customname;
    }

    public String getCreationdate() {
        return creationdate;
    }
    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public String getRelativefilepath() {
        return relativefilepath;
    }
    public void setRelativefilepath(String relativefilepath) {this.relativefilepath = relativefilepath;}
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public int getCameraid() {
        return cameraid;
    }
    public void setCameraid(int cameraid) {
        this.cameraid = cameraid;
    }
}
