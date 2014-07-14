package ru.appkode.instagramcolllage.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;

/**
 * Created by lexer on 04.06.14.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private OnImageDowloadCompeleteListener listener;

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap dBitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            dBitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dBitmap;
    }

    protected void onPostExecute(Bitmap result) {
        if (listener != null) {
            listener.onImageDownloadComplete(result);
        }
    }

    public void setOnImageDowloadCompleteListener(OnImageDowloadCompeleteListener l) {
        this.listener = l;
    }

    public interface OnImageDowloadCompeleteListener {
        public void onImageDownloadComplete(Bitmap bitmap);
    }
}
