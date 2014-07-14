package ru.appkode.instagramcolllage;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.List;

import ru.appkode.instagramcolllage.fragments.ChoosePhotoFragment;
import ru.appkode.instagramcolllage.fragments.CollageFragment;
import ru.appkode.instagramcolllage.fragments.MainFragment;
import ru.appkode.instagramcolllage.gui.OkDialog;


public class Main extends Activity implements MainFragment.OnDownloadComplete, ChoosePhotoFragment.OnPhotoSelectedListener {

    public static final String APIURL = "https://api.instagram.com/v1";
    public static final String CHOOSE_PHOTO_TAG = "CHOOSE_PHOTO_TAG";

    public UserSearch userSearch;
    public UserPhotoDownloader photoDownloader;

    private MainFragment mainFragment;

    private FragmentTransaction fragmentTransaction;

    private List<UserPhoto> theBestPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userSearch = new UserSearch(this);
        photoDownloader = new UserPhotoDownloader(this);

        mainFragment = new MainFragment();
        mainFragment.setOnDownloadCompleteListener(this);

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, mainFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDownloadComplete(MainFragment fragment, int status) {
        fragmentTransaction = getFragmentManager().beginTransaction();
        ChoosePhotoFragment choosePhotoFragment = new ChoosePhotoFragment();
        choosePhotoFragment.setOnPhotoSelectedListener(this);
        fragmentTransaction.replace(R.id.container, choosePhotoFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPhotoSelect(List<UserPhoto> theBestPhoto) {
        this.theBestPhoto = theBestPhoto;
        fragmentTransaction = getFragmentManager().beginTransaction();
        CollageFragment collageFragment = new CollageFragment();
        fragmentTransaction.replace(R.id.container, collageFragment);
        fragmentTransaction.commit();
    }
}



