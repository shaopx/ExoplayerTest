package com.spx.exoplayertest;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sogo.exoplayer.LdExoPlayerController;
import com.sogo.exoplayer.VUtil;

public class VideoPlayActivity extends Activity {
    private static final String TAG = "VideoPlayActivity";

    private int mVideoId;
    private String mVideoUrl = null;
    private View playView;
    private LdExoPlayerController controller;

    private int viewX, viewY, viewW, viewH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        mVideoId = getIntent().getIntExtra("play_id", 0);
        mVideoUrl = getIntent().getStringExtra("play_url");

        viewX = getIntent().getIntExtra("view_x", 0);
        viewY = getIntent().getIntExtra("view_y", 0);
        viewW = getIntent().getIntExtra("view_w", 0);
        viewH = getIntent().getIntExtra("view_h", 0);

        Log.d(TAG, "onCreate: url:" + mVideoUrl);
        Log.d(TAG, "onCreate: viewX:" + viewX+", viewY:"+viewY+", viewW:"+viewW+", viewH:"+viewH);

        playView = findViewById(R.id.player_area);
        controller = new LdExoPlayerController(playView);
        controller.bindData(this, mVideoId, "", mVideoUrl, "");
        controller.initPlayer(false);


//        controller.start(false);
        controller.transform(mVideoUrl);



//        ObjectAnimator widthAnimator = ObjectAnimator.ofFloat(playView, "translationY", 0, 800);
//        widthAnimator.setDuration(1000);
//        widthAnimator.addUpdateListener(new An);
//        widthAnimator.start();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;

        int playerViewHeight = getResources().getDimensionPixelSize(R.dimen.player_view_height);
        int statusBarHeight = VUtil.getStatusBarHeight(this);
        Log.d(TAG, "onCreate: statusBarHeight:"+statusBarHeight);
        viewY = viewY-statusBarHeight;

        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        PropertyValuesHolder wHolder = PropertyValuesHolder.ofInt("width", viewW, widthPixels);
        PropertyValuesHolder hHolder = PropertyValuesHolder.ofInt("height", viewH, playerViewHeight);
        PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("transactionX", viewX, 0);
        PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("transactionY", viewY, 0);
        animator.setValues(wHolder, hHolder, xHolder,yHolder);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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
            }
        });

        animator.start();
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
    protected void onDestroy() {
        super.onDestroy();
        controller.release();
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
}
