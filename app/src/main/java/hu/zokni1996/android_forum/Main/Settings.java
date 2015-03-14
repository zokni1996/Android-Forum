package hu.zokni1996.android_forum.Main;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import hu.zokni1996.android_forum.R;

public class Settings extends PreferenceActivity {

    static String changeLogHun = "";
    static String changeLogEng = "";
    static ProgressDialog progressDialog;
    static ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setTheme(android.R.style.Theme_Material_Light);
        else setTheme(R.style.StyleThemeSettings);

        actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setIcon(R.drawable.ic_action_action_settings_holo_light);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.activity_settings, target);
        for (Header header : target) {
            if (header.titleRes == R.string.AboutMe)
                header.iconRes = R.drawable.ic_action_action_about_holo_light;
            if (header.titleRes == R.string.ScreenSettings)
                header.iconRes = R.drawable.ic_action_hardware_phone_holo_light;
            if (header.titleRes == R.string.NotificationSettings)
                header.iconRes = R.drawable.ic_action_action_announcement_holo_light;
            if (header.titleRes == R.string.Extras)
                header.iconRes = R.drawable.ic_action_social_plus_one_holo_light;
            if (header.titleRes == R.string.NameRules)
                header.iconRes = R.drawable.ic_action_alerts_and_states_error_holo_light;
            if (header.titleRes == R.string.ParseSettingsNotificationName)
                header.iconRes = R.drawable.ic_action_action_lock_holo_light;
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }


    public static class FragmentSettingsExtra extends PreferenceFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_social_plus_one_holo_light);
            addPreferencesFromResource(R.xml.settings_extras);

            VideoOnPreferenceClick("KeyFirstOne", "f73ZjSQU1Jo");
            VideoOnPreferenceClick("KeyFirstTwo", "c8m_JF-bXm4");
            VideoOnPreferenceClick("KeySecondOne", "qgyndokygf4");
            VideoOnPreferenceClick("KeySecondTwo", "6sxFE_SheGU");
            VideoOnPreferenceClick("KeyThirdOne", "c7znB9XjNTM");
            VideoOnPreferenceClick("KeyThirdTwo", "dBFDvPdFz2o");
            VideoOnPreferenceClick("KeyFourthOne", "qIqsC5oDasY");
            VideoOnPreferenceClick("KeyFourthTwo", "C6gZ49aJSl0");
            VideoOnPreferenceClick("KeyFifthOne", "92fdPDEp9TQ");
            VideoOnPreferenceClick("KeyFifthTwo", "Xm_94xQWSiY");
            VideoOnPreferenceClick("KeySixthOne", "3brEp170_HI");
            VideoOnPreferenceClick("KeySixthTwo", "8ttrNqtXfBI");
            VideoOnPreferenceClick("KeySeventhOne", "gJdlq2YBGRQ");
            VideoOnPreferenceClick("KeySeventhOne", "QmDPsXZs1lc");
            VideoChannelClick("KeyChannelAndroidUpdate", "https://www.youtube.com/playlist?list=PL8ZaIlUpLnZ93Nd_CRnndm-qoLTkhPBqI");

            findPreference("Facebook").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/167587200031522"));
                        startActivity(intent);
                    } catch (Exception e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/AndroidForumhu?fref=ts"));
                        startActivity(intent);
                    }
                    return false;
                }
            });

            findPreference("GooglePlus").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+Android-forumHu/posts")));
                    return false;
                }
            });

        }

        public void VideoChannelClick(String PreferenceKey, final String ChannelID) {
            findPreference(PreferenceKey).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(ChannelID));
                    startActivity(intent);
                    return false;
                }
            });
        }

        public void VideoOnPreferenceClick(String PreferenceKey, final String VideoID) {
            findPreference(PreferenceKey).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + VideoID));
                    intent.putExtra(VideoID, VideoID);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }

    public static class FragmentSettingsAbout extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_action_about_holo_light);

            addPreferencesFromResource(R.xml.settings_about);


            PackageInfo info = null;
            try {
                info = getActivity().getPackageManager().getPackageInfo(
                        getActivity().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String version;
            if (info != null) {
                version = info.versionName;
            } else version = getString(R.string.application_version);
            findPreference("BuildVersion").setSummary(version);
            findPreference("ChangeLog").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Context context = getActivity();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.ChangeLog))
                            .setNeutralButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    WebView webView = new WebView(context);

                    if (Locale.getDefault().getDisplayLanguage().equals("magyar")) {
                        try {
                            InputStream input;
                            AssetManager assetManager = context.getAssets();
                            input = assetManager.open("changeLogHun.txt");
                            BufferedReader f = new BufferedReader(new InputStreamReader(input));
                            for (String sor = f.readLine(); sor != null; sor = f.readLine()) {
                                changeLogHun += sor;
                            }
                            input.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        webView.loadDataWithBaseURL(null, changeLogHun, "text/html; charset=utf-8", "UTF-8",
                                null);
                    } else {
                        try {
                            InputStream input;
                            AssetManager assetManager = context.getAssets();
                            input = assetManager.open("changeLogEng.txt");
                            BufferedReader f = new BufferedReader(new InputStreamReader(input));
                            for (String sor = f.readLine(); sor != null; sor = f.readLine()) {
                                changeLogEng += sor;
                            }
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        webView.loadDataWithBaseURL(null, changeLogEng, "text/html; charset=utf-8", "UTF-8",
                                null);
                    }
                    builder.setView(webView);
                    builder.show();
                    return false;
                }
            });
        }
    }

    public static class FragmentSettingsScreen extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_hardware_phone_holo_light);

            addPreferencesFromResource(R.xml.settings_screen);
        }
    }

    public static class FragmentSettingsNotification extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        String password = "";
        String username = "";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_action_announcement_holo_light);

            addPreferencesFromResource(R.xml.settings_notification);

            findPreference("NotificationParseMaintenance").setEnabled(false);
            password = getActivity().getSharedPreferences("PASSWORD", MODE_PRIVATE).getString("Password", "");
            username = getActivity().getSharedPreferences("USERNAME", MODE_PRIVATE).getString("Username", "");
            if (!username.equals("") && !password.equals("")) {
                progressDialog = ProgressDialog.show(getActivity(),
                        getActivity().getString(R.string.ParseLoading),
                        getActivity().getString(R.string.ParseNotificationPleaseWait), true);
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            findPreference("NotificationParseMaintenance").setEnabled(true);
                        } else {
                            findPreference("NotificationParseMaintenance").setEnabled(false);
                        }
                    }
                });
                progressDialog.dismiss();
            }
            if (ParseUser.getCurrentUser() != null) {
                findPreference("NotificationParseMaintenance").setOnPreferenceChangeListener(this);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            final Context context = getActivity().getApplicationContext();
            if ("NotificationParseMaintenance".equals(preference.getKey())) {
                if ((Boolean) newValue) {
                    progressDialog = ProgressDialog.show(getActivity(),
                            getActivity().getString(R.string.ParseLoading),
                            getActivity().getString(R.string.ParseNotificationPleaseWait), true);
                    ParsePush.subscribeInBackground("moderators", new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), context.getString(R.string.SubscribeSuccess), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), context.getString(R.string.SubscribeFailed), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    progressDialog = ProgressDialog.show(getActivity(),
                            getActivity().getString(R.string.ParseLoading),
                            getActivity().getString(R.string.ParseNotificationPleaseWait), true);
                    ParsePush.unsubscribeInBackground("moderators", new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), context.getString(R.string.UnSubscribeSuccess), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), context.getString(R.string.UnSubscribeFailed), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                return true;
            }
            return false;
        }
    }

    public static class FragmentSettingsRules extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_alerts_and_states_error_holo_light);

            addPreferencesFromResource(R.xml.settings_rules);
        }
    }

    public static class FragmentSettingsDeveloper extends PreferenceFragment {
        boolean loggedIn = false;
        String password = "";
        String username = "";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_action_lock_holo_light);
            addPreferencesFromResource(R.xml.settings_developer);

            tryToLogIn();

            if (!loggedIn) {
                findPreference("ParseLogin").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final Context context = getActivity();
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.parse_login);
                        dialog.setTitle(context.getString(R.string.ParseLogIn));
                        final Button loginButton = (Button) dialog.findViewById(R.id.ParseLoginButton);
                        final EditText usernameEdit = (EditText) dialog.findViewById(R.id.ParseUsername);
                        final EditText passwordEdit = (EditText) dialog.findViewById(R.id.ParsePassword);
                        loginButton.setEnabled(false);
                        TextWatcher textWatcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                if (passwordEdit.getText().toString().length() > 0 &&
                                        usernameEdit.getText().toString().length() > 0)
                                    loginButton.setEnabled(true);
                                else loginButton.setEnabled(false);
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (passwordEdit.getText().toString().length() > 0 &&
                                        usernameEdit.getText().toString().length() > 0)
                                    loginButton.setEnabled(true);
                                else loginButton.setEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (passwordEdit.getText().toString().length() > 0 &&
                                        usernameEdit.getText().toString().length() > 0)
                                    loginButton.setEnabled(true);
                                else loginButton.setEnabled(false);
                            }
                        };
                        usernameEdit.addTextChangedListener(textWatcher);
                        passwordEdit.addTextChangedListener(textWatcher);

                        loginButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                username = usernameEdit.getText().toString();
                                password = passwordEdit.getText().toString();
                                progressDialog = ProgressDialog.show(context,
                                        context.getString(R.string.ParseLogIn),
                                        context.getString(R.string.ParseNotificationPleaseWait), true);
                                ParseUser.logInInBackground(
                                        username, password,
                                        new LogInCallback() {
                                            public void done(ParseUser user, ParseException e) {
                                                if (user != null) {
                                                    progressDialog.dismiss();
                                                    context.getSharedPreferences("PASSWORD", MODE_PRIVATE)
                                                            .edit()
                                                            .putString("Password", password).commit();
                                                    context.getSharedPreferences("USERNAME", MODE_PRIVATE)
                                                            .edit()
                                                            .putString("Username", username).commit();
                                                    dialog.dismiss();
                                                    SuccessLoggedIn();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(),
                                                            context.getString(R.string.ParseFailedLogin),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        });
                        dialog.show();
                        return false;
                    }
                });
            }
        }

        public void tryToLogIn() {
            final Context context = getActivity();
            password = context.getSharedPreferences("PASSWORD", MODE_PRIVATE).getString("Password", "");
            username = context.getSharedPreferences("USERNAME", MODE_PRIVATE).getString("Username", "");
            if (!username.equals("") && !password.equals("")) {

                progressDialog = ProgressDialog.show(context,
                        context.getString(R.string.ParseLogIn),
                        context.getString(R.string.ParseNotificationPleaseWait), true);
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            progressDialog.dismiss();
                            context.getSharedPreferences("PASSWORD", MODE_PRIVATE)
                                    .edit()
                                    .putString("Password", password).commit();
                            context.getSharedPreferences("USERNAME", MODE_PRIVATE)
                                    .edit()
                                    .putString("Username", username).commit();
                            loggedIn = true;
                            SuccessLoggedIn();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),
                                    context.getString(R.string.ParseFailedLogin),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }

        public void SuccessLoggedIn() {
            final Context context = getActivity();
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                findPreference("ParseLogin").setSummary(username + context.getString(R.string.ParseLogInWithName));
                findPreference("ParseLogin").setTitle(context.getString(R.string.ParseLoggedIn));
                findPreference("ParseLogin").setEnabled(false);
                findPreference("ParseNotification").setEnabled(true);
                findPreference("ParseNotification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.parse_notification);
                        dialog.setTitle(context.getString(R.string.SendNotificationTitle));
                        final Button sendNot = (Button) dialog.findViewById(R.id.ParseSendNotification);
                        final EditText parseNotificationMessage = (EditText) dialog.findViewById(R.id.ParseNotificationMessage);
                        final Spinner channel = (Spinner) dialog.findViewById(R.id.ParseNotificationChannel);
                        parseNotificationMessage.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s == null || s.length() == 0)
                                    sendNot.setEnabled(false);
                                else sendNot.setEnabled(true);
                            }
                        });

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                                R.array.ParseNotificationChannel, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        final String[] channelSelected = {""};
                        channel.setAdapter(adapter);
                        channel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0: {
                                        channelSelected[0] = "everyBody";
                                    }
                                    break;
                                    case 1: {
                                        channelSelected[0] = "moderators";
                                    }
                                    break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        sendNot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ParseUser.getCurrentUser() != null) {
                                    progressDialog = ProgressDialog.show(context,
                                            getString(R.string.ParseNotificationSending),
                                            getString(R.string.ParseNotificationPleaseWait), true);
                                    ParsePush parsePush = new ParsePush();
                                    parsePush.setMessage(username + ": " + parseNotificationMessage.getText().toString());
                                    parsePush.setChannel(channelSelected[0]);
                                    parsePush.sendInBackground(new SendCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.ParseNotificationSentSucces), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.ParseNotificationSentFailed), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                } else
                                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.ParseNotificationSentFailed), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                        return false;
                    }
                });
                ParseAnalytics.trackAppOpenedInBackground(getActivity().getIntent());
            }

        }
    }

}
