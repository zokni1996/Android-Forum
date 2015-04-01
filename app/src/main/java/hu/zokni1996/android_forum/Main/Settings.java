package hu.zokni1996.android_forum.Main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import hu.zokni1996.android_forum.Parse.ParseLoginDialog;
import hu.zokni1996.android_forum.Parse.ParseSendNotificationDialog;
import hu.zokni1996.android_forum.R;

public class Settings extends PreferenceActivity {

    static String changeLogHun = "";
    static String changeLogEng = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.StyleThemeSettings);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.activity_settings, target);
        for (Header header : target) {
            if (header.titleRes == R.string.AboutMe)
                header.iconRes = R.drawable.ic_action_info_outline;
            if (header.titleRes == R.string.ScreenSettings)
                header.iconRes = R.drawable.ic_hardware_phone_android;
            if (header.titleRes == R.string.Extras)
                header.iconRes = R.drawable.ic_image_exposure_plus_1;
            if (header.titleRes == R.string.NameRules)
                header.iconRes = R.drawable.ic_action_report_problem;
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
            addPreferencesFromResource(R.xml.settings_extras);
            final Context context = getActivity();
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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_about);
        }
    }

    public static class FragmentSettingsScreen extends PreferenceFragment {
        String username;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);
            final Context context = getActivity();
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
            if (updateConnectedFlags(context)) {
                final ParseLoginDialog parseLoginDialog = new ParseLoginDialog(context);
                parseLoginDialog.LoginTry(new ParseLoginDialog.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(final ParseUser parseUser) {
                        if (parseUser != null) {
                            username = parseUser.getUsername();
                            findPreference("ParseLogin").setSummary(parseUser.getUsername() + getString(R.string.ParseLogInWithName));
                            findPreference("ParseLogin").setTitle(getString(R.string.ParseLoggedIn));
                            findPreference("ParseLogin").setEnabled(false);
                            findPreference("ParseNotification").setEnabled(true);

                        } else {
                            findPreference("ParseLogin").setSummary("");
                            findPreference("ParseLogin").setTitle(getResources().getString(R.string.ParseLogIn));
                            findPreference("ParseLogin").setEnabled(true);
                            findPreference("ParseNotification").setEnabled(false);
                            findPreference("ParseLogin").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                                @Override
                                public boolean onPreferenceClick(Preference preference) {
                                    parseLoginDialog.LoginNew(new ParseLoginDialog.OnTaskCompleted() {
                                        @Override
                                        public void onTaskCompleted(final ParseUser parseUser) {
                                            if (parseUser != null) {
                                                username = parseUser.getUsername();
                                                findPreference("ParseLogin").setSummary(parseUser.getUsername() + context.getString(R.string.ParseLogInWithName));
                                                findPreference("ParseLogin").setTitle(context.getString(R.string.ParseLoggedIn));
                                                findPreference("ParseLogin").setEnabled(false);
                                                findPreference("ParseNotification").setEnabled(true);
                                            }
                                        }
                                    });
                                    return true;
                                }
                            });
                        }
                    }
                });
                findPreference("ParseNotification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new ParseSendNotificationDialog(username, context);
                        return true;
                    }
                });
            }
        }

        private boolean updateConnectedFlags(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean connectedMobile = mMobile.isConnected();
            boolean connectedWifi = mWifi.isConnected();
            return connectedMobile || connectedWifi;
        }
    }

    public static class FragmentSettingsRules extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_rules);
        }
    }

}
