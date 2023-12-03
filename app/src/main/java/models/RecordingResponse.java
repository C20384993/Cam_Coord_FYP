package models;
//When getting recording info from db, it will have a fileid field.
//Sending recording info doesn't include a fileid, as the db auto-increments the field.
public class RecordingResponse {

    private int recordingid;
    private String recordingname;
    private String creationdate;
    private int userid;
    private String relativefilepath;
    private int cameraid;

    public int getRecordingid() {
        return recordingid;
    }

    public void setRecordingid(int recordingid) {
        this.recordingid = recordingid;
    }

    public String getRecordingname() {
        return recordingname;
    }

    public void setRecordingname(String recordingname) {
        this.recordingname = recordingname;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
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
