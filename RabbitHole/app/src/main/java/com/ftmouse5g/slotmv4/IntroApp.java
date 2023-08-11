package com.ftmouse5g.slotmv4;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;

public class IntroApp extends AppCompatActivity {

    private String mURL;
    private Integer mBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setFlags(1024, 1024);

        mURL = getIntent().getStringExtra("mURL");
        mBtn = getIntent().getIntExtra("mBtn", 0);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setupVideoView();
        setupLaunchGameButton();
    }

    private void setupVideoView() {
        VideoView splashVideo = findViewById(R.id.videoView);
        Uri splashFile = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_logo);
        splashVideo.setVideoURI(splashFile);
        splashVideo.start();
        splashVideo.setOnCompletionListener(mediaPlayer -> {
            splashVideo.stopPlayback();
            splashVideo.setVisibility(View.GONE);

            ConstraintLayout uiLayout = findViewById(R.id.uiContainer);
            uiLayout.setVisibility(View.VISIBLE);
        });
    }

    private void setupLaunchGameButton() {
        ImageButton launchGame = findViewById(R.id.imageButton);

        ObjectAnimator scaleXa = ObjectAnimator.ofFloat(launchGame, "scaleX", 1.0f, 0.8f);
        scaleXa.setRepeatMode(ValueAnimator.REVERSE);
        scaleXa.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator scaleYa = ObjectAnimator.ofFloat(launchGame, "scaleY", 1.0f, 0.8f);
        scaleYa.setRepeatMode(ValueAnimator.REVERSE);
        scaleYa.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animatorSeta = new AnimatorSet();
        animatorSeta.playTogether(scaleXa, scaleYa);
        animatorSeta.setDuration(500);
        animatorSeta.start();

        launchGame.setOnClickListener(view -> {
            if (mBtn == 0) {
                openCustomTab(mURL);
            } else {
                launchGamePlayActivity();
            }
        });
    }

    private void openCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder
                .setShowTitle(true)
                .setUrlBarHidingEnabled(true)
                .setInitialActivityHeightPx(250, CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE)
                .build();

        if (chromeInstalled()) {
            customTabsIntent.intent.setPackage("com.android.chrome");
        }

        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private void launchGamePlayActivity() {
        Intent i = new Intent(this, GamePlay.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("mURL", mURL);
        i.putExtra("mBtn", mBtn);
        startActivity(i);
        finish();
    }
    private boolean chromeInstalled(){
        try{
            getPackageManager().getPackageInfo("com.android.chrome", 0);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
