package com.spx.exoplayertest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sogo.exoplayer.LdExoPlayerController;
import com.sogo.exoplayer.VUtil;

public class VideoPlayActivity extends Activity {
    private static final String TAG = "VideoPlayActivity";
    private static final long ANIMATION_DURATION = 300;

    private int mVideoId;
    private String mVideoUrl = null;
    private View playView;
    private LdExoPlayerController controller;
    private View mRootView;

    private int viewX, viewY, viewW, viewH;
    private ValueAnimator animatorStart = new ValueAnimator();
    private ValueAnimator animatorEnd = new ValueAnimator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        mRootView = findViewById(R.id.rootview);

        mVideoId = getIntent().getIntExtra("play_id", 0);
        mVideoUrl = getIntent().getStringExtra("play_url");

        viewX = getIntent().getIntExtra("view_x", 0);
        viewY = getIntent().getIntExtra("view_y", 0);
        viewW = getIntent().getIntExtra("view_w", 0);
        viewH = getIntent().getIntExtra("view_h", 0);

        Log.d(TAG, "onCreate: url:" + mVideoUrl);
        Log.d(TAG, "onCreate: viewX:" + viewX + ", viewY:" + viewY + ", viewW:" + viewW + ", viewH:" + viewH);

        playView = findViewById(R.id.player_area);
        controller = new LdExoPlayerController(playView);
        controller.bindData(this, mVideoId, "", mVideoUrl, "");
        controller.initPlayer(false);

        controller.transformIn(mVideoUrl);

        initTransform();
        transformIn();
    }

    private void transformIn() {
        animatorStart.start();
    }

    private void transformOut() {
        animatorEnd.start();
    }

    private void initTransform() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;

        int playerViewHeight = getResources().getDimensionPixelSize(R.dimen.player_view_height);
        int statusBarHeight = VUtil.getStatusBarHeight(this);
        Log.d(TAG, "onCreate: statusBarHeight:" + statusBarHeight);
        viewY = viewY - statusBarHeight;

        {
            animatorStart.setDuration(ANIMATION_DURATION);
            animatorStart.setInterpolator(new AccelerateDecelerateInterpolator());

            PropertyValuesHolder wHolder = PropertyValuesHolder.ofInt("width", viewW, widthPixels);
            PropertyValuesHolder hHolder = PropertyValuesHolder.ofInt("height", viewH, playerViewHeight);
            PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("transactionX", viewX, 0);
            PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("transactionY", viewY, 0);
            animatorStart.setValues(wHolder, hHolder, xHolder, yHolder);

            animatorStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int width = (Integer) animation.getAnimatedValue("width");
                    int height = (Integer) animation.getAnimatedValue("height");
                    float transactionX = (float) animation.getAnimatedValue("transactionX");
                    float transactionY = (float) animation.getAnimatedValue("transactionY");

                    ViewGroup.LayoutParams layoutParams = playView.getLayoutParams();
                    layoutParams.width = width;
                    layoutParams.height = height;
                    playView.setLayoutParams(layoutParams);

                    playView.setTranslationX(transactionX);
                    playView.setTranslationY(transactionY);

                    float animatedFraction = animation.getAnimatedFraction();
                    float alpha = animatedFraction * 0.8f;
                    Log.d(TAG, "onAnimationUpdate: alpha:" + alpha);
                    mRootView.setBackgroundColor(getColorWithAlpha(alpha, Color.BLACK));
                }
            });
        }


        {
            animatorEnd.setDuration(ANIMATION_DURATION);
            animatorEnd.setInterpolator(new AccelerateDecelerateInterpolator());

            PropertyValuesHolder wHolder = PropertyValuesHolder.ofInt("width", widthPixels, viewW);
            PropertyValuesHolder hHolder = PropertyValuesHolder.ofInt("height", playerViewHeight, viewH);
            PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("transactionX", 0, viewX);
            PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("transactionY", 0, viewY);
            animatorEnd.setValues(wHolder, hHolder, xHolder, yHolder);

            animatorEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int width = (Integer) animation.getAnimatedValue("width");
                    int height = (Integer) animation.getAnimatedValue("height");
                    float transactionX = (float) animation.getAnimatedValue("transactionX");
                    float transactionY = (float) animation.getAnimatedValue("transactionY");

                    ViewGroup.LayoutParams layoutParams = playView.getLayoutParams();
                    layoutParams.width = width;
                    layoutParams.height = height;
                    playView.setLayoutParams(layoutParams);

                    playView.setTranslationX(transactionX);
                    playView.setTranslationY(transactionY);

                    float animatedFraction = animation.getAnimatedFraction();
                    float alpha = (1 - animatedFraction) * 0.8f;
                    Log.d(TAG, "onAnimationUpdate: alpha:" + alpha);
                    mRootView.setBackgroundColor(getColorWithAlpha(alpha, Color.BLACK));
                }
            });

            animatorEnd.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    exit();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onViewAttachedToWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onViewDetachedFromWindow();
    }

    @Override
    public void onBackPressed() {
        transformOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.release();
    }

    /**
     * 关闭页面
     */
    public void exit() {
        controller.transformOut();
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }
}
