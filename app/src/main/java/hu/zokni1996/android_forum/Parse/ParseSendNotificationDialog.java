package hu.zokni1996.android_forum.Parse;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SendCallback;

import hu.zokni1996.android_forum.R;

public class ParseSendNotificationDialog {
    Context context;
    View sendNot;
    EditText parseNotificationMessage;
    RadioGroup radioGroup;
    MaterialDialog progressDialog;
    String username;

    public ParseSendNotificationDialog(final String username, final Context context) {
        this.context = context;
        this.username = username;
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.parse_notification, true)
                .positiveText(context.getString(R.string.SendNotification))
                .negativeText(context.getString(R.string.cancel))
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        if (ParseUser.getCurrentUser() != null) {
                            progressDialog = new MaterialDialog.Builder(context)
                                    .title(context.getString(R.string.ParseNotificationSending))
                                    .content(context.getString(R.string.ParseNotificationPleaseWait))
                                    .cancelable(false)
                                    .progress(true, 0).show();
                            ParsePush parsePush = new ParsePush();
                            parsePush.setMessage(username + ": " + parseNotificationMessage.getText().toString());
                            if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_moderators)
                                parsePush.setChannel("moderators");
                            else parsePush.setChannel("everybody");
                            parsePush.sendInBackground(new SendCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, context.getString(R.string.ParseNotificationSentSucces), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.ParseNotificationSentFailed), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else
                            Toast.makeText(context, context.getString(R.string.ParseNotificationSentFailed), Toast.LENGTH_SHORT).show();
                    }
                })
                .title(context.getString(R.string.SendNotificationTitle))
                .build();
        sendNot = dialog.getActionButton(DialogAction.POSITIVE);
        parseNotificationMessage = (EditText) dialog.getCustomView().findViewById(R.id.ParseNotificationMessage);
        radioGroup = (RadioGroup) dialog.getCustomView().findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radioButton_everybody);
        parseNotificationMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendNot.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dialog.show();
        sendNot.setEnabled(false);
    }
}
