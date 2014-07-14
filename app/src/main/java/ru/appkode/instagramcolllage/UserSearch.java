package ru.appkode.instagramcolllage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.appkode.instagramcolllage.gui.OkDialog;
import ru.appkode.instagramcolllage.gui.UserListAdapter;
import ru.appkode.instagramcolllage.network.AsyncRequest;
import ru.appkode.instagramcolllage.network.ImageDownloader;

/**
 * Created by lexer on 14.07.14.
 */
public class UserSearch implements AsyncRequest.OnRequestCompleteListener {
    private static final int SEARCH_REQUEST_ID = 1;
    public static final String USER_NOT_FOUND = "-1";

    private String nickName;
    private Context context;

    private String clientId;

    public List<User> users;

    public OnSearchCompleteListener listener;

    public UserSearch(Context context, String nickName) {
        this(context);
        this.nickName = nickName;
    }

    public UserSearch(Context context) {
        this.context = context;

        clientId = context.getString(R.string.client_id);
        users = new ArrayList<User>();
    }

    public void search() {
        users.clear();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", nickName));
        params.add(new BasicNameValuePair("client_id", clientId));

        AsyncRequest request = new AsyncRequest(Main.APIURL + "/users/search", SEARCH_REQUEST_ID, this);

        request.execute(params);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void onComplete(String response, int id) {
        parseResponse(response);
    }

    private void parseResponse(String response) {
        try {
            JSONObject root = new JSONObject(response);
            int code = root.getJSONObject("meta").getInt("code");

            if (code != 200) {
                showErrorMessage();
                return;
            }

            final JSONArray usersJson = root.getJSONArray("data");

            if (usersJson.length() == 0) {
                new OkDialog(context, context.getString(R.string.user_not_found)).show();
                if (listener != null) {
                    listener.onSearchComplete(this, USER_NOT_FOUND);
                }
            }

            for (int i = 0; i < usersJson.length(); i++) {
                JSONObject user = usersJson.getJSONObject(i);
                final String userName = user.getString("username");
                final String id = user.getString("id");
                String imageUrl = user.getString("profile_picture").replace("\\", "");

                ImageDownloader downloader = new ImageDownloader();
                final int finalI = i;
                downloader.setOnImageDowloadCompleteListener(new ImageDownloader.OnImageDownloadCompleteListener() {

                    @Override
                    public void onImageDownloadComplete(Bitmap bitmap) {
                        users.add(new User(id, userName, bitmap));
                        if (finalI == usersJson.length() - 1) {
                            downloadComplete();
                        }
                    }
                });
                downloader.execute(imageUrl);
            }

        } catch (JSONException e) {
            showErrorMessage();
        }
    }

    private void downloadComplete() {
        if (users.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.choose_user);

            ListView listView = new ListView(context);
            listView.setLayoutParams(
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            builder.setView(listView);
            final AlertDialog dialog = builder.create();
            UserListAdapter adapter = new UserListAdapter(context, R.layout.list_with_image_item, users);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    finishSearch(users.get(position).id);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } else {
            finishSearch(users.get(0).id);
        }
    }

    public void finishSearch(String id) {
        if (listener != null) {
            listener.onSearchComplete(this, id);
        }
    }

    private void showErrorMessage() {
        new OkDialog(context, context.getString(R.string.error_text), context.getString(R.string.erorr_title)).show();
    }

    public void setOnSearchCompleteListener(OnSearchCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnSearchCompleteListener {
        public void onSearchComplete(UserSearch request, String userId);
    }
}
