package hu.zokni1996.android_forum.Main;


import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import hu.zokni1996.android_forum.Favourites.GetFavourites;
import hu.zokni1996.android_forum.Main.ActiveTopics.ActiveTopicsFragment;
import hu.zokni1996.android_forum.Main.Web.WebFragment;
import hu.zokni1996.android_forum.R;

public class Main extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static WebFragment webFragment = new WebFragment();
    ActiveTopicsFragment activeTopicsFragment = new ActiveTopicsFragment();
    static ActionBar actionBar;
    public AccountHeader.Result headerResult = null;
    public Drawer.Result result = null;
    static Context context;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);
        context = Main.this;
        //Get the references for the objects
        Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);
        InitializeParse();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBar = getSupportActionBar();
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionFirstLine(getResources().getString(R.string.NameApp))
                .addProfiles(new ProfileDrawerItem().withName(getResources().getString(R.string.NameApp)).withIcon(getResources().getDrawable(R.mipmap.ic_launcher)))
                .withCurrentProfileHiddenInList(true)
                .withProfileImagesVisible(true)
                .withProfileImagesClickable(false)
                .withSelectionListEnabled(false)
                .withSavedInstance(savedInstanceState)
                .build();
        final String[] strings = getResources().getStringArray(R.array.drawer_list);
        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withHeaderDivider(false)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(strings[0]).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName(strings[1]).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName(strings[2]).withIcon(GoogleMaterial.Icon.gmd_hearing).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName(strings[3]).withIcon(GoogleMaterial.Icon.gmd_announcement).withIdentifier(4).withCheckable(false),
                        new PrimaryDrawerItem().withName(strings[4]).withIcon(GoogleMaterial.Icon.gmd_visibility_off).withIdentifier(5).withCheckable(false),
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1) {
                            if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                            webFragment.LoadURL("http://android-forum.hu/index.php?mobile=mobile");
                            webFragment.booleanClearHistory = true;
                        } else if (drawerItem.getIdentifier() == 2) {
                            if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                            new GetFavourites(Main.this);
                        } else if (drawerItem.getIdentifier() == 3) {
                            if (!activeTopicsFragment.isAdded()) {
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.add(R.id.frameLayout, activeTopicsFragment, "ACTIVE_TOPICS_FRAGMENT").commit();
                                getSupportActionBar().setTitle(strings[2]);
                            }
                        } else if (drawerItem.getIdentifier() == 4) {
                            if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                            webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unanswered");
                        } else if (drawerItem.getIdentifier() == 5) {
                            if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                            webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unreadposts");
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)

                .build();
        result.getListView().setVerticalScrollBarEnabled(false);

        //Cancel all the application notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        Bundle extras = getIntent().getExtras();
        boolean booleanLoadNewPost = false;
        if (extras != null)
            booleanLoadNewPost = extras.getBoolean("nameID");

        //Register preference changer
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        if (booleanLoadNewPost) {
            getFragmentManager().beginTransaction().add(R.id.frameLayout, webFragment, "WEB_FRAGMENT").commit();
            getFragmentManager().beginTransaction().add(R.id.frameLayout, activeTopicsFragment, "ACTIVE_TOPICS_FRAGMENT").commit();
            getSupportActionBar().setTitle(strings[2]);
        } else {
            getFragmentManager().beginTransaction().add(R.id.frameLayout, webFragment, "WEB_FRAGMENT").commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (result.isDrawerOpen()) {
                result.closeDrawer();
                return true;
            }
            if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT").isVisible()) {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                    getSupportActionBar().setTitle(webFragment.setTitleWebView());
                    return true;
                }
            if (getFragmentManager().findFragmentByTag("WEB_FRAGMENT").isVisible()) {
                if (webFragment.getWebViewMain().canGoBack()) {
                    webFragment.getProgressBarLoad().setVisibility(View.VISIBLE);
                    webFragment.getProgressBarLoad().setProgress(0);
                    webFragment.getWebViewMain().goBack();
                    return true;
                }
                if (!webFragment.getWebViewMain().canGoBack()) {
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("OnKeyDown", true)) {
                        new AlertDialogWrapper.Builder(this)
                                .setTitle(getString(R.string.SureExitTitle))
                                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                        return true;
                    } else
                        finish();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (result.isDrawerOpen())
                    result.closeDrawer();
                else
                    result.openDrawer();
            }
            break;
            case R.id.favouriteMenu:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
        }
        return true;
    }

    private void InitializeParse() {
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Parse.setLogLevel(Parse.LOG_LEVEL_INFO);
        if (!getSharedPreferences("PARSE_EVERYBODY", MODE_PRIVATE).getBoolean("ParseEveryBody", false))
            ParsePush.subscribeInBackground("everyBody", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        getSharedPreferences("PARSE_EVERYBODY", MODE_PRIVATE)
                                .edit()
                                .putBoolean("ParseEveryBody", true)
                                .commit();
                    } else {
                        getSharedPreferences("PARSE_EVERYBODY", MODE_PRIVATE)
                                .edit()
                                .putBoolean("ParseEveryBody", false)
                                .commit();
                    }
                }
            });
        if (!getSharedPreferences("PARSE_MODERATORS", MODE_PRIVATE).getBoolean("ParseModerators", false)) {
            String password = getSharedPreferences("PASSWORD", MODE_PRIVATE).getString("Password", "");
            String username = getSharedPreferences("USERNAME", MODE_PRIVATE).getString("Username", "");
            if (!username.equals("") && !password.equals("")) {
                ParsePush.subscribeInBackground("moderators", new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            getSharedPreferences("PARSE_MODERATORS", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("ParseModerators", true)
                                    .commit();
                        else {
                            getSharedPreferences("PARSE_MODERATORS", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("ParseModerators", false)
                                    .commit();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Notification")) {
            if (sharedPreferences.getBoolean(key, false)) {
                Intent intent = new Intent();
                intent.setAction("startServiceAndroidForum");
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("stopServiceAndroidForum");
                sendBroadcast(intent);
            }
        }
        if (key.equals("NotificationStyle") || key.equals("NotificationTimeCheck") || key.equals("NotificationPriority") || key.equals("NotificationRow")) {
            if (sharedPreferences.getBoolean("Notification", false)) {
                sendBroadcast(new Intent().setAction("stopServiceAndroidForum"));
                sendBroadcast(new Intent().setAction("startServiceAndroidForum"));
            }
        }
        if (key.equals("BasicZoom"))
            webFragment.getWebViewMain().getSettings().setTextZoom(Integer.parseInt(sharedPreferences.getString(key, "100")));
        if (key.equals("Zoom"))
            webFragment.getWebViewMain().getSettings().setBuiltInZoomControls(sharedPreferences.getBoolean(key, false));
    }

    public static WebFragment getWebFragment() {
        return webFragment;
    }

    public static void setActionBarTitle(String title) {
        actionBar.setTitle(title);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
