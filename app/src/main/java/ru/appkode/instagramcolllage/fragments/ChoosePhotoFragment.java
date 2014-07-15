package ru.appkode.instagramcolllage.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.appkode.instagramcolllage.Main;
import ru.appkode.instagramcolllage.R;
import ru.appkode.instagramcolllage.UserPhoto;
import ru.appkode.instagramcolllage.gui.PhotoGridViewAdapter;

public class ChoosePhotoFragment extends Fragment implements AbsListView.MultiChoiceModeListener {

    private GridView gridView;
    private PhotoGridViewAdapter gridViewAdapter;

    private List<UserPhoto> bestPhoto;

    private List<UserPhoto> theBestOfTheBestPhoto;

    private OnPhotoSelectedListener listener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.bestPhoto = ((Main) getActivity()).photoDownloader.bestPhoto;

        Toast.makeText(getActivity(), R.string.long_press_tip, Toast.LENGTH_SHORT).show();

        theBestOfTheBestPhoto = new ArrayList<UserPhoto>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_choose_photo, null);

        gridView = (GridView) v.findViewById(R.id.gridView);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridViewAdapter = new PhotoGridViewAdapter(getActivity(), R.layout.photo_item, bestPhoto);
        gridView.setAdapter(gridViewAdapter);
        gridView.setMultiChoiceModeListener(this);

        return v;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        mode.setSubtitle(gridView.getCheckedItemCount() + " " + getActivity().getString(R.string.selected_photo));

        if (checked) {
            theBestOfTheBestPhoto.add(bestPhoto.get(position));
        } else {
            theBestOfTheBestPhoto.remove(bestPhoto.get(position));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.setTitle(R.string.choose_photo);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (!(theBestOfTheBestPhoto.size() == 0)) {
            if (listener != null) {
                listener.onPhotoSelect(theBestOfTheBestPhoto);
            }
        }
    }

    public void setOnPhotoSelectedListener(OnPhotoSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnPhotoSelectedListener {
        public void onPhotoSelect(List<UserPhoto> theBestPhoto);
    }
}
