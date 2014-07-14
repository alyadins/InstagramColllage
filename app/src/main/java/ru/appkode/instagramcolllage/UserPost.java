package ru.appkode.instagramcolllage;

import android.graphics.Bitmap;

/**
 * Created by lexer on 14.07.14.
 */
public class UserPost {
    public Bitmap image;
    public int likes;

    public UserPost(Bitmap image, int likes) {
        this.image = image;
        this.likes = likes;
    }
}
