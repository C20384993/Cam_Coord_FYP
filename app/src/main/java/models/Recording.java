package models;
//Data Model, used when sending a POST request to save a recording to SQL.
public class Recording {
    private String filename;
    private String creationdate;
    private int userid;
    private String relativefilepath;
    private int camerasid;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public int getCamerasid() {
        return camerasid;
    }

    public void setCamerasid(int camerasid) {
        this.camerasid = camerasid;
    }

}
