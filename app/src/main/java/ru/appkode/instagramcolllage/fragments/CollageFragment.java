package ru.appkode.instagramcolllage.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.appkode.instagramcolllage.R;

/**
 * Created by lexer on 14.07.14.
 */
public class CollageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collage_fragment, null);
    }
}
