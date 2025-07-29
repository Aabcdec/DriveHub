package com.example.web.query;

public class Images {
    private int imgId;
    private int aid;
    private String image;

    @Override
    public String toString() {
        return "Images{" +
                "imgId=" + imgId +
                ", aid=" + aid +
                ", image='" + image + '\'' +
                '}';
    }

    public Images() {
    }

    public Images(int imgId, int aid, String image) {
        this.imgId = imgId;
        this.aid = aid;
        this.image = image;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
