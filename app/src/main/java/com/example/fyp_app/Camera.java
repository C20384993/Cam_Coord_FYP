package com.example.fyp_app;

public class Camera {

    int CameraID; //ID generated when the camera is added in AddCameraActivity.java.
    String CameraName; //A name set by the user/owner for the camera.
    String CamUsername; //The username required to view the RTSP stream from the camera.
    String CamPassword; //The password required to view the RTSP stream from the camera.

    //TODO: Add CamIP value to IPtable in SQL.
    String CamIP; //The local IP address of the camera.

    public int getCameraID() {
        return CameraID;
    }

    public void setCameraID(int cameraID) {
        CameraID = cameraID;
    }

    public String getCameraName() {
        return CameraName;
    }

    public void setCameraName(String cameraName) {
        CameraName = cameraName;
    }

    public String getCamUsername() {
        return CamUsername;
    }

    public void setCamUsername(String camUsername) {
        CamUsername = camUsername;
    }

    public String getCamPassword() {
        return CamPassword;
    }

    public void setCamPassword(String camPassword) {
        CamPassword = camPassword;
    }
}//end class
