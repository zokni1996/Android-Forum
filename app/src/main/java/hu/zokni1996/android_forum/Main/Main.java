package hu.zokni1996.android_forum.Main;


import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.zokni1996.android_forum.Items.Items;
import hu.zokni1996.android_forum.Main.ActiveTopics.ActiveTopicsFragment;
import hu.zokni1996.android_forum.Main.Web.WebFragment;
import hu.zokni1996.android_forum.R;

public class Main extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener, WebFragment.WebFragmentWebClicked {

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    List<Items> itemsList = new ArrayList<>();
    boolean booleanMenu = true;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    WebFragment webFragment;
    boolean booleanOnKeyDown = true;
    ActiveTopicsFragment activeTopicsFragment;
    ActionBar actionBar;
    boolean booleanLoadNewPost = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (booleanMenu)
            menu.findItem(R.id.favouriteMenu).setVisible(true);
        else
            menu.findItem(R.id.favouriteMenu).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);

        //Get the references for the objects
        toolbar = (Toolbar) findViewById(R.id.Toolbar);
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
        booleanOnKeyDown = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("OnKeyDown", true);

        if (booleanLoadNewPost) {
            webFragment = new WebFragment();
            webFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.frameLayout, webFragment, "WEB_FRAGMENT").commit();
            activeTopicsFragment = new ActiveTopicsFragment();
            activeTopicsFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.frameLayout, activeTopicsFragment, "ACTIVE_TOPICS_FRAGMENT").commit();
            booleanLoadNewPost = false;
        } else {
            webFragment = new WebFragment();
            webFragment.setArguments(getIntent().getExtras());
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
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") == null) {
                        webFragment.LoadURL("http://android-forum.hu/index.php?mobile=mobile");
                        webFragment.booleanClearHistory = true;
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                        webFragment.LoadURL("http://android-forum.hu/index.php?mobile=mobile");
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
                if (editedPosition == 2) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") == null) {
                        Favourites();
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                        getSupportActionBar().setTitle(webFragment.setTitleWebView());
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
                if (editedPosition == 3) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") == null) {
                        getSupportActionBar().setTitle(strings[2]);
                        mDrawerLayout.closeDrawer(mDrawerList);
                        activeTopicsFragment = new ActiveTopicsFragment();
                        getFragmentManager().beginTransaction().add(R.id.frameLayout, activeTopicsFragment, "ACTIVE_TOPICS_FRAGMENT").commit();
                    } else {
                        getSupportActionBar().setTitle(strings[2]);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
                if (editedPosition == 4) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") == null) {
                        webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unanswered");
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                        webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unanswered");
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
                if (editedPosition == 5) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") == null) {
                        webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unreadposts");
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT")).commit();
                        webFragment.LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unreadposts");
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
                if (editedPosition == 6)
                    startActivity(new Intent(getApplicationContext(), Settings.class));
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                webFragment.getSwipeRefreshLayout().setEnabled(false);
                webFragment.getFloatingActionButtonBACK().hide();
                webFragment.getFloatingActionButtonFORWARD().hide();
                if (slideOffset == 0.0) {
                    webFragment.getSwipeRefreshLayout().setEnabled(true);
                    if (webFragment.getWebViewMain().canGoForward())
                        webFragment.getFloatingActionButtonFORWARD().show();
                    if (webFragment.getWebViewMain().canGoBack())
                        webFragment.getFloatingActionButtonBACK().show();
                }
            }

            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
                webFragment.getSwipeRefreshLayout().setEnabled(true);
                if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT") != null) {
                    if (getFragmentManager().findFragmentByTag("ACTIVE_TOPICS_FRAGMENT").isVisible())
                        getSupportActionBar().setTitle(strings[2]);
                } else
                    actionBar.setTitle(webFragment.setTitleWebView());
                booleanMenu = true;
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

    private void Favourites() {
        final Dialog dialog = new Dialog(Main.this);
        dialog.setContentView(R.layout.main_favourite_list);
        dialog.setTitle(R.string.title_activity_favourites);
        final ListView listView = (ListView) dialog.findViewById(R.id.favouritesListView);
        final TextView textView = (TextView) dialog.findViewById(R.id.favouritesTextView);
        final String string = getSharedPreferences("FAVOURITES", MODE_PRIVATE).getString("Favourites", "");
        Log.i("FIRST_GET", "" + string);
        if (string != null) {
            if (!string.equals("")) {
                listView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                String[] strings = string.split("THIS_IS_THE_SPLIT");
                final String[] name = {""};
                final String[] link = {""};
                for (String string1 : strings) {
                    String[] oneName = string1.split("THIS_IS_LINK_SPLIT");
                    name[0] += oneName[1];
                    link[0] += oneName[0];
                    name[0] += "THIS_IS_SECOND_SPLIT";
                    link[0] += "THIS_IS_SECOND_SPLIT";
                }
                final String[] names = name[0].split("THIS_IS_SECOND_SPLIT");
                final String[] links = link[0].split("THIS_IS_SECOND_SPLIT");
                ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        webFragment.LoadURL(links[position]);
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        new AlertDialog.Builder(Main.this)
                                .setMessage(getString(R.string.ListItemDelete) + names[position])
                                .setCancelable(false)
                                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        links[position] = "";
                                        names[position] = "";
                                        String string = "";
                                        for (int i = 0; i < links.length; i++) {
                                            if (!links[i].equals("")) {
                                                string += links[i];
                                                string += "THIS_IS_LINK_SPLIT";
                                            }
                                            if (!names[i].equals("")) {
                                                string += names[i];
                                                string += "THIS_IS_THE_SPLIT";
                                            }
                                        }
                                        getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit().putString("Favourites", string).commit();
                                        dialogInterface.dismiss();
                                        final String stringTwo = getSharedPreferences("FAVOURITES", MODE_PRIVATE).getString("Favourites", "");
                                        if (stringTwo != null) {
                                            if (!stringTwo.equals("")) {
                                                listView.setVisibility(View.VISIBLE);
                                                textView.setVisibility(View.INVISIBLE);
                                                String[] stringTwoArray = string.split("THIS_IS_THE_SPLIT");
                                                name[0] = "";
                                                link[0] = "";
                                                for (String string1 : stringTwoArray) {
                                                    String[] oneName = string1.split("THIS_IS_LINK_SPLIT");
                                                    name[0] += oneName[1];
                                                    link[0] += oneName[0];
                                                    name[0] += "THIS_IS_SECOND_SPLIT";
                                                    link[0] += "THIS_IS_SECOND_SPLIT";
                                                }
                                                final String[] names = name[0].split("THIS_IS_SECOND_SPLIT");
                                                ArrayAdapter arrayAdapterTwo = new ArrayAdapter<>(Main.this, android.R.layout.simple_list_item_1, names);
                                                listView.setAdapter(arrayAdapterTwo);
                                            } else {
                                                listView.setVisibility(View.INVISIBLE);
                                                textView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                })
                                .setTitle(R.string.Delete)
                                .show();
                        return true;
                    }


                });
            } else {
                listView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
        }
        dialog.setCancelable(true);

        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
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
                    if (booleanOnKeyDown) {
                        new AlertDialog.Builder(this)
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
                    } else {
                        finish();
                    }
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
                AddToFavourite();
                break;
        }
        return true;
    }

    private void AddToFavourite() {
        final Dialog dialog = new Dialog(Main.this);
        dialog.setContentView(R.layout.main_add_favourite);
        dialog.setTitle(getString(R.string.AddFavourite));

        final EditText editText = (EditText) dialog.findViewById(R.id.editTextFavouriteName);
        final Button buttonSAVE = (Button) dialog.findViewById(R.id.buttonSaveFavourite);
        buttonSAVE.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0)
                    buttonSAVE.setEnabled(false);
                else buttonSAVE.setEnabled(true);
            }
        });
        buttonSAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = getSharedPreferences("FAVOURITES", MODE_PRIVATE).getString("Favourites", "");
                string += webFragment.getWebViewMain().getUrl();
                string += "THIS_IS_LINK_SPLIT";
                string += editText.getText().toString();
                string += "THIS_IS_THE_SPLIT";
                getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit().putString("Favourites", string).commit();
                dialog.dismiss();
                Toast.makeText(Main.this, getString(R.string.SavedSuccessFully), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
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
        if (key.equals("OnKeyDown"))
            booleanOnKeyDown = sharedPreferences.getBoolean(key, true);
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

    @Override
    public int getActionBarSize() {
        return actionBar.getHeight();
    }

    @Override
    public void setActionBarTitle(String title) {
        actionBar.setTitle(title);
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

}
