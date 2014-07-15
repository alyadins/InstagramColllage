package ru.appkode.instagramcolllage.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.appkode.instagramcolllage.CollageCreator;
import ru.appkode.instagramcolllage.Main;
import ru.appkode.instagramcolllage.R;
import ru.appkode.instagramcolllage.UserPhoto;

public class CollageFragment extends Fragment implements CollageCreator.OnCollageCreatedListener {



    private ImageView collageImage;

    private List<UserPhoto> bestPhotos;

    private OnPrintListener listener;

    private ProgressDialog progressDialog;

    private Bitmap collage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bestPhotos = ((Main) getActivity()).theBestPhoto;
        progressDialog = ((Main) getActivity()).progressDialog;
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
        progressDialog.setMessage(getString(R.string.creating_collage));
        progressDialog.show();

        CollageCreator collageCreator = new CollageCreator(this);
        collageCreator.execute(bestPhotos);
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



    public void setOnPrintListener(OnPrintListener l) {
        this.listener = l;
    }

    @Override
    public void onCollageCreated(Bitmap bitmap) {
        progressDialog.hide();
        collageImage.setImageBitmap(bitmap);
        this.collage = bitmap;
    }

    public interface OnPrintListener {
        public void onPrint(Bitmap bitmap);
    }
}
