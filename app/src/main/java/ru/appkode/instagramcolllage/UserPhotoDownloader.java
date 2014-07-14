package ru.appkode.instagramcolllage;

import android.content.Context;
import android.graphics.Bitmap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.appkode.instagramcolllage.gui.OkDialog;
import ru.appkode.instagramcolllage.network.AsyncRequest;
import ru.appkode.instagramcolllage.network.ImageDownloader;

/**
 * Created by lexer on 14.07.14.
 */
public class UserPhotoDownloader implements AsyncRequest.OnRequestCompleteListener {

    private static final int USER_INFO_REQUEST_ID = 2;
    private static final int NUMBER_OF_BEST_PHOTO = 20;

    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;

    private String clientId;

    private String id;
    private Context context;

    public List<UserPhoto> bestPhoto;
    private List<UserPhoto> photo;

    private OnPhotoDownloadListener listener;

    public UserPhotoDownloader(Context context, String id) {
        this.id = id;
        this.context = context;

        clientId = context.getString(R.string.client_id);

        bestPhoto = new ArrayList<UserPhoto>();
        photo = new ArrayList<UserPhoto>();
    }

    public void download() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", clientId));

        StringBuilder builder = new StringBuilder();
        String url = builder.append(Main.APIURL)
                .append("/users/")
                .append(id)
                .append("/media/recent/")
                .toString();
        AsyncRequest request = new AsyncRequest(url, USER_INFO_REQUEST_ID, this);

        request.execute(params);
    }

    @Override
    public void onComplete(String response, int id) {
        parseResponse(response);
    }

    private void parseResponse(String response) {
        int code = 0;
        try {
            JSONObject root = new JSONObject(response);
            code = root.getJSONObject("meta").getInt("code");
            if (code != 200) {
                showErrorMessage();
                finish(STATUS_ERROR);
            }

            final JSONArray data = root.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject post = data.getJSONObject(i);

                int likeNumber = post.getJSONObject("likes").getInt("count");
                String id = post.getString("id");
                String imageUrl = post.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

                photo.add(new UserPhoto(likeNumber, id, imageUrl));
            }

            if (!root.getJSONObject("pagination").isNull("next_url")) {
                String url = root.getJSONObject("pagination").getString("next_url").replace("\\", "");
                AsyncRequest request = new AsyncRequest(url, USER_INFO_REQUEST_ID, this);
                request.execute(new ArrayList<NameValuePair>());
            } else {
               downloadPhotos();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadPhotos() {
        Collections.sort(photo, comp);

        if (photo.size() > NUMBER_OF_BEST_PHOTO)
            bestPhoto = photo.subList(0, NUMBER_OF_BEST_PHOTO);
        else
            bestPhoto = photo;

        for (int i = 0; i < bestPhoto.size(); i++) {
            final UserPhoto bp = bestPhoto.get(i);
            String url = bp.imageUrl;
            ImageDownloader downloader = new ImageDownloader();
            final int finalI = i;
            downloader.setOnImageDowloadCompleteListener(new ImageDownloader.OnImageDownloadCompleteListener() {
                @Override
                public void onImageDownloadComplete(Bitmap bitmap) {
                    bp.image = bitmap;
                    if (finalI == bestPhoto.size() - 1) {
                        finish(STATUS_OK);
                    }
                }
            });
            downloader.execute(url);
        }
    }

    private void finish(int status) {
        if (listener != null) {
            listener.onPhotoDownload(this, status);
        }
    }

    Comparator<UserPhoto> comp = new Comparator<UserPhoto>() {
        @Override
        public int compare(UserPhoto lhs, UserPhoto rhs) {
            return rhs.likes - lhs.likes;
        }
    };


    private void showErrorMessage() {
        new OkDialog(context, context.getString(R.string.error_text), context.getString(R.string.erorr_title)).show();
    }

    public void setOnPhotoDownloadListener(OnPhotoDownloadListener l) {
        this.listener = l;
    }

    public interface OnPhotoDownloadListener {
        public void onPhotoDownload(UserPhotoDownloader downloader, int status);
    }
}
