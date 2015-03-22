package hu.zokni1996.android_forum.Main.Web;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import hu.zokni1996.android_forum.Main.Main;
import hu.zokni1996.android_forum.Parse.ParseError;
import hu.zokni1996.android_forum.R;

public class WebFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private WebView webViewMain;
    private ProgressBar progressBarLoad;
    private ParseError parseError = new ParseError();
    private String[] stringFailedLoadPage = new String[2];
    public boolean booleanClearHistory = true;
    private String stringFailingURL = "";
    private boolean booleanFailedLoadURL = false;
    private boolean booleanReloadSwipe = false;
    private FloatingActionButton floatingActionButtonBACK;
    private FloatingActionButton floatingActionButtonFORWARD;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String titleWebView = "";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_web, container, false);
        InitializeParse();
        webViewMain = (WebView) view.findViewById(R.id.WebViewMain);
        floatingActionButtonBACK = (FloatingActionButton) view.findViewById(R.id.WebViewBack);
        floatingActionButtonFORWARD = (FloatingActionButton) view.findViewById(R.id.WebViewForward);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeContainer);
        progressBarLoad = new ProgressBar(getActivity().getBaseContext(), null, android.R.attr.progressBarStyleHorizontal);
        getSettings(PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()));
        FloatingActionButtonSETUP();
        ProgressBarSETUP();
        webViewMain.setWebViewClient(new WebViewClient() {
            //TESTING!!!!
            @Override
            public void onReceivedHttpAuthRequest(final WebView view, @NonNull final HttpAuthHandler handler, final String host, final String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                final Dialog dialog = new Dialog(getActivity().getBaseContext());
                dialog.setTitle(getString(R.string.ParseLogIn));
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.main_on_received_http_auth_request);
                final Button button = (Button) dialog.findViewById(R.id.buttonHttpAuthRequest);
                final EditText editTextUsername = (EditText) dialog.findViewById(R.id.editTextHttpAuthRequestUsername);
                final EditText editTextPassword = (EditText) dialog.findViewById(R.id.editTextHttpAuthRequestPassword);
                final TextView textView = (TextView) dialog.findViewById(R.id.textViewHttpAuthRequestTitle);
                textView.setText("The " + host + " need to login for the following website: " + view.getUrl());
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (editTextPassword.getText().toString().length() > 0 &&
                                editTextUsername.getText().toString().length() > 0)
                            button.setEnabled(true);
                        else button.setEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (editTextPassword.getText().toString().length() > 0 &&
                                editTextUsername.getText().toString().length() > 0)
                            button.setEnabled(true);
                        else button.setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (editTextPassword.getText().toString().length() > 0 &&
                                editTextUsername.getText().toString().length() > 0)
                            button.setEnabled(true);
                        else button.setEnabled(false);
                    }
                };
                editTextPassword.addTextChangedListener(textWatcher);
                editTextUsername.addTextChangedListener(textWatcher);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            handler.proceed(editTextUsername.getText().toString(), editTextPassword.getText().toString());
                            handler.useHttpAuthUsernamePassword();
                            dialog.dismiss();
                        } catch (Exception e) {
                            parseError.sendError("Main.java", "AUTH_USERNAME_PASSWORD", "" + e, e.getCause().toString(), e.getMessage());
                        }
                    }
                });
                dialog.show();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                String CurrentLanguage = Locale.getDefault().getDisplayLanguage();

                if (CurrentLanguage.equals("magyar")) {
                    try {
                        InputStream input;
                        AssetManager assetManager = getActivity().getBaseContext().getAssets();
                        input = assetManager.open("failedLoadPage");
                        BufferedReader f = new BufferedReader(new InputStreamReader(input));
                        String sor = f.readLine();
                        stringFailedLoadPage[0] = sor;
                        stringFailedLoadPage[0] += " " + description + getString(R.string.web_error_code) + errorCode + getString(R.string.web_error_code_2);
                        stringFailedLoadPage[0] += "<hr />";
                        stringFailingURL = failingUrl;
                        booleanFailedLoadURL = true;
                        input.close();
                    } catch (IOException e) {
                        parseError.sendError("Main.java", "OnReceivedError", "" + e, e.getCause().toString(), e.getMessage());
                    }
                    view.loadDataWithBaseURL(null, stringFailedLoadPage[0], "text/html; charset=utf-8", "UTF-8",
                            null);
                } else {
                    try {
                        InputStream input;
                        AssetManager assetManager = getActivity().getBaseContext().getAssets();
                        input = assetManager.open("failedLoadPage");
                        BufferedReader f = new BufferedReader(new InputStreamReader(input));
                        String sor = f.readLine();
                        stringFailedLoadPage[1] = sor;
                        stringFailedLoadPage[1] += " " + description + getString(R.string.web_error_code) + errorCode + getString(R.string.web_error_code_2);
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
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBarLoad.setVisibility(View.VISIBLE);
                progressBarLoad.setProgress(1);
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
                        if (stringTitle.charAt(i) == '\u2022' && stringTitle.length() >= i + 2) {
                            for (int j = i + 2; j < stringTitle.length(); j++) {
                                titleTwo += stringTitle.charAt(j);
                            }
                        }
                    }
                    Main.setActionBarTitle(titleTwo);
                    titleWebView = titleTwo;
                } catch (Exception e) {
                    Main.setActionBarTitle(stringTitle);
                    parseError.sendError("Main.class", "setTitleError", "" + e, e.getCause().toString(), e.getMessage());
                    titleWebView = stringTitle;
                }
                if (booleanFailedLoadURL) {
                    String CurrentLanguage = Locale.getDefault().getDisplayLanguage();
                    if (CurrentLanguage.equals("magyar"))
                        Main.setActionBarTitle(getString(R.string.FailedLoadURL));
                    else
                        Main.setActionBarTitle(getString(R.string.FailedLoadURL));
                }
                super.onPageFinished(view, url);
            }

        });
        webViewMain.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBarLoad.setVisibility(ProgressBar.VISIBLE);
                }
                progressBarLoad.setProgress(progress);
                if (progress == 100) {
                    progressBarLoad.setVisibility(ProgressBar.GONE);
                }
            }
        });
        String mainUrl = "http://android-forum.hu/index.php?mobile=mobile";
        if (savedInstanceState == null)
            LoadURL(mainUrl);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.green, R.color.red);
        return view;
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

    private void getSettings(SharedPreferences sharedPreferences) {
        webViewMain.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewMain.getSettings().setLoadsImagesAutomatically(true);
        webViewMain.getSettings().setLoadWithOverviewMode(true);
        webViewMain.getSettings().setUseWideViewPort(true);
        webViewMain.requestFocusFromTouch();
        webViewMain.getSettings().setJavaScriptEnabled(true);
        webViewMain.setFitsSystemWindows(true);
        webViewMain.getSettings().setDisplayZoomControls(false);
        webViewMain.getSettings().setBuiltInZoomControls(sharedPreferences.getBoolean("Zoom", false));
        webViewMain.getSettings().setTextZoom(Integer.parseInt(sharedPreferences.getString("BasicZoom", "100")));

    }

    public void LoadURL(String aURL) {
        progressBarLoad.setVisibility(View.VISIBLE);
        progressBarLoad.setProgress(0);
        webViewMain.loadUrl(aURL);
    }

    public void ProgressBarSETUP() {
        progressBarLoad.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 24));
        progressBarLoad.setProgress(100);
        progressBarLoad.setProgressDrawable(getResources().getDrawable(R.drawable.progress_horizontal_holo_light));
        FrameLayout decorView = (FrameLayout) getActivity().getWindow().getDecorView();
        decorView.addView(progressBarLoad);

        ViewTreeObserver observer = progressBarLoad.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                progressBarLoad.setY(Main.getActionBarSize() + (float) Math.ceil(25 * getResources().getDisplayMetrics().density) - 10);

                ViewTreeObserver observer = progressBarLoad.getViewTreeObserver();
                observer.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void FloatingActionButtonSETUP() {
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
    }

    private void InitializeParse() {
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Parse.setLogLevel(Parse.LOG_LEVEL_INFO);
        if (!getActivity().getSharedPreferences("PARSE_EVERYBODY", Context.MODE_PRIVATE).getBoolean("ParseEveryBody", false))
            ParsePush.subscribeInBackground("everyBody", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        getActivity().getSharedPreferences("PARSE_EVERYBODY", Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean("ParseEveryBody", true)
                                .commit();
                    } else {
                        getActivity().getSharedPreferences("PARSE_EVERYBODY", Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean("ParseEveryBody", false)
                                .commit();
                    }
                }
            });
    }

    public String setTitleWebView() {
        return titleWebView;
    }

    public ProgressBar getProgressBarLoad() {
        return progressBarLoad;
    }

    public WebView getWebViewMain() {
        return webViewMain;
    }


    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webViewMain.saveState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        webViewMain.restoreState(savedInstanceState);
    }
}

