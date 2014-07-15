package ru.appkode.instagramcolllage;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.print.PrintHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.appkode.instagramcolllage.fragments.ChoosePhotoFragment;
import ru.appkode.instagramcolllage.fragments.CollageFragment;
import ru.appkode.instagramcolllage.fragments.MainFragment;

public class Main extends FragmentActivity implements MainFragment.OnDownloadComplete, ChoosePhotoFragment.OnPhotoSelectedListener, CollageFragment.OnPrintListener {

    public static final String APIURL = "https://api.instagram.com/v1";

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getFragmentManager();

        MainFragment mainFragment;
        mainFragment = (MainFragment) manager.findFragmentByTag(MainFragment.TAG);

        if (mainFragment == null) {
            mainFragment = new MainFragment();
            mainFragment.setOnDownloadCompleteListener(this);
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
            fragmentTransaction.add(R.id.container, mainFragment, mainFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onDownloadComplete(MainFragment fragment, int status) {

        FragmentManager manager = getFragmentManager();

        ChoosePhotoFragment choosePhotoFragment;
        choosePhotoFragment = (ChoosePhotoFragment) manager.findFragmentByTag(ChoosePhotoFragment.TAG);

        if (choosePhotoFragment == null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            choosePhotoFragment = new ChoosePhotoFragment();
            choosePhotoFragment.setOnPhotoSelectedListener(this);
            choosePhotoFragment.setBestPhoto(fragment.photoDownloader.bestPhoto);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
            fragmentTransaction.replace(R.id.container, choosePhotoFragment, ChoosePhotoFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onPhotoSelect(List<UserPhoto> theBestPhoto) {

        FragmentManager manager = getFragmentManager();

        CollageFragment collageFragment;
        collageFragment = (CollageFragment) manager.findFragmentByTag(CollageFragment.TAG);

        if (collageFragment == null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            collageFragment = new CollageFragment();
            collageFragment.setBestPhotos(theBestPhoto);
            collageFragment.setOnPrintListener(this);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
            fragmentTransaction.replace(R.id.container, collageFragment, CollageFragment.TAG);
            fragmentTransaction.commit();
        }
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

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}




