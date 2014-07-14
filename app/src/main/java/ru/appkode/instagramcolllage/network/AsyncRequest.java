package ru.appkode.instagramcolllage.network;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class AsyncRequest extends AsyncTask<List<NameValuePair>, Integer, String> {

    private String url;
    private int id;
    private DefaultHttpClient httpClient;
    private StringBuffer sb;

    private OnRequestCompleteListener listener;

    public AsyncRequest(String url, int id) {
        this.url = url;
        this.id = id;
        httpClient = CustomHttpClient.getHttpClient();
    }

    public AsyncRequest(String url, int id, OnRequestCompleteListener listener) {
        this(url, id);
        this.listener = listener;
    }

    @Override
    protected String doInBackground(List<NameValuePair>... lists) {

        sb =  new StringBuffer();
        BufferedReader in = null;

        try {
            HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);

            HttpResponse response;
            HttpGet getRequest;
            if (lists[0].size() == 0) {
                getRequest = new HttpGet(url);
            } else {
                getRequest = new HttpGet(addParamsToUrl(url, lists[0]));
            }
            response = httpClient.execute(getRequest);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();

        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (listener != null) {
            listener.onComplete(s, id);
        }
    }


    private String addParamsToUrl(String url, List<NameValuePair> params) {
        if (!url.endsWith("?")) {
            url += "?";
        }

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;

        return url;
    }

    public void setOnRequestCompleteListener(OnRequestCompleteListener l) {
        listener = l;
    }

    public interface OnRequestCompleteListener {
        public void onComplete(String response, int id);
    }
}
