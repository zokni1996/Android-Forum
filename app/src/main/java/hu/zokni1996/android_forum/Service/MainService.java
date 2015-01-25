package hu.zokni1996.android_forum.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hu.zokni1996.android_forum.Main.Main;
import hu.zokni1996.android_forum.Parse.ParseError;
import hu.zokni1996.android_forum.R;

public class MainService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String checkNowLastPostTimeString = "";
    private String lastUpdatedString = "";
    private String savedUpdatedString = "";
    private String contentString = "";
    private String titleString = "";
    private String updatedString = "";
    private String contentArray[];
    private String titleArray[];
    private String updatedArray[];
    private String id = "";
    private boolean enabledBoolean = true;
    private boolean wifiConnectedBoolean = false;
    private boolean mobileConnectedBoolean = false;
    private boolean lastPostShow = false;
    private int timeCheck = 300000;
    private int NotificationPriorityInt;
    private int NotificationSoundInt;
    private int NotificationLedInt;
    private int NotificationVibrateInt;
    private int NotificationStyleInt;
    private NotificationCompat.InboxStyle inboxStyle;
    private ParseError parseError = new ParseError();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        enabledBoolean = true;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        getSettings();
        id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        new MySync().start();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        enabledBoolean = false;
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("NotificationStyle")) {
            SharedPreferences NotificationStylePref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int NotificationStyle = Integer.parseInt(NotificationStylePref.getString("NotificationStyle", "4"));
            switch (NotificationStyle) {
                case 1:
                    NotificationStyleInt = 1;
                    break;
                case 2:
                    NotificationStyleInt = 2;
                    break;
                case 3:
                    NotificationStyleInt = 3;
                    break;
                case 4:
                    NotificationStyleInt = 4;
                    break;
            }
        }
        if (key.equals("NotificationTimeCheck")) {
            SharedPreferences NotificationTimeCheckPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int NotificationTimeCheck = Integer.parseInt(NotificationTimeCheckPref.getString("NotificationTimeCheck", "1"));
            switch (NotificationTimeCheck) {
                case 1:
                    timeCheck = 300000;
                    break;
                case 2:
                    timeCheck = 600000;
                    break;
                case 3:
                    timeCheck = 1800000;
                    break;
                case 4:
                    timeCheck = 3600000;
                    break;
                case 5:
                    timeCheck = 7200000;
                    break;
            }
        }
        if (key.equals("NotificationPriority")) {
            SharedPreferences NotificationPriorityPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int NotificationPriority = Integer.parseInt(NotificationPriorityPref.getString("NotificationPriority", "1"));
            switch (NotificationPriority) {
                case 1:
                    NotificationPriorityInt = Notification.PRIORITY_DEFAULT;
                    break;
                case 2:
                    NotificationPriorityInt = Notification.PRIORITY_MIN;
                    break;
                case 3:
                    NotificationPriorityInt = Notification.PRIORITY_LOW;
                    break;
                case 4:
                    NotificationPriorityInt = Notification.PRIORITY_HIGH;
                    break;
                case 5:
                    NotificationPriorityInt = Notification.PRIORITY_MAX;
                    break;
            }
        }
        if (key.equals("Sound")) {
            SharedPreferences NotificationSoundPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean NotificationSound = NotificationSoundPref.getBoolean("Sound", false);
            if (NotificationSound)
                NotificationSoundInt = Notification.DEFAULT_SOUND;
            else
                NotificationSoundInt = 0;
        }
        if (key.equals("Led")) {
            SharedPreferences NotificationLedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean NotificationLed = NotificationLedPref.getBoolean("Led", false);
            if (NotificationLed)
                NotificationLedInt = Notification.DEFAULT_LIGHTS;
            else
                NotificationLedInt = -999;
        }
        if (key.equals("Vibrate")) {
            SharedPreferences NotificationVibratePref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean NotificationVibrate = NotificationVibratePref.getBoolean("Vibrate", false);
            if (!NotificationVibrate)
                NotificationVibrateInt = Notification.DEFAULT_VIBRATE;
            else
                NotificationVibrateInt = -999;
        }
        if (key.equals("LastPostNotification")) {
            SharedPreferences NotificationLastPostPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean NotificationLastPost = NotificationLastPostPref.getBoolean("LastPostNotification", false);
            if (!NotificationLastPost)
                lastPostShow = false;
            else lastPostShow = true;
        }
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnectedBoolean = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnectedBoolean = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnectedBoolean = false;
            mobileConnectedBoolean = false;
        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        AndroidForumParser stackAndroidForumParser = new AndroidForumParser();
        List<AndroidForumParser.Entry> entries = null;

        try {
            stream = downloadUrl(urlString);
            entries = stackAndroidForumParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        for (AndroidForumParser.Entry entry : entries) {
            titleString += "• ";
            titleString += entry.title;
            titleString += "ThisIsTheSplit";
            checkNowLastPostTimeString += entry.updated;
            checkNowLastPostTimeString += " ";
            contentString += entry.content;
            contentString += "ThisIsTheSplit";
        }
        return "";
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    private void NotificationMethod(int NotificationVibrateInt, int NotificationSoundInt, int NotificationLedInt, int NotificationPriorityInt) {
        getSettings();
        Intent intent = new Intent(getApplicationContext(), Main.class);
        intent.putExtra("nameID", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainService.this)

                .setSmallIcon(R.drawable.ic_stat_af)
                .setContentTitle(getString(R.string.NotificationContentTitle))
                .setAutoCancel(true)
                .setContentText(getString(R.string.NotificationContentText))
                .setDefaults(NotificationVibrateInt | NotificationSoundInt | NotificationLedInt)
                .setPriority(NotificationPriorityInt)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, intent, 0));
        inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.TheLatestPostsTitle));
        updatedString = " •";
        contentArray = contentString.split("ThisIsTheSplit");
        getString();
        titleArray = titleString.split("ThisIsTheSplit");
        updatedArray = updatedString.split("ThisIsTheSplit");

        if (NotificationStyleInt == 1)
            NotificationStyleInt1();
        if (NotificationStyleInt == 2)
            NotificationStyleInt2();
        if (NotificationStyleInt == 3)
            NotificationStyleInt3();
        if (NotificationStyleInt == 4)
            NotificationStyleInt4();
        mBuilder.setStyle(inboxStyle);
        mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 0;
        mBuilder.setStyle(inboxStyle);
        Notification notification = mBuilder.build();
        notificationManager.notify(notificationId, notification);
        getSharedPreferences("LAST_UPDATED", MODE_PRIVATE)
                .edit()
                .putString("LastUpdated", lastUpdatedString)
                .commit();
        setStringNull();
    }

    private void getString() {
        for (String aContentArray : contentArray) {
            boolean you = true;
            int j = 0;
            while (you) {
                if (aContentArray.charAt(j) == '<')
                    if (aContentArray.charAt(j + 1) == '/')
                        if (aContentArray.charAt(j + 2) == 'a')
                            if (aContentArray.charAt(j + 3) == '>')
                                if (aContentArray.charAt(j + 4) == ' ')
                                    if (aContentArray.charAt(j + 5) == '—') {
                                        boolean there = true;
                                        int a = j + 6;
                                        while (there) {
                                            updatedString += aContentArray.charAt(a);
                                            a++;
                                            if (aContentArray.charAt(a) == '<') {
                                                updatedString += "ThisIsTheSplit •";
                                                there = false;
                                                you = false;
                                            }
                                        }
                                    }
                j++;
            }
        }
    }

    private void setStringNull() {
        lastUpdatedString = "";
        savedUpdatedString = "";
        contentString = "";
        titleString = "";
        updatedString = "";
        try {
            if (contentArray != null)
                for (int i = 0; i < contentArray.length; i++)
                    contentArray[i] = "";
        } catch (Exception e) {
            parseError.sendError("MainService.class", "Set NULL contentArray", "" + e, id);
        }
        try {
            if (titleArray != null)
                for (int i = 0; i < titleArray.length; i++)
                    titleArray[i] = "";
        } catch (Exception e) {
            parseError.sendError("MainService.class", "Set NULL titleArray", "" + e, id);
        }
        try {
            if (updatedArray != null)
                for (int i = 0; i < updatedArray.length; i++)
                    updatedArray[i] = "";
        } catch (Exception e) {
            parseError.sendError("MainService.class", "Set NULL updatedArray", "" + e, id);
        }
        try {
            inboxStyle = null;
        } catch (Exception e) {
            parseError.sendError("MainService.class", "Set NULL inboxStyle", "" + e, id);
        }
    }

    private void getSettings() {
        SharedPreferences NotificationStylePref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int NotificationStyle = Integer.parseInt(NotificationStylePref.getString("NotificationStyle", "4"));
        switch (NotificationStyle) {
            case 1:
                NotificationStyleInt = 1;
                break;
            case 2:
                NotificationStyleInt = 2;
                break;
            case 3:
                NotificationStyleInt = 3;
                break;
            case 4:
                NotificationStyleInt = 4;
                break;
        }
        SharedPreferences NotificationTimeCheckPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int NotificationTimeCheck = Integer.parseInt(NotificationTimeCheckPref.getString("NotificationTimeCheck", "1"));
        switch (NotificationTimeCheck) {
            case 1:
                timeCheck = 300000;
                break;
            case 2:
                timeCheck = 600000;
                break;
            case 3:
                timeCheck = 1800000;
                break;
            case 4:
                timeCheck = 3600000;
                break;
            case 5:
                timeCheck = 7200000;
                break;
        }
        SharedPreferences NotificationPriorityPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int NotificationPriority = Integer.parseInt(NotificationPriorityPref.getString("NotificationPriority", "1"));
        switch (NotificationPriority) {
            case 1:
                NotificationPriorityInt = Notification.PRIORITY_DEFAULT;
                break;
            case 2:
                NotificationPriorityInt = Notification.PRIORITY_MIN;
                break;
            case 3:
                NotificationPriorityInt = Notification.PRIORITY_LOW;
                break;
            case 4:
                NotificationPriorityInt = Notification.PRIORITY_HIGH;
                break;
            case 5:
                NotificationPriorityInt = Notification.PRIORITY_MAX;
                break;
        }
        SharedPreferences NotificationSoundPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean NotificationSound = NotificationSoundPref.getBoolean("Sound", false);
        if (NotificationSound)
            NotificationSoundInt = Notification.DEFAULT_SOUND;
        else
            NotificationSoundInt = 0;

        SharedPreferences NotificationLedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean NotificationLed = NotificationLedPref.getBoolean("Led", false);
        if (NotificationLed)
            NotificationLedInt = Notification.DEFAULT_LIGHTS;
        else
            NotificationLedInt = -999;

        SharedPreferences NotificationVibratePref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean NotificationVibrate = NotificationVibratePref.getBoolean("Vibrate", false);
        if (NotificationVibrate)
            NotificationVibrateInt = Notification.DEFAULT_VIBRATE;
        else
            NotificationVibrateInt = -999;
        SharedPreferences NotificationPostPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean NotificationPost = NotificationPostPref.getBoolean("LastPostNotification", false);
        if (NotificationPost)
            lastPostShow = true;
        else
            lastPostShow = false;
    }

    private void NotificationStyleInt1() {
        try {
            String topicNameString = "• ";
            for (String aNewPostsArray : titleArray) {
                boolean you = true;
                int j = 0;
                while (you) {
                    if (aNewPostsArray.charAt(j) == '•') {
                        boolean there = true;
                        int a = j + 1;
                        while (there) {
                            topicNameString += aNewPostsArray.charAt(a);
                            a++;
                            if (a == aNewPostsArray.length()) {
                                you = false;
                                break;
                            }
                            if (aNewPostsArray.charAt(a) == '•') {
                                topicNameString += "ThisIsTheSplit•";
                                there = false;
                                you = false;
                            }
                        }
                    }
                    j++;
                }
            }
            String[] topicArray = topicNameString.split("ThisIsTheSplit");
            if (!lastPostShow)
                for (int i = 0; i < topicArray.length; i++) {
                    topicArray[i] += updatedArray[i];
                    inboxStyle.addLine(topicArray[i]);
                }
            else {
                topicArray[0] += updatedArray[0];
                inboxStyle.addLine(topicArray[0]);
            }
            for (int i = 0; i < topicArray.length; i++)
                topicArray[i] = "";
        } catch (Exception e) {
            parseError.sendError("MainService.class", "NotificationStyleInt 1", "" + e, id);
        }
    }

    private void NotificationStyleInt2() {
        try {
            String forumString = "•";
            for (String aNewPostsArray : titleArray) {
                boolean you = true;
                int j = 0;
                while (you) {
                    if (aNewPostsArray.charAt(j) == '•') {
                        boolean you2 = true;
                        int k = j;
                        k++;
                        while (you2) {
                            if (aNewPostsArray.charAt(k) == '•') {
                                int a = k + 1;
                                while (true) {
                                    forumString += aNewPostsArray.charAt(a);
                                    a++;
                                    if (a == aNewPostsArray.length()) {
                                        you = false;
                                        you2 = false;
                                        forumString += "ThisIsTheSplit•";
                                        break;
                                    }
                                }
                            }
                            k++;
                            if (k == aNewPostsArray.length()) {
                                you = false;
                                break;
                            }
                        }
                    }
                    j++;
                }
            }
            String[] forumArray = forumString.split("ThisIsTheSplit");
            if (!lastPostShow)
                for (int i = 0; i < forumArray.length; i++) {
                    forumArray[i] += updatedArray[i];
                    inboxStyle.addLine(forumArray[i]);
                }
            else {
                forumArray[0] += updatedArray[0];
                inboxStyle.addLine(forumArray[0]);
            }
            for (int i = 0; i < forumArray.length; i++)
                forumArray[i] = "";
        } catch (Exception e) {
            parseError.sendError("MainService.class", "NotificationStyleInt 2", "" + e, id);
        }
    }

    private void NotificationStyleInt3() {
        try {
            if (!lastPostShow)
                for (String aNewPostsArray : titleArray) inboxStyle.addLine(aNewPostsArray);
            else inboxStyle.addLine(titleArray[0]);
        } catch (Exception e) {
            parseError.sendError("MainService.class", "NotificationStyleInt 3", "" + e, id);
        }
    }

    private void NotificationStyleInt4() {
        try {
            if (!lastPostShow)
                for (int i = 0; i < titleArray.length; i++) {
                    titleArray[i] += updatedArray[i];
                    inboxStyle.addLine(titleArray[i]);
                }
            else {
                titleArray[0] += updatedArray[0];
                inboxStyle.addLine(titleArray[0]);
            }
        } catch (Exception e) {
            parseError.sendError("MainService.class", "NotificationStyleInt 4", "" + e, id);
        }

    }

    private class MySync extends Thread {
        public void run() {
            while (enabledBoolean) {
                updateConnectedFlags();
                getSettings();
                if (wifiConnectedBoolean || mobileConnectedBoolean) {
                    getSettings();
                    savedUpdatedString = getSharedPreferences("LAST_UPDATED", MODE_PRIVATE).getString("LastUpdated", "");
                    new DownloadXmlTask().execute("http://android-forum.hu/feed.php");
                    try {
                        sleep(timeCheck);
                    } catch (InterruptedException e) {
                        parseError.sendError("MainService.class", "The sleep interrupted:", "" + e, id);
                    }
                } else {
                    getSettings();
                    try {
                        sleep(timeCheck);
                    } catch (InterruptedException e) {
                        parseError.sendError("MainService.class", "The sleep interrupted:", "" + e, id);
                    }
                }
            }
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "" + e;
            } catch (XmlPullParserException e) {
                return "" + e;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            getSettings();
            try {
                if (checkNowLastPostTimeString.length() > 50)
                    if (checkNowLastPostTimeString.charAt(51) == ' ')
                        lastUpdatedString = checkNowLastPostTimeString.substring(25, 51);
            } catch (Exception e) {
                parseError.sendError("MainService.class", "Can\'t check the update", "" + e, id);
            }
            try {
                if (!lastUpdatedString.equals(savedUpdatedString))
                    NotificationMethod(NotificationVibrateInt, NotificationSoundInt, NotificationLedInt, NotificationPriorityInt);
            } catch (Exception e) {
                parseError.sendError("MainService.class", "Can\'t check the update", "" + e, id);
            }
            checkNowLastPostTimeString = "";
        }
    }
}
