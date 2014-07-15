package ru.appkode.instagramcolllage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;

import java.util.List;

public class CollageCreator extends AsyncTask<List<UserPhoto>, Void, Bitmap>  {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int BOTTOM = 3;

    private static final int MAX_WIDTH = 2048;
    private static final int MAX_HEIGHT = 2048;

    private int side = RIGHT;

    private Bitmap collage;

    private OnCollageCreatedListener listener;

    public CollageCreator(OnCollageCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(List<UserPhoto>... params) {
        return createCollage(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (listener != null) {
            listener.onCollageCreated(bitmap);
        }
    }

    private Bitmap createCollage(List<UserPhoto> bestPhotos) {

        collage = null;
        side = getFirstSide(bestPhotos.size());
        for (UserPhoto photo : bestPhotos) {
            addPhoto(photo.image);
        }

        return collage;
    }

    private void addPhoto(Bitmap photo) {
        if (collage == null) {
            collage = photo;
            return;
        }

        switch (side) {
            case RIGHT:
                addPhotoToRight(photo);
                break;
            case BOTTOM:
                addPhotoToBottom(photo);
                break;
            case LEFT:
                addPhotoToLeft(photo);
                break;
            case TOP:
                addPhotoToTop(photo);
                break;
        }

        side = getNextSide(side);
    }

    private void addPhotoToTop(Bitmap photo) {
        int w, h;

        w = collage.getWidth();
        photo = scaleByWidth(photo, w);
        h = photo.getHeight() + collage.getHeight();

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(photo, 0, 0, null);
        canvas.drawBitmap(collage, 0, photo.getHeight(), null);

        collage = scaleByHeight(temp, MAX_HEIGHT);
    }

    private void addPhotoToLeft(Bitmap photo) {
        int w, h;

        h = collage.getHeight();
        photo = scaleByHeight(photo, h);
        w = collage.getWidth() + photo.getWidth();

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(photo, 0, 0, null);
        canvas.drawBitmap(collage, photo.getWidth(), 0, null);

        collage = scaleByWidth(temp, MAX_WIDTH);
    }

    private void addPhotoToBottom(Bitmap photo) {

        int w, h;
        w = collage.getWidth();
        photo = scaleByWidth(photo, w);
        h = collage.getHeight() + photo.getHeight();

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(collage, 0, 0, null);
        canvas.drawBitmap(photo, 0, collage.getHeight(), null);

        collage = scaleByHeight(temp, MAX_HEIGHT);
    }

    private Bitmap scaleByHeight(Bitmap photo, int h) {
        float newWidth = ((float) h) / photo.getHeight() * photo.getWidth();
        return Bitmap.createScaledBitmap(photo, (int) newWidth, h, true);
    }

    private Bitmap scaleByWidth(Bitmap photo, int w) {
        float newHeight = ((float) w) / photo.getWidth() * photo.getHeight();
        return Bitmap.createScaledBitmap(photo, w, (int) newHeight, true);
    }



    private void addPhotoToRight(Bitmap photo) {
        int w, h;

        h = collage.getHeight();
        photo = scaleByHeight(photo, h);
        w = collage.getWidth() + photo.getWidth();

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(collage, 0, 0, null);
        canvas.drawBitmap(photo, collage.getWidth(), 0, null);

        collage = scaleByWidth(temp, MAX_WIDTH);
    }

    private int getNextSide(int side) {
        switch (side) {
            case LEFT:
                return TOP;
            case TOP:
                return RIGHT;
            case RIGHT:
                return BOTTOM;
            case BOTTOM:
                return LEFT;
            default:
                return RIGHT;
        }
    }

    private int getFirstSide(int count) {
        switch (count % 4) {
            case 0:
                return TOP;
            case 1:
                return LEFT;
            case 2:
                return BOTTOM;
            case 3:
                return RIGHT;
            default:
                return RIGHT;
        }
    }

    public void setOnCollageCreatedListener(OnCollageCreatedListener l) {
        listener = l;
    }
    public interface OnCollageCreatedListener {
        public void onCollageCreated(Bitmap bitmap);
    }
}
