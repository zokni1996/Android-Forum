package hu.zokni1996.android_forum.Main;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import hu.zokni1996.android_forum.Parse.ParseLoadingDialog;
import hu.zokni1996.android_forum.Parse.ParseLoginDialog;
import hu.zokni1996.android_forum.Parse.ParseSendNotificationDialog;
import hu.zokni1996.android_forum.R;

public class Settings extends PreferenceActivity {

    static String changeLogHun = "";
    static String changeLogEng = "";
    static ActionBar actionBar;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.StyleThemeSettings);

        actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setIcon(R.drawable.ic_action_settings);
        context = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.activity_settings, target);
        for (Header header : target) {
            if (header.titleRes == R.string.AboutMe)
                header.iconRes = R.drawable.ic_action_info_outline;
            if (header.titleRes == R.string.ScreenSettings)
                header.iconRes = R.drawable.ic_hardware_phone_android;
            if (header.titleRes == R.string.NotificationSettings)
                header.iconRes = R.drawable.ic_action_announcement;
            if (header.titleRes == R.string.Extras)
                header.iconRes = R.drawable.ic_image_exposure_plus_1;
            if (header.titleRes == R.string.NameRules)
                header.iconRes = R.drawable.ic_action_report_problem;
            if (header.titleRes == R.string.ParseSettingsNotificationName)
                header.iconRes = R.drawable.ic_action_https;
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    public static class FragmentSettingsExtra extends PreferenceFragment {
        Context context;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_image_exposure_plus_1);
            addPreferencesFromResource(R.xml.settings_extras);
            context = getActivity();
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
                        context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
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
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_info_outline);

            addPreferencesFromResource(R.xml.settings_about);
            context = getActivity();

            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
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
                    WebView webView = new WebView(context);
                    MaterialDialog builder = new MaterialDialog.Builder(context)
                            .title(context.getString(R.string.ChangeLog))
                            .positiveText(getString(R.string.Ok))
                            .customView(webView, true)
                            .cancelable(false)
                            .build();
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
                        webView.loadDataWithBaseURL(null, changeLogEng, "text/html; charset=utf-8", "UTF-8", null);
                    }
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
                actionBar.setIcon(R.drawable.ic_hardware_phone_android);

            addPreferencesFromResource(R.xml.settings_screen);
        }
    }

    public static class FragmentSettingsNotification extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_announcement);

            addPreferencesFromResource(R.xml.settings_notification);
            context = getActivity();
            findPreference("NotificationParseMaintenance").setEnabled(false);
            new ParseLoginDialog(context).tryToLogIn();
            if (ParseUser.getCurrentUser() != null) {
                findPreference("NotificationParseMaintenance").setEnabled(true);
                findPreference("NotificationParseMaintenance").setOnPreferenceChangeListener(this);
            } else {
                findPreference("NotificationParseMaintenance").setEnabled(false);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ("NotificationParseMaintenance".equals(preference.getKey()))
                if ((Boolean) newValue)
                    new ParseLoadingDialog(context).subscribe();
                else
                    new ParseLoadingDialog(context).unsubscribe();
            return true;
        }
    }

    public static class FragmentSettingsRules extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_report_problem);

            addPreferencesFromResource(R.xml.settings_rules);
        }
    }

    public static class FragmentSettingsDeveloper extends PreferenceFragment {
        Context context;
        ParseLoginDialog parseLoginDialog;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (actionBar != null)
                actionBar.setIcon(R.drawable.ic_action_https);
            addPreferencesFromResource(R.xml.settings_developer);
            context = getActivity();
            parseLoginDialog = new ParseLoginDialog(context);
            parseLoginDialog.tryToLogIn();
            if (ParseUser.getCurrentUser() != null)
                SuccessLoggedIn();
            else {
                findPreference("ParseLogin").setSummary("");
                findPreference("ParseLogin").setTitle(getResources().getString(R.string.ParseLogIn));
                findPreference("ParseLogin").setEnabled(true);
                findPreference("ParseNotification").setEnabled(false);
                findPreference("ParseLogin").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        parseLoginDialog.login();
                        if (ParseUser.getCurrentUser() != null)
                            SuccessLoggedIn();
                        return true;
                    }
                });
            }
        }


        public void SuccessLoggedIn() {
            if (ParseUser.getCurrentUser() != null) {
                findPreference("ParseLogin").setSummary(parseLoginDialog.getUsername() + context.getString(R.string.ParseLogInWithName));
                findPreference("ParseLogin").setTitle(context.getString(R.string.ParseLoggedIn));
                findPreference("ParseLogin").setEnabled(false);
                findPreference("ParseNotification").setEnabled(true);
                findPreference("ParseNotification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new ParseSendNotificationDialog(parseLoginDialog.getUsername(), context);
                        return true;
                    }
                });
            }
        }
    }

}
