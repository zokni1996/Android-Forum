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
    Context context;
    EditText usernameEdit;
    EditText passwordEdit;
    View loginButton;
    MaterialDialog progressDialog;
    String username;
    String password;

    public String getUsername() {
        return username;
    }


    public ParseLoginDialog(final Context context) {
        this.context = context;
    }


    public void tryToLogIn() {
        password = context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE).getString("Password", "");
        username = context.getSharedPreferences("USERNAME", Context.MODE_PRIVATE).getString("Username", "");
        if (!username.equals("") && !password.equals("")) {
            progressDialog = new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.ParseLogIn))
                    .content(context.getString(R.string.ParseNotificationPleaseWait))
                    .cancelable(false)
                    .progress(true, 0).show();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        progressDialog.dismiss();
                        context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE)
                                .edit()
                                .putString("Password", password).commit();
                        context.getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
                                .edit()
                                .putString("Username", username).commit();
                    }
                    if (user == null){
                        progressDialog.dismiss();
                        Toast.makeText(context,
                                context.getString(R.string.ParseFailedLogin),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void login() {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.parse_login, true)
                .title(context.getString(R.string.ParseLogIn))
                .cancelable(true)
                .negativeText(context.getString(R.string.cancel))
                .positiveText(context.getString(R.string.ParseLogIn))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        username = usernameEdit.getText().toString();
                        password = passwordEdit.getText().toString();
                        progressDialog = new MaterialDialog.Builder(context)
                                .title(context.getString(R.string.ParseLogIn))
                                .content(context.getString(R.string.ParseNotificationPleaseWait))
                                .cancelable(false)
                                .progress(true, 0).show();
                        ParseUser.logInInBackground(
                                username, password,
                                new LogInCallback() {
                                    public void done(ParseUser user, ParseException e) {
                                        if (user != null) {
                                            progressDialog.dismiss();
                                            context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("Password", password).commit();
                                            context.getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("Username", username).commit();
                                            dialog.dismiss();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(context,
                                                    context.getString(R.string.ParseFailedLogin),
                                                    Toast.LENGTH_SHORT).show();
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
