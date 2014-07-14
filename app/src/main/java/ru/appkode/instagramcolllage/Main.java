package ru.appkode.instagramcolllage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ru.appkode.instagramcolllage.gui.OkDialog;


public class Main extends Activity implements UserSearch.OnSearchCompleteListener, UserPhotoDownloader.OnPhotoDownloadListener {

    public static final String APIURL = "https://api.instagram.com/v1";

    private String clientId;

    private EditText nickNameEditText;
    private ProgressDialog progressDialog;

    private UserSearch userSearch;
    private UserPhotoDownloader photoDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientId = getString(R.string.client_id);
        nickNameEditText = (EditText) findViewById(R.id.nick_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.make_collage:
                if (isNetworkOnline()) {
                    String nickName = nickNameEditText.getText().toString();
                    userSearch = new UserSearch(this, nickName);
                    userSearch.setOnSearchCompleteListener(this);
                    progressDialog = createProgressDialog();
                    progressDialog.show();
                    userSearch.search();
                }
                else {
                    new OkDialog(this, getString(R.string.network_error));
                }
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public void onSearchComplete(UserSearch request, String userId) {

        if (!userId.equals(UserSearch.USER_NOT_FOUND)) {
            photoDownloader = new UserPhotoDownloader(this, userId);
            photoDownloader.setOnPhotoDownloadListener(this);
            photoDownloader.download();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPhotoDownload(UserPhotoDownloader downloader, int status) {
    }


    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.wait_message));
        dialog.setCancelable(false);
        return dialog;
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}



