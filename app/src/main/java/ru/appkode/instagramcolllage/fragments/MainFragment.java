package ru.appkode.instagramcolllage.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Created by lexer on 14.07.14.
 */
public class MainFragment extends Fragment implements UserSearch.OnSearchCompleteListener, UserPhotoDownloader.OnPhotoDownloadListener {
    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;

    private EditText nickNameEditText;

    private UserSearch userSearch;
    private UserPhotoDownloader photoDownloader;
    private ProgressDialog progressDialog;

    private OnDownloadComplete listener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.userSearch = ((Main)getActivity()).userSearch;
        this.photoDownloader = ((Main) getActivity()).photoDownloader;
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
                    progressDialog = createProgressDialog();
                    progressDialog.show();
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
        if (!userId.equals(UserSearch.USER_NOT_FOUND)) {
            photoDownloader.setId(userId);
            photoDownloader.setOnPhotoDownloadListener(this);
            photoDownloader.download();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPhotoDownload(UserPhotoDownloader downloader, int status) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
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

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.wait_message));
        dialog.setCancelable(false);
        return dialog;
    }

    public void setOnDownloadCompleteListener(OnDownloadComplete l) {
        this.listener = l;
    }

    public interface OnDownloadComplete {
        public void onDownloadComplete(MainFragment fragment, int status);
    }
}
