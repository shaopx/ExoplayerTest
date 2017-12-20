package com.spx.exoplayertest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sogo.exoplayer.LdExoPlayerController;

public class VideoPlayActivity extends Activity {
    private static final String TAG = "VideoPlayActivity";

    private int mVideoId;
    private String mVideoUrl = null;
    private View playView;
    private LdExoPlayerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        mVideoId = getIntent().getIntExtra("play_id", 0);
        mVideoUrl = getIntent().getStringExtra("play_url");
        Log.d(TAG, "onCreate: url:" + mVideoUrl);

        playView = findViewById(R.id.player_area);
        controller = new LdExoPlayerController(playView);
        controller.bindData(this, mVideoId, "", mVideoUrl, "");
        controller.initPlayer(false);
        controller.start(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
