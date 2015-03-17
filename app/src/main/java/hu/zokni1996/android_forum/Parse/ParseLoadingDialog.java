package hu.zokni1996.android_forum.Parse;


import android.content.Context;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import hu.zokni1996.android_forum.R;

public class ParseLoadingDialog {
    Context context;
    MaterialDialog progressDialog;

    public ParseLoadingDialog(Context context) {
        this.context = context;
    }

    public void subscribe() {
        progressDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.ParseLoading))
                .content(context.getString(R.string.ParseNotificationPleaseWait))
                .cancelable(false)
                .progress(true, 0).show();
        ParsePush.subscribeInBackground("moderators", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(context, context.getString(R.string.SubscribeSuccess), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(context, context.getString(R.string.SubscribeFailed), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void unsubscribe() {
        progressDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.ParseLoading))
                .content(context.getString(R.string.ParseNotificationPleaseWait))
                .cancelable(false)
                .progress(true, 0).show();
        ParsePush.unsubscribeInBackground("moderators", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(context, context.getString(R.string.UnSubscribeSuccess), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(context, context.getString(R.string.UnSubscribeFailed), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
}
