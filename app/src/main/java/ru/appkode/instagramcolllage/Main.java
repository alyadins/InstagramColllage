package ru.appkode.instagramcolllage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.appkode.instagramcolllage.network.AsyncRequest;
import ru.appkode.instagramcolllage.network.ImageDownloader;


public class Main extends Activity implements AsyncRequest.OnRequestCompleteListener {

    public static final String APIURL = "https://api.instagram.com/v1";
    public static final String SEARCH_URL = "/users/search";
    private static final int SEARCH_REQUEST_ID = 1;
    private static final int USER_INFO_REQUEST_ID = 2;
    private static final int NUMBER_OF_BEST_PHOTO = 20;

    private String clientId;

    private EditText nickNameEditText;
    private ProgressDialog progressDialog;

    private List<User> users;
    private List<UserPhoto> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientId = getString(R.string.client_id);
        nickNameEditText = (EditText) findViewById(R.id.nick_edit);

        users = new ArrayList<User>();
        posts = new ArrayList<UserPhoto>();
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
                if (isNetworkOnline())
                    idRequest();
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.network_error);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }



    @Override
    public void onComplete(String response, int id) {
        switch (id) {
            case SEARCH_REQUEST_ID:
                users.clear();
                parseSearch(response);
                break;
            case USER_INFO_REQUEST_ID:
//                if (progressDialog != null) {
//                    progressDialog.dismiss();
//                }
                Log.d("TEST", response);
                parseUserInfo(response);
                break;
        }
    }

    /*
        Id request
     */
    private void idRequest() {
        String nickName = nickNameEditText.getText().toString();

        List<NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", nickName));
        params.add(new BasicNameValuePair("client_id", clientId));

        AsyncRequest request = new AsyncRequest(APIURL + SEARCH_URL, SEARCH_REQUEST_ID, this);

        progressDialog = createProgressDialog();
        progressDialog.show();
        request.execute(params);
    }


    private void parseSearch(String response) {
        try {
            JSONObject root = new JSONObject(response);
            int code = root.getJSONObject("meta").getInt("code");

            if (code != 200) {
                showErrorMessage();
                return;
            }

            final JSONArray usersJson = root.getJSONArray("data");

            if (usersJson.length() == 0) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.user_not_found);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }

            for (int i = 0; i < usersJson.length(); i++) {
                JSONObject user = usersJson.getJSONObject(i);
                final String userName = user.getString("username");
                final String id = user.getString("id");
                String imageUrl = user.getString("profile_picture").replace("\\", "");

                ImageDownloader downloader = new ImageDownloader();
                final int finalI = i;
                downloader.setOnImageDowloadCompleteListener(new ImageDownloader.OnImageDowloadCompeleteListener() {
                    @Override
                    public void onImageDownloadComplete(Bitmap bitmap) {
                        users.add(new User(id, userName, bitmap));
                        if (finalI == usersJson.length() - 1) {
                            onSearchParseComplete();
                        }
                    }
                });
                downloader.execute(imageUrl);
            }

        } catch (JSONException e) {
            showErrorMessage();
        }
    }

    private void onSearchParseComplete() {
        if (users.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.choose_user);

            ListView listView = new ListView(this);
            listView.setLayoutParams(
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            builder.setView(listView);
            final AlertDialog dialog = builder.create();
            ListDialogAdapter adapter = new ListDialogAdapter(this, R.layout.list_with_image_item, users);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    userInfoRequest(users.get(position).id);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } else {
            userInfoRequest(users.get(0).id);
        }
    }

    /*
        User info request
     */

    private void userInfoRequest(String id) {
        if (!progressDialog.isShowing()) {
            progressDialog = createProgressDialog();
            progressDialog.show();
        }

        List<NameValuePair> params= new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("count", "0"));

        StringBuilder builder = new StringBuilder();
        String url = builder.append(APIURL)
                .append("/users/")
                .append(id)
                .append("/media/recent/")
                .toString();
        AsyncRequest request = new AsyncRequest(url, USER_INFO_REQUEST_ID, this);
        posts.clear();

        request.execute(params);
    }

    private void parseUserInfo(String response) {
        int code = 0;
        try {
            JSONObject root = new JSONObject(response);
            code = root.getJSONObject("meta").getInt("code");
            if (code != 200) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                showErrorMessage();
                return;
            }

            final JSONArray data = root.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject post = data.getJSONObject(i);

                int likeNumber = post.getJSONObject("likes").getInt("count");
                String id = post.getString("id");
                String imageUrl = post.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

                posts.add(new UserPhoto(likeNumber, id, imageUrl));
            }

            Log.d("TEST", !root.getJSONObject("pagination").isNull("next_url") + "");
            if (!root.getJSONObject("pagination").isNull("next_url")) {
                String url = root.getJSONObject("pagination").getString("next_url").replace("\\", "");
                AsyncRequest request = new AsyncRequest(url, USER_INFO_REQUEST_ID, this);
                request.execute(new ArrayList<NameValuePair>());
            } else {
                Collections.sort(posts, comp);

                final List<UserPhoto> bestPhotos;
                if (posts.size() > NUMBER_OF_BEST_PHOTO)
                    bestPhotos = posts.subList(0, NUMBER_OF_BEST_PHOTO);
                else
                    bestPhotos = posts;

                for (int i = 0; i < bestPhotos.size(); i++) {
                    final UserPhoto bp = bestPhotos.get(i);
                    String url = bp.imageUrl;
                    ImageDownloader downloader = new ImageDownloader();
                    final int finalI = i;
                    downloader.setOnImageDowloadCompleteListener(new ImageDownloader.OnImageDowloadCompeleteListener() {
                        @Override
                        public void onImageDownloadComplete(Bitmap bitmap) {
                            bp.image = bitmap;
                            if (finalI == bestPhotos.size() - 1) {
                                onParseUserInfoComplete(bestPhotos);
                            }
                        }
                    });
                    downloader.execute(url);
                }
            }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    private void onParseUserInfoComplete(List<UserPhoto> photos) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ListView listView = new ListView(this);
        listView.setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        builder.setView(listView);
        AlertDialog dialog = builder.create();
        TestAdapter adapter = new TestAdapter(this, R.layout.list_with_image_item, photos);
        listView.setAdapter(adapter);
        dialog.show();
    }

    Comparator<UserPhoto> comp = new Comparator<UserPhoto>() {
        @Override
        public int compare(UserPhoto lhs, UserPhoto rhs) {
            return  rhs.likes - lhs.likes;
        }
    };

    private void showErrorMessage() {
        Log.d("TEST", "showing error message");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.erorr_title)
                .setMessage(R.string.error_text)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.wait_message));
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
                if (netInfo!=null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
class ListDialogAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private int resId;

    public ListDialogAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.users = users;
        this.resId = resource;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.userName = (TextView) rowView.findViewById(R.id.text);
            holder.image = (ImageView) rowView.findViewById(R.id.image);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        User user = users.get(position);
        holder.userName.setText(user.userName);
        holder.image.setImageBitmap(user.image);

        return rowView;
    }

    private class ViewHolder {
        ImageView image;
        TextView userName;
    }
}

class TestAdapter extends ArrayAdapter<UserPhoto> {

    private List<UserPhoto> posts;
    private int resId;

    public TestAdapter(Context context, int resource, List<UserPhoto> posts) {
        super(context, resource, posts);
        this.posts = posts;
        this.resId = resource;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.likes = (TextView) rowView.findViewById(R.id.text);
            holder.image = (ImageView) rowView.findViewById(R.id.image);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        UserPhoto post = posts.get(position);
        holder.likes.setText("Лайки " + post.likes);
        holder.image.setImageBitmap(post.image);

        return rowView;
    }

    private class ViewHolder {
        ImageView image;
        TextView likes;
    }
}


