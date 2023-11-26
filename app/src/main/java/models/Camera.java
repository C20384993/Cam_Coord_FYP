package models;

public class Camera {
    private String cameraname;
    private String camusername;
    private String campassword;
    private int userid;


    public String getCameraname() {
        return cameraname;
    }

    public void setCameraname(String cameraname) {
        this.cameraname = cameraname;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
