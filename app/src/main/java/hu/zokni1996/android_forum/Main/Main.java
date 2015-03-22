package hu.zokni1996.android_forum.Main;


import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import java.util.ArrayList;
import java.util.List;

import hu.zokni1996.android_forum.Favourites.AddToFavourite;
import hu.zokni1996.android_forum.Favourites.GetFavourites;
import hu.zokni1996.android_forum.Items.Items;
import hu.zokni1996.android_forum.Main.ActiveTopics.ActiveTopicsFragment;
import hu.zokni1996.android_forum.Main.Web.WebFragment;
import hu.zokni1996.android_forum.R;

public class Main extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    List<Items> itemsList = new ArrayList<>();
    boolean booleanMenu = true;
    ActionBarDrawerToggle mDrawerToggle;
    static WebFragment webFragment = new WebFragment();
    ActiveTopicsFragment activeTopicsFragment = new ActiveTopicsFragment();
    static ActionBar actionBar;
    boolean booleanLoadNewPost = false;
    boolean booleanFavourites = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (booleanMenu)
            menu.findItem(R.id.favouriteMenu).setVisible(true);
        else
            menu.findItem(R.id.favouriteMenu).setVisible(false);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);

        //Get the references for the objects
        mDrawerLayout = (DrawerLayout) findViewById(R.id.Drawer);
        mDrawerList = (ListView) findViewById(android.R.id.list);

        DrawerSETUP();

        //Cancel all the application notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            booleanLoadNewPost = extras.getBoolean("nameID");

        //Register preference changer
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        if (booleanLoadNewPost) {
            getFragmentManager().beginTransaction().add(R.id.frameLayout, webFragment, "WEB_FRAGMENT").commit();
            getFragmentManager().beginTransaction().add(R.id.frameLayout, activeTopicsFragment, "ACTIVE_TOPICS_FRAGMENT").commit();
            booleanLoadNewPost = false;
        } else {
            getFragmentManager().beginTransaction().add(R.id.frameLayout, webFragment, "WEB_FRAGMENT").commit();
        }
    }

    private void DrawerSETUP() {
        final String[] strings = getResources().getStringArray(R.array.drawer_list);
        itemsList.add(new Items(strings[0], R.drawable.ic_action_action_home_holo_light));
        itemsList.add(new Items(strings[1], R.drawable.ic_action_action_stars));
        itemsList.add(new Items(strings[2], R.drawable.ic_action_new));
        itemsList.add(new Items(strings[3], R.drawable.ic_action_communication_forum));
        itemsList.add(new Items(strings[4], R.drawable.ic_action_action_visibility_off));
        itemsList.add(new Items(strings[5], R.drawable.ic_action_action_settings_holo_light));
        ArrayAdapter<Items> itemsArrayAdapter = new ListViewAdapter();

        mDrawerList.setAdapter(itemsArrayAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int editedPosition = position + 1;
                if (editedPosition == 1) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                    webFragment.LoadURL("http://android-forum.hu/index.php?mobile=mobile");
                    webFragment.booleanClearHistory = true;
                    mDrawerLayout.closeDrawer(mDrawerList);

                }
                if (editedPosition == 2) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                    booleanFavourites = true;
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 3) {
                    if (!activeTopicsFragment.isAdded()) {
                        getFragmentManager().beginTransaction().add(R.id.frameLayout, activeTopicsFragment, "ACTIVE_TOPICS_FRAGMENT").commit();
                    }
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 4) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                    webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unanswered");
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 5) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                    webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unreadposts");
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 6)
                    startActivity(new Intent(getApplicationContext(), Settings.class));
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                webFragment.getSwipeRefreshLayout().setEnabled(false);
                if (slideOffset == 0.0) {
                    webFragment.getSwipeRefreshLayout().setEnabled(true);
                }
            }

            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
                if (booleanFavourites)
                    new GetFavourites(Main.this);
                booleanFavourites = false;
                webFragment.getSwipeRefreshLayout().setEnabled(true);
                if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null)
                    getSupportActionBar().setTitle(strings[2]);
                else
                    actionBar.setTitle(webFragment.setTitleWebView());
                if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") == null)
                    booleanMenu = true;
                else
                    booleanLoadNewPost = false;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
                syncState();
                webFragment.getSwipeRefreshLayout().setEnabled(false);
                getSupportActionBar().setTitle(R.string.NameApp);
                booleanMenu = false;
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        actionBar = getSupportActionBar();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            booleanMenu = true;
            invalidateOptionsMenu();
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
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
                if (mDrawerLayout.isDrawerOpen(mDrawerList))
                    mDrawerLayout.closeDrawer(mDrawerList);
                else
                    mDrawerLayout.openDrawer(mDrawerList);
            }
            break;
            case R.id.favouriteMenu:
                new AddToFavourite(webFragment.getWebViewMain().getUrl(), this);
                break;
        }
        return true;
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public class ListViewAdapter extends ArrayAdapter<Items> {
        public ListViewAdapter() {
            super(Main.this, R.layout.main_list, itemsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.main_list, parent, false);

            Items currentItem = itemsList.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewIconID);
            TextView textView = (TextView) itemView.findViewById(R.id.textVewName);
            imageView.setImageResource(currentItem.getIconID());
            textView.setText(currentItem.getName());

            return itemView;
        }
    }

    public static int getActionBarSize() {
        return actionBar.getHeight();
    }

    public static WebFragment getWebFragment() {
        return webFragment;
    }

    public static void setActionBarTitle(String title) {
        actionBar.setTitle(title);
    }
}
