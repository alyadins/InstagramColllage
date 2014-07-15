package ru.appkode.instagramcolllage.fragments;

import android.app.Fragment;
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
import android.widget.EditText;

import ru.appkode.instagramcolllage.Main;
import ru.appkode.instagramcolllage.R;
import ru.appkode.instagramcolllage.UserPhotoDownloader;
import ru.appkode.instagramcolllage.UserSearch;
import ru.appkode.instagramcolllage.gui.OkDialog;


public class MainFragment extends Fragment implements UserSearch.OnSearchCompleteListener, UserPhotoDownloader.OnPhotoDownloadListener {
    public static final int STATUS_OK = 1;
    public static final String TAG = "mainFragment";

    private EditText nickNameEditText;

    public UserSearch userSearch;
    public UserPhotoDownloader photoDownloader;

    private OnDownloadComplete listener;

    private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        userSearch = new UserSearch(getActivity());
        photoDownloader = new UserPhotoDownloader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, null);

        nickNameEditText = (EditText) v.findViewById(R.id.nick_edit);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.make_collage:
                if (isNetworkOnline()) {
                    String nickName = nickNameEditText.getText().toString();
                    userSearch.setNickName(nickName);
                    userSearch.setOnSearchCompleteListener(this);
                    getProgressDialog().setMessage(getString(R.string.user_search));
                    getProgressDialog().show();
                    userSearch.search();
                }
                else {
                    new OkDialog(getActivity(), getString(R.string.network_error));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchComplete(UserSearch request, String userId) {
        if ((userId.equals(UserSearch.USER_NOT_FOUND) || userId.equals(UserSearch.CANCEL))) {
            getProgressDialog().hide();
        } else {
            photoDownloader.setId(userId);
            photoDownloader.setOnPhotoDownloadListener(this);
            getProgressDialog().setMessage(getString(R.string.photo_download));
            getProgressDialog().show();

            photoDownloader.download();
        }
    }

    @Override
    public void onPhotoDownload(UserPhotoDownloader downloader, int status) {
        if (getProgressDialog().isShowing()) {
            getProgressDialog().hide();
        }

        if (listener != null) {
            listener.onDownloadComplete(this, STATUS_OK);
        }
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void setOnDownloadCompleteListener(OnDownloadComplete l) {
        this.listener = l;
    }

    public interface OnDownloadComplete {
        public void onDownloadComplete(MainFragment fragment, int status);
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.wait_message));
        }
        return progressDialog;
    }
}
