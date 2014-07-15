package ru.appkode.instagramcolllage;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.appkode.instagramcolllage.fragments.ChoosePhotoFragment;
import ru.appkode.instagramcolllage.fragments.CollageFragment;
import ru.appkode.instagramcolllage.fragments.MainFragment;

public class Main extends Activity implements MainFragment.OnDownloadComplete, ChoosePhotoFragment.OnPhotoSelectedListener, CollageFragment.OnPrintListener {

    public static final String APIURL = "https://api.instagram.com/v1";

    public UserSearch userSearch;
    public UserPhotoDownloader photoDownloader;

    private MainFragment mainFragment;

    private FragmentTransaction fragmentTransaction;

    public List<UserPhoto> theBestPhoto;

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
        collageFragment.setOnPrintListener(this);
        fragmentTransaction.replace(R.id.container, collageFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPrint(Bitmap bitmap) {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("collage_" + getFormattedDate(), bitmap);
    }

    private String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
        return format.format(Calendar.getInstance().getTime());
    }
}



