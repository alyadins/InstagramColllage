package ru.appkode.instagramcolllage;

import android.graphics.Bitmap;

/**
 * Created by lexer on 14.07.14.
 */
public class User {
    public String id;
    public String userName;
    public Bitmap image;

    public User(String id, String userName, Bitmap image) {
        this.id = id;
        this.userName = userName;
        this.image = image;
    }
}
