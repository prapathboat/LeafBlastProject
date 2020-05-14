package com.example.leafblast;

public class Detection {
    private String name;
    private String imageUrl;

    public Detection() {
        //empty constructor needed
    }

    public Detection(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        name = name;
        imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        imageUrl = imageUrl;
    }
}
