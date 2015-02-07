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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import hu.zokni1996.android_forum.Main.Splash;
import hu.zokni1996.android_forum.Parse.ParseError;
import hu.zokni1996.android_forum.R;

public class MainService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String id = "";
    private boolean enabledBoolean = true;
    private boolean lastPostShow = false;
    private int timeCheck = 300000;
    private int NotificationPriorityInt;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
            int NotificationStyle = Integer.parseInt(sharedPreferences.getString(key, "4"));
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
            int NotificationTimeCheck = Integer.parseInt(sharedPreferences.getString(key, "1"));
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
            int NotificationPriority = Integer.parseInt(sharedPreferences.getString(key, "1"));
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
    }

    private boolean updateConnectedFlags() {
        /* Check the device whatever connected to network */
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connectedMobile = mMobile.isConnected();
        boolean connectedWifi = mWifi.isConnected();
        return connectedMobile || connectedWifi;
    }

    private void NotificationMethod(int NotificationPriorityInt, String content, String title) {
        getSettings();
        Intent intent = new Intent(getApplicationContext(), Splash.class);
        intent.putExtra("nameID", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainService.this)
                .setSmallIcon(R.drawable.ic_stat_af)
                .setContentTitle(getString(R.string.NotificationContentTitle))
                .setAutoCancel(true)
                .setContentText(getString(R.string.NotificationContentText))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationPriorityInt)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, intent, 0));
        inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.TheLatestPostsTitle));

        String[] contentArray = content.split("THIS_IS_THE_SPLIT");
        String updatedString = getString(contentArray);
        String[] titleArray = title.split("THIS_IS_THE_SPLIT");
        String[] updatedArray = updatedString.split("THIS_IS_THE_SPLIT");

        if (NotificationStyleInt == 1)
            try {
                NotificationStyleInt1(titleArray, updatedArray);
            } catch (NullPointerException e) {
                parseError.sendError("MainService.class", "NotificationStyleInt 1", "" + e, id);
            }
        if (NotificationStyleInt == 2)
            try {
                NotificationStyleInt2(titleArray, updatedArray);
            } catch (NullPointerException e) {
                parseError.sendError("MainService.class", "NotificationStyleInt 2", "" + e, id);
            }
        if (NotificationStyleInt == 3)
            try {
                NotificationStyleInt3(titleArray);
            } catch (NullPointerException e) {
                parseError.sendError("MainService.class", "NotificationStyleInt 3", "" + e, id);
            }
        if (NotificationStyleInt == 4)
            try {
                NotificationStyleInt4(titleArray, updatedArray);
            } catch (NullPointerException e) {
                parseError.sendError("MainService.class", "NotificationStyleInt 4", "" + e, id);
            }
        mBuilder.setStyle(inboxStyle);
        mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 0;
        mBuilder.setStyle(inboxStyle);
        Notification notification = mBuilder.build();
        notificationManager.notify(notificationId, notification);

    }

    private String getString(String[] CONTENT_ARRAY_TO_UPDATED_TIME) {
        /* Get the time from the content, that we will compare the saved time */
        String updatedString = " •";
        for (String aContentArray : CONTENT_ARRAY_TO_UPDATED_TIME) {
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
                                                updatedString += "THIS_IS_THE_SPLIT •";
                                                there = false;
                                                you = false;
                                            }
                                        }
                                    }
                j++;
            }
        }
        return updatedString;
    }

    private void getSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int NotificationStyle = Integer.parseInt(sharedPreferences.getString("NotificationStyle", "4"));
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
        int NotificationTimeCheck = Integer.parseInt(sharedPreferences.getString("NotificationTimeCheck", "1"));
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
        int NotificationPriority = Integer.parseInt(sharedPreferences.getString("NotificationPriority", "1"));
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

    private void NotificationStyleInt1(String[] titleArray, String[] updatedArray) throws NullPointerException {
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
                            topicNameString += "THIS_IS_THE_SPLIT•";
                            there = false;
                            you = false;
                        }
                    }
                }
                j++;
            }
        }
        String[] topicArray = topicNameString.split("THIS_IS_THE_SPLIT");
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
    }

    private void NotificationStyleInt2(String[] titleArray, String[] updatedArray) throws NullPointerException {
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
                                    forumString += "THIS_IS_THE_SPLIT•";
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
        String[] forumArray = forumString.split("THIS_IS_THE_SPLIT");
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
    }

    private void NotificationStyleInt3(String[] titleArray) throws NullPointerException {
        if (!lastPostShow)
            for (String aNewPostsArray : titleArray) inboxStyle.addLine(aNewPostsArray);
        else inboxStyle.addLine(titleArray[0]);
    }

    private void NotificationStyleInt4(String[] titleArray, String[] updatedArray) throws NullPointerException {
        if (!lastPostShow)
            for (int i = 0; i < titleArray.length; i++) {
                titleArray[i] += updatedArray[i];
                inboxStyle.addLine(titleArray[i]);
            }
        else {
            titleArray[0] += updatedArray[0];
            inboxStyle.addLine(titleArray[0]);
        }
    }

    private class MySync extends Thread {
        public void run() {
            while (enabledBoolean) {
                getSettings();
                if (updateConnectedFlags()) {
                    getSettings();
                    new DownloadXmlTask().execute("http://android-forum.hu/feed.php?mobile=mobile");
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

    private class DownloadXmlTask extends AsyncTask<String, Void, Void> {
        String stringUPDATED = "";
        String contentSTRING = "";
        String titleSTRING = "";

        @Override
        protected Void doInBackground(String... urls) {
            try {
                Document document = Jsoup.connect("http://android-forum.hu/feed.php").timeout(10000).get();
                Elements updated = document.select("updated");
                Elements entry = document.select("entry");

                for (int i = 0; i < entry.size(); i++) {
                    /* Get all the titles */
                    Elements title = entry.get(i).select("title");
                    for (int j = 0; j < title.size(); j++) {
                        Elements titleAttr = title.get(j).getElementsByAttributeValueContaining("type", "html");
                        for (int l = 0; l < titleAttr.size(); l++) {
                            titleSTRING += "• ";
                            titleSTRING += titleAttr.get(l).text().replace("<![CDATA[", "").replace("]]>", "");
                            titleSTRING += "THIS_IS_THE_SPLIT";
                        }
                    }
                    /* Get all the contents*/
                    Elements content = entry.get(i).select("content");
                    for (int j = 0; j < content.size(); j++) {
                        contentSTRING += content.get(j).text().replace("<![CDATA[", "]]>").replace("]]>", "");
                        contentSTRING += "THIS_IS_THE_SPLIT";
                    }
                }
                /* Get the latest post time */
                stringUPDATED = updated.get(0).text();
            } catch (IOException e) {
                parseError.sendError("MainService.java", "DOWNLOAD_FEED", "" + e, id);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getSettings();
            /* Get the saved time */
            String stringSAVED = getSharedPreferences("LAST_UPDATED", MODE_PRIVATE).getString("LastUpdated", "");
            /*  If the data got successfully, save the time */
            if (stringUPDATED.length() > 0 && !stringUPDATED.equals(stringSAVED) && contentSTRING.length() > 0 && titleSTRING.length() > 0) {
                getSharedPreferences("LAST_UPDATED", MODE_PRIVATE)
                        .edit()
                        .putString("LastUpdated", stringUPDATED)
                        .commit();
                /* If the saved time > 0, then make the notification */
                if (stringSAVED.length() > 0)
                    NotificationMethod(NotificationPriorityInt, contentSTRING, titleSTRING);

            }
        }
    }

}
