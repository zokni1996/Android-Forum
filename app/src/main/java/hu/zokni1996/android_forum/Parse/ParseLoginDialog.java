package hu.zokni1996.android_forum.Parse;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import hu.zokni1996.android_forum.R;

public class ParseLoginDialog {
    EditText usernameEdit;
    EditText passwordEdit;
    View loginButton;
    Context context;

    public interface OnTaskCompleted {
        void onTaskCompleted(ParseUser parseUser);
    }

    public ParseLoginDialog(final Context context) {
        this.context = context;
    }

    public void LoginTry(final OnTaskCompleted listener) {
        final String password = context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE).getString("Password", "");
        final String username = context.getSharedPreferences("USERNAME", Context.MODE_PRIVATE).getString("Username", "");
        if (!username.equals("") && !password.equals("")) {
            final MaterialDialog progressDialog = new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.ParseLogIn))
                    .content(context.getString(R.string.ParseNotificationPleaseWait))
                    .cancelable(false)
                    .progress(true, 0).show();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE)
                                .edit()
                                .putString("Password", password).commit();
                        context.getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
                                .edit()
                                .putString("Username", username).commit();
                        progressDialog.dismiss();
                        listener.onTaskCompleted(ParseUser.getCurrentUser());
                    }
                    if (user == null) {
                        Toast.makeText(context,
                                context.getString(R.string.ParseFailedLogin),
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }
        listener.onTaskCompleted(ParseUser.getCurrentUser());
    }

    public void LoginNew(final OnTaskCompleted listener) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.parse_login, true)
                .title(context.getString(R.string.ParseLogIn))
                .cancelable(true)
                .negativeText(context.getString(R.string.cancel))
                .positiveText(context.getString(R.string.ParseLogIn))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        final String username = usernameEdit.getText().toString();
                        final String password = passwordEdit.getText().toString();
                        final MaterialDialog progressDialog = new MaterialDialog.Builder(context)
                                .title(context.getString(R.string.ParseLogIn))
                                .content(context.getString(R.string.ParseNotificationPleaseWait))
                                .cancelable(false)
                                .progress(true, 0).show();
                        ParseUser.logInInBackground(
                                username, password,
                                new LogInCallback() {
                                    public void done(ParseUser user, ParseException e) {
                                        if (user != null) {
                                            context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("Password", password).commit();
                                            context.getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("Username", username).commit();
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            listener.onTaskCompleted(ParseUser.getCurrentUser());
                                        } else {
                                            Toast.makeText(context,
                                                    context.getString(R.string.ParseFailedLogin),
                                                    Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
                })
                .build();
        loginButton = dialog.getActionButton(DialogAction.POSITIVE);
        usernameEdit = (EditText) dialog.getCustomView().findViewById(R.id.ParseUsername);
        passwordEdit = (EditText) dialog.getCustomView().findViewById(R.id.ParsePassword);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setEnabled(passwordEdit.getText().toString().length() > 0 &&
                        usernameEdit.getText().toString().length() > 0);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        usernameEdit.addTextChangedListener(textWatcher);
        passwordEdit.addTextChangedListener(textWatcher);
        dialog.show();
        loginButton.setEnabled(false);
    }

}
