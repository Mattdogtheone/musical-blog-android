package com.example.musicalblog.Model;

public class PostModel {

    String pImage, pTitle, pDescription, pId;

    public PostModel() {
    }

    public PostModel(String pImage, String pTitle, String pDescription, int pId) {
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.pId = String.valueOf(pId);
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpId() { return pId; }

    public void setpId(String pId) { this.pId = pId; }
}
