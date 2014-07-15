package ru.appkode.instagramcolllage.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.appkode.instagramcolllage.Main;
import ru.appkode.instagramcolllage.R;
import ru.appkode.instagramcolllage.UserPhoto;

public class CollageFragment extends Fragment {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int BOTTOM = 3;

    private static final int MAX_WIDTH = 2048;
    private static final int MAX_HEIGHT = 2048;

    private ImageView collageImage;

    private List<UserPhoto> bestPhotos;

    private int side = RIGHT;

    private Bitmap collage;

    private OnPrintListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bestPhotos = ((Main) getActivity()).theBestPhoto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.collage_fragment, null);

        collageImage = (ImageView) v.findViewById(R.id.collage_image);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        createCollage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.create_collage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print:
                if (listener != null && collage != null) {
                    listener.onPrint(collage);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createCollage() {

        collage = null;
        side = getFirstSide(bestPhotos.size());
        for (UserPhoto photo : bestPhotos) {
            addPhoto(photo.image);
        }

        collageImage.setImageBitmap(collage);
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

    public void setOnPrintListener(OnPrintListener l) {
        this.listener = l;
    }

    public interface OnPrintListener {
        public void onPrint(Bitmap bitmap);
    }
}
