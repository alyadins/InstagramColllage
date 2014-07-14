package ru.appkode.instagramcolllage.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import ru.appkode.instagramcolllage.R;

/**
 * Created by lexer on 14.07.14.
 */
public class OkDialog {
    private String message = null;
    private String title = null;
    private Context context;

    private AlertDialog dialog;

    public OkDialog(Context context, String message, String title) {
        this.message = message;
        this.title = title;
        this.context = context;
        create();
    }

    public OkDialog(Context context,String message) {
        this(context, message, null);
    }

    private void create() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }

        if (message == null) {
            throw new NullPointerException("Message can't be null");
        }

        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
