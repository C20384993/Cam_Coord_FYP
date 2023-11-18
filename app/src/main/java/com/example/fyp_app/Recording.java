package com.example.fyp_app;
//Data Model, used when sending a POST request to save a recording to SQL. UserRequest
public class Recording {
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

    private String filename;
    private String creationdate;
    private int userid;
    private String relativefilepath;
    private int camerasid;

    /*public Recording(String filename, String creationdate, int userid, String relativefilepath,
                     int camerasid){
        this.filename = filename;
        this.creationdate = creationdate;
        this.userid = userid;
        this.relativefilepath = relativefilepath;
        this.camerasid = camerasid;
    }//end constructor */

}
