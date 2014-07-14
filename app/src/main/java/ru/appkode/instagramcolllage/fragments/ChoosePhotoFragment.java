package ru.appkode.instagramcolllage.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import ru.appkode.instagramcolllage.Main;
import ru.appkode.instagramcolllage.R;
import ru.appkode.instagramcolllage.UserPhotoDownloader;
import ru.appkode.instagramcolllage.UserSearch;
import ru.appkode.instagramcolllage.gui.PhotoGridViewAdapter;

/**
 * Created by lexer on 14.07.14.
 */
public class ChoosePhotoFragment extends Fragment {

    private GridView gridView;
    private PhotoGridViewAdapter gridViewAdapter;

    private UserPhotoDownloader photoDownloader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.photoDownloader = ((Main) getActivity()).photoDownloader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_choose_photo, null);

        gridView = (GridView) v.findViewById(R.id.gridView);
        gridViewAdapter = new PhotoGridViewAdapter(getActivity(), R.layout.photo_item, photoDownloader.bestPhoto);
        gridView.setAdapter(gridViewAdapter);

        return v;
    }
}
