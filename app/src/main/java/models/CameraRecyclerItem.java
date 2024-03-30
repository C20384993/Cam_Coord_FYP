package models;
//Data Model for items in the Recycler View of the CameraListActivity.
public class CameraRecyclerItem {

    private int cameraid;
    private String customname;
    private String camusername;
    private String campassword;
    private String rtspurl;
    private String streampath;

    public CameraRecyclerItem(int cameraid, String customname, String camusername,
                              String campassword, String rtspurl, String streampath) {
        this.cameraid = cameraid;
        this.customname = customname;
        this.camusername = camusername;
        this.campassword = campassword;
        this.rtspurl = rtspurl;
        this.streampath = streampath;
    }

    public int getCameraid() {
        return cameraid;
    }

    public void setCameraid(int cameraid) {
        this.cameraid = cameraid;
    }

    public String getCustomname() {
        return customname;
    }

    public void setCustomname(String customname) {
        this.customname = customname;
    }

    public String getCamusername() {
        return camusername;
    }

    public void setCamusername(String camusername) {
        this.camusername = camusername;
    }

    public String getCampassword() {
        return campassword;
    }

    public void setCampassword(String campassword) {
        this.campassword = campassword;
    }

    public String getRtspurl() {
        return rtspurl;
    }

    public void setRtspurl(String rtspurl) {
        this.rtspurl = rtspurl;
    }

    public String getStreampath() {
        return streampath;
    }

    public void setStreampath(String streampath) {
        this.streampath = streampath;
    }
}
