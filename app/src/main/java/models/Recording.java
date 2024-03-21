package models;
//Data Model for recordings.
public class Recording {
    private String customname;
    private String relativefilepath;
    private int userid;
    private int cameraid;

    public String getCustomname() {
        return customname;
    }
    public void setCustomname(String customname) {
        this.customname = customname;
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
