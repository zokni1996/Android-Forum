package hu.zokni1996.android_forum.Main;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.zokni1996.android_forum.Items.Items;
import hu.zokni1996.android_forum.Parse.ParseError;
import hu.zokni1996.android_forum.R;

public class Main extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener, SwipeRefreshLayout.OnRefreshListener {

    private WebView webViewMain = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private ParseError parseError = new ParseError();
    private List<Items> itemsList = new ArrayList<>();
    private ProgressBar progressBarLoad;
    private FloatingActionButton floatingActionButtonBACK;
    private FloatingActionButton floatingActionButtonFORWARD;
    private boolean booleanClearHistory = true;
    private boolean booleanOnKeyDown = true;
    private String[] stringFailedLoadPage = new String[2];
    private String stringFailingURL = "";
    private String mainUrl = "http://android-forum.hu/index.php?mobile=mobile";
    private String newPostsUrl = "http://android-forum.hu/search.php?mobile=mobile&search_id=active_topics";
    private String id = "";
    private String titleWebView = "";
    private boolean booleanFailedLoadURL = false;
    private boolean booleanReloadSwipe = false;
    private boolean booleanLoadNewPost = false;
    private boolean booleanMenu = true;

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
        setContentView(R.layout.main_web);
        IsItFirstRun();
        id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        webViewMain = (WebView) findViewById(R.id.WebViewMain);
        progressBarLoad = (ProgressBar) findViewById(R.id.ProgressBar);
        toolbar = (Toolbar) findViewById(R.id.Toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeContainer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.Drawer);
        mDrawerList = (ListView) findViewById(android.R.id.list);
        floatingActionButtonBACK = (FloatingActionButton) findViewById(R.id.WebViewBack);
        floatingActionButtonFORWARD = (FloatingActionButton) findViewById(R.id.WebViewForward);
        floatingActionButtonBACK.hide(false);
        floatingActionButtonFORWARD.hide(false);
        floatingActionButtonBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViewMain.canGoBack()) {
                    progressBarLoad.setVisibility(View.VISIBLE);
                    progressBarLoad.setProgress(0);
                    webViewMain.goBack();
                }
            }
        });
        floatingActionButtonFORWARD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViewMain.canGoForward()) {
                    progressBarLoad.setVisibility(View.VISIBLE);
                    progressBarLoad.setProgress(0);
                    webViewMain.goForward();
                }
            }
        });
        SetUpDrawer();

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        Parse.setLogLevel(Parse.LOG_LEVEL_INFO);

        String ns = Context.NOTIFICATION_SERVICE;
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            booleanLoadNewPost = extras.getBoolean("nameID");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancelAll();
        SettingsWebView();
        SettingScreen();
        webViewMain.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                String CurrentLanguage = Locale.getDefault().getDisplayLanguage();

                if (CurrentLanguage.equals("magyar")) {
                    try {
                        InputStream input;
                        AssetManager assetManager = getApplicationContext().getAssets();
                        input = assetManager.open("failedLoadPage");
                        BufferedReader f = new BufferedReader(new InputStreamReader(input));
                        String sor = f.readLine();
                        stringFailedLoadPage[0] = sor;
                        stringFailedLoadPage[0] += " " + description + " •</div><hr /><div style=\"text-align: center;\">Hiba kódja: • " + errorCode + " •</div>";
                        stringFailedLoadPage[0] += "<hr />";
                        stringFailingURL = failingUrl;
                        booleanFailedLoadURL = true;
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    view.loadDataWithBaseURL(null, stringFailedLoadPage[0], "text/html; charset=utf-8", "UTF-8",
                            null);
                } else {
                    try {
                        InputStream input;
                        AssetManager assetManager = getApplicationContext().getAssets();
                        input = assetManager.open("failedLoadPage");
                        BufferedReader f = new BufferedReader(new InputStreamReader(input));
                        String sor = f.readLine();
                        stringFailedLoadPage[1] = sor;
                        stringFailedLoadPage[1] += " " + description + " •</div><hr /><div style=\"text-align: center;\">Error code: • " + errorCode + " •</div>";
                        stringFailingURL = failingUrl;
                        booleanFailedLoadURL = true;
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    view.loadDataWithBaseURL(null, stringFailedLoadPage[1], "text/html; charset=utf-8", "UTF-8",
                            null);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressBarLoad.setVisibility(View.VISIBLE);
                progressBarLoad.setProgress(0);
                LoadURL(url);
                return false;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                progressBarLoad.setProgress(100);
                progressBarLoad.setVisibility(View.GONE);

                if (booleanClearHistory) {
                    booleanClearHistory = false;
                    webViewMain.clearHistory();
                    if (!webViewMain.canGoBack())
                        floatingActionButtonBACK.hide();
                    else floatingActionButtonBACK.show();
                    if (!webViewMain.canGoForward())
                        floatingActionButtonFORWARD.hide();
                    else floatingActionButtonFORWARD.show();
                } else {
                    if (!webViewMain.canGoBack())
                        floatingActionButtonBACK.hide();
                    else floatingActionButtonBACK.show();
                    if (!webViewMain.canGoForward())
                        floatingActionButtonFORWARD.hide();
                    else floatingActionButtonFORWARD.show();
                }
                if (booleanReloadSwipe) {
                    booleanReloadSwipe = false;
                    swipeRefreshLayout.setRefreshing(false);

                }
                String stringTitle = webViewMain.getTitle();
                try {
                    String titleTwo = "";
                    for (int i = 0; i < stringTitle.length(); i++) {
                        if (stringTitle.charAt(i) == '•') {
                            for (int j = i + 2; j < stringTitle.length(); j++) {
                                titleTwo += stringTitle.charAt(j);
                            }
                        }
                    }
                    getSupportActionBar().setTitle(titleTwo);
                    titleWebView = titleTwo;
                } catch (Exception e) {
                    parseError.sendError("Main.class", "setTitleError", "" + e, id);
                    getSupportActionBar().setTitle(stringTitle);
                    titleWebView = stringTitle;
                }
                if (booleanFailedLoadURL) {
                    String CurrentLanguage = Locale.getDefault().getDisplayLanguage();
                    if (CurrentLanguage.equals("magyar"))
                        getSupportActionBar().setTitle(getString(R.string.FailedLoadURL));
                    else
                        getSupportActionBar().setTitle(getString(R.string.FailedLoadURL));
                }
                super.onPageFinished(view, url);
            }

        });

        webViewMain.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBarLoad.setProgress(progress);
            }
        });

        if (savedInstanceState == null)
            if (!booleanLoadNewPost) {
                LoadURL(mainUrl);
            } else {
                LoadURL(newPostsUrl);
                booleanLoadNewPost = false;
            }
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        final boolean ParseEveryBody = getSharedPreferences("PARSE_EVERYBODY", MODE_PRIVATE).getBoolean("ParseEveryBody", false);
        if (!ParseEveryBody)
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
    }

    private void SetUpDrawer() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        String[] strings = getResources().getStringArray(R.array.drawer_list);
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
                    LoadURL(mainUrl);
                    booleanClearHistory = true;
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 2) {
                    Favourites();
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 3) {
                    LoadURL(newPostsUrl);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 4) {
                    LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unanswered");
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                if (editedPosition == 5) {
                    LoadURL("http://android-forum.hu/search.php?mobile=mobile&search_id=unreadposts");
                    mDrawerLayout.closeDrawer(mDrawerList);
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
                swipeRefreshLayout.setEnabled(false);
                floatingActionButtonBACK.hide();
                floatingActionButtonFORWARD.hide();
                if (slideOffset == 0.0) {
                    swipeRefreshLayout.setEnabled(true);
                    if (webViewMain.canGoForward())
                        floatingActionButtonFORWARD.show();
                    if (webViewMain.canGoBack())
                        floatingActionButtonBACK.show();
                }
            }

            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
                swipeRefreshLayout.setEnabled(true);
                getSupportActionBar().setTitle(titleWebView);
                booleanMenu = true;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
                syncState();
                swipeRefreshLayout.setEnabled(false);
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
    }

    private void Favourites() {
        final Dialog dialog = new Dialog(Main.this);
        dialog.setContentView(R.layout.activity_favourite_list);
        dialog.setTitle(R.string.title_activity_favourites);
        final ListView listView = (ListView) dialog.findViewById(R.id.favouritesListView);
        final TextView textView = (TextView) dialog.findViewById(R.id.favouritesTextView);
        final String string = getSharedPreferences("FAVOURITES", MODE_PRIVATE).getString("Favourites", "");
        Log.i("FIRST_GET", "" + string);
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
                    LoadURL(links[position]);
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
                                    Log.i("BEFORE_SAVE", "" + string);
                                    getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit().putString("Favourites", string).commit();
                                    dialogInterface.dismiss();
                                    final String stringTwo = getSharedPreferences("FAVOURITES", MODE_PRIVATE).getString("Favourites", "");
                                    if (!stringTwo.equals("")) {
                                        Log.i("FIRST_GET", "" + string);
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
        dialog.setCancelable(true);

        dialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webViewMain.canGoBack() && !mDrawerLayout.isDrawerOpen(mDrawerList)) {
            progressBarLoad.setVisibility(View.VISIBLE);
            progressBarLoad.setProgress(0);
            webViewMain.goBack();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && !webViewMain.canGoBack() && !mDrawerLayout.isDrawerOpen(mDrawerList)) {
            if (booleanOnKeyDown) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.SureExitTitle))
                        .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
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
                string += webViewMain.getUrl();
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
        if (key.equals("OnKeyDown")) {
            SharedPreferences OnkKeyDownPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            booleanOnKeyDown = OnkKeyDownPref.getBoolean("OnKeyDown", true);
        }
        if (key.equals("Screen")) {
            SharedPreferences ScreenPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean Screen = ScreenPref.getBoolean("Screen", false);
            if (Screen) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (key.equals("BasicZoom")) {
            SharedPreferences BasicZoomPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int BasicZoom = Integer.parseInt(BasicZoomPref.getString("BasicZoom", "2"));
            switch (BasicZoom) {
                case 1:
                    webViewMain.getSettings().setTextZoom(100);
                    break;
                case 2:
                    webViewMain.getSettings().setTextZoom(120);
                    break;
                case 3:
                    webViewMain.getSettings().setTextZoom(140);
                    break;
                case 4:
                    webViewMain.getSettings().setTextZoom(160);
                    break;
            }
        }
        if (key.equals("ZoomButton")) {
            SharedPreferences ZoomButtonPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean ZoomButton = ZoomButtonPref.getBoolean("ZoomButton", false);
            if (!ZoomButton) {
                Toast.makeText(this, getString(R.string.ToastRestartTheApplication), Toast.LENGTH_SHORT).show();
                webViewMain.getSettings().setDisplayZoomControls(false);
            } else
                webViewMain.getSettings().setDisplayZoomControls(true);
        }
        if (key.equals("Zoom")) {
            SharedPreferences ZoomPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean Zoom = ZoomPref.getBoolean("Zoom", false);
            if (Zoom)
                webViewMain.getSettings().setBuiltInZoomControls(true);
            else
                webViewMain.getSettings().setBuiltInZoomControls(false);
        }
        if (key.equals("Notification")) {
            SharedPreferences NotificationPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean Notification = NotificationPref.getBoolean("Notification", false);
            if (Notification) {
                Intent intent = new Intent();
                intent.setAction("startService");
                sendBroadcast(intent);
            }
            if (!Notification) {
                Intent intent = new Intent();
                intent.setAction("stopService");
                sendBroadcast(intent);
            }
        }
    }

    private void IsItFirstRun() {

        final boolean FirstRunUpdate = getSharedPreferences("FIRST_RUN_UPDATE", MODE_PRIVATE).getBoolean("FirstRunUpdate", true);
        if (FirstRunUpdate) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.FirstRunMainTitle))
                    .setMessage(getString(R.string.FirstRunMainMessage))
                    .setPositiveButton(getString(R.string.Accept), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), Settings.class);
                            i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Settings.FragmentSettingsRules.class.getName());
                            i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                            startActivity(i);
                            getSharedPreferences("FIRST_RUN_UPDATE", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("FirstRunUpdate", false)
                                    .commit();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(getString(R.string.Decline), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            onDestroy();
                        }
                    })
                    .show();
        }
        boolean FirstRunHelp = getSharedPreferences("FIRST_RUN_HELP", MODE_PRIVATE).getBoolean("FirstRunHelp", true);
        if (FirstRunHelp) {
            SpannableString email = new SpannableString(getString(R.string.FirstRunHelpMessage) +
                    "");
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            final AlertDialog Dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.FirstRunHelpTitle))
                    .setPositiveButton(R.string.Ok, null)
                    .setMessage(email)
                    .create();
            Dialog.show();
            ((TextView) Dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            getSharedPreferences("FIRST_RUN_HELP", MODE_PRIVATE)
                    .edit()
                    .putBoolean("FirstRunHelp", false)
                    .commit();
        }
    }

    private void SettingsWebView() {
        webViewMain.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewMain.getSettings().setLoadsImagesAutomatically(true);
        webViewMain.requestFocusFromTouch();
        webViewMain.getSettings().setJavaScriptEnabled(true);
        webViewMain.setFitsSystemWindows(true);
        SharedPreferences ZoomPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean Zoom = ZoomPref.getBoolean("Zoom", false);
        if (Zoom)
            webViewMain.getSettings().setBuiltInZoomControls(true);
        else
            webViewMain.getSettings().setBuiltInZoomControls(false);
        SharedPreferences ZoomButtonPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean ZoomButton = ZoomButtonPref.getBoolean("ZoomButton", false);
        if (!ZoomButton)
            webViewMain.getSettings().setDisplayZoomControls(false);
        else
            webViewMain.getSettings().setDisplayZoomControls(true);
        SharedPreferences BasicZoomPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int BasicZoom = Integer.parseInt(BasicZoomPref.getString("BasicZoom", "100"));
        switch (BasicZoom) {
            case 1:
                webViewMain.getSettings().setTextZoom(100);
                break;
            case 2:
                webViewMain.getSettings().setTextZoom(120);
                break;
            case 3:
                webViewMain.getSettings().setTextZoom(140);
                break;
            case 4:
                webViewMain.getSettings().setTextZoom(160);
                break;

        }
    }

    private void SettingScreen() {
        SharedPreferences OnkKeyDownPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        booleanOnKeyDown = OnkKeyDownPref.getBoolean("OnKeyDown", true);
        SharedPreferences ScreenPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean Screen = ScreenPref.getBoolean("Screen", false);
        if (Screen)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void LoadURL(String aURL) {
        progressBarLoad.setVisibility(View.VISIBLE);
        progressBarLoad.setProgress(0);
        webViewMain.loadUrl(aURL);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            webViewMain.saveState(outState);
        } catch (Exception e) {
            parseError.sendError("Main.class", "onSaveInstanceState", "" + e, id);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
            webViewMain.restoreState(savedInstanceState);
        } catch (Exception e) {
            parseError.sendError("Main.class", "onRestoreInstanceState", "" + e, id);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onRefresh() {
        if (booleanFailedLoadURL) {
            LoadURL(stringFailingURL);
            booleanFailedLoadURL = false;
            booleanReloadSwipe = true;
        } else {
            LoadURL(webViewMain.getUrl());
            booleanReloadSwipe = true;
        }
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

}
