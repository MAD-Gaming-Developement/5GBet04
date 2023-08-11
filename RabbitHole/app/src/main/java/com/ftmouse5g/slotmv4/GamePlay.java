package com.ftmouse5g.slotmv4;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GamePlay extends AppCompatActivity {

    WebView webView;
    BottomNavigationView bottomNav;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setFlags(1024, 1024);

        setupFullScreenMode();

        String mURL = getIntent().getStringExtra("mURL");
        Integer mBtn = getIntent().getIntExtra("mBtn", 0);

        setupActionBar();

        webView = findViewById(R.id.webView);
        bottomNav = findViewById(R.id.bottom_navigation);

        if (mBtn == 1) {
            setupBottomNav();
        }

        loadWeb(webView, mURL, mBtn);
    }

    private void setupFullScreenMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void setupBottomNav() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                webView.reload();
                return true;
            } else if (item.getItemId() == R.id.navigation_policy) {
                openPolicyUrl();
                return true;
            } else if (item.getItemId() == R.id.navigation_share) {
                shareAppFB();
                return true;
            } else if (item.getItemId() == R.id.navigation_quit) {
                finishAndRemoveTask();
            }
            return false;
        });
    }

    private void openPolicyUrl() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder
                .setShowTitle(true)
                .setInitialActivityHeightPx(250).build();
        if (chromeInstalled()) {
            customTabsIntent.intent.setPackage("com.android.chrome");
        }
        customTabsIntent.launchUrl(this, Uri.parse("https://5gbgaming.site/policy/?appid=5GB04"));
    }

    private void loadWeb(WebView wApp, String mURL, Integer mBtn) {
        if (mBtn == 0) {
            wApp.addJavascriptInterface(new jsBridge(this), jsBridge.TAG);
        }

        wApp.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                return true;
            }
        });

        WebSettings settings = wApp.getSettings();
        settings.setLoadsImagesAutomatically(true);
        wApp.setBackgroundColor(Color.rgb(0, 18, 39));
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        wApp.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 11; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");

        wApp.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                bottomNav.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> view.evaluateJavascript(
                        "(function() { " +
                                "   if(document.getElementById('pngPreloaderWrapper')) {" +
                                "       document.getElementById('pngPreloaderWrapper').removeChild(document.getElementById('pngLogoWrapper')); " +
                                "   }" +
                                "})();",
                        html -> wApp.setVisibility(View.VISIBLE)), 600);

                new Handler().postDelayed(() -> view.evaluateJavascript(
                        "(function() { " +
                                "   var myHome = document.getElementById('lobbyButtonWrapper'); " +
                                "   if(document.getElementById('lobbyButtonWrapper')) {" +
                                "       document.getElementById('lobbyButtonWrapper').style = 'display:none;';" +
                                "   }" +
                                "})();",
                        html -> wApp.setVisibility(View.VISIBLE)), 5000);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        wApp.loadUrl(mURL);
    }

    private boolean chromeInstalled(){
        try{
            getPackageManager().getPackageInfo("com.android.chrome", 0);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void shareApp() {
        try {
            String packageName = getPackageName();
            Uri appUri = Uri.parse("https://play.google.com/store/apps/details")
                    .buildUpon()
                    .appendQueryParameter("id", packageName)
                    .build();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, appUri.toString());

            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareAppFB() {
        String packageName = getPackageName();
        String appUrl = "https://play.google.com/store/apps/details?id=" + packageName;

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(appUrl))
                .build();

        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            public void onSuccess(Sharer.Result result) {

            }


            public void onCancel() {

            }


            public void onError(@NonNull FacebookException e) {
                shareApp();
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }
    }


    protected void onPause() {
        super.onPause();
    }


    protected void onResume() {
        super.onResume();
    }
}
