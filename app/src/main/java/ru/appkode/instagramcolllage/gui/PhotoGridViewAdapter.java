package ru.appkode.instagramcolllage.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.appkode.instagramcolllage.R;
import ru.appkode.instagramcolllage.UserPhoto;

/**
 * Created by lexer on 14.07.14.
 */
public class PhotoGridViewAdapter extends ArrayAdapter<UserPhoto> {

    private int resId;
    private List<UserPhoto> photos;

    public PhotoGridViewAdapter(Context context, int resource, List<UserPhoto> photos) {
        super(context, resource, photos);
        this.resId = resource;
        this.photos = photos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.likes = (TextView) view.findViewById(R.id.likes);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        UserPhoto photo = photos.get(position);
        holder.image.setImageBitmap(photo.image);
        holder.likes.setText(String.valueOf(photo.likes));

        return view;
    }

    private class ViewHolder {
        public TextView likes;
        public ImageView image;
    }
}
