package ru.appkode.instagramcolllage;

import android.graphics.Bitmap;

/**
 * Created by lexer on 14.07.14.
 */
public class UserPhoto {
    public Bitmap image;
    public String imageUrl;
    public int likes;
    public String id;

    public UserPhoto(int likes, String id, String imageUrl) {
        this.likes = likes;
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
