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
import ru.appkode.instagramcolllage.User;

/**
 * Created by lexer on 14.07.14.
 */
public class UserListAdapter extends ArrayAdapter {
    private List<User> users;
    private int resId;

    public UserListAdapter(Context context, int resource, List<User> users) {
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

