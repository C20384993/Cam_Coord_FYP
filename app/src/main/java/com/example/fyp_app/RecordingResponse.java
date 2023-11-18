package com.example.fyp_app;

public class RecordingResponse {

    public int getFileid() {
        return fileid;
    }

    public void setFileid(int fileid) {
        this.fileid = fileid;
    }

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

    private int fileid;
    private String filename;
    private String creationdate;
    private int userid;
    private String relativefilepath;
    private int camerasid;

}
