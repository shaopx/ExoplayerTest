package com.sogo.exoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.spx.exoplayertest.R;


import java.io.IOException;

/**
 * Created by shaopengxiang on 2017/12/18.
 */

public class LdExoPlayerController implements PlayerManager.PlayerHolder {

    private static final String TAG = "Player.Controller";

    private Context mContext;
    private int mId;
    private String mContent;
    private String mVideoUrl;
    private String mCoverImgUrl;


    private SimpleExoPlayerView mPlayerView;
    private PlayerWrapper mPlayer;
    private ImageView mCoverIv;
    private ImageView mStartPlayIv;

    private ProgressBar mProgressBar;

    private long mOnClickTime = 0L;

    public LdExoPlayerController(View itemView) {
        mPlayerView = itemView.findViewById(R.id.player_view);
        mPlayer = PlayerManager.getInstance().getPlayer(this);
        mCoverIv = itemView.findViewById(R.id.video_cover_iv);
        mStartPlayIv = itemView.findViewById(R.id.video_startplay_iv);
        mProgressBar = itemView.findViewById(R.id.progressBar);

        if (mCoverIv != null) {
            mCoverIv.setOnClickListener(clickListener);
        }

        mStartPlayIv.setOnClickListener(clickListener);
    }

    public void bindData(Context context, int id, String content, String videoUrl, String coverImgUrl) {
        this.mContext = context;
        this.mId = id;
        this.mContent = content;
        this.mVideoUrl = videoUrl;
        this.mCoverImgUrl = coverImgUrl;
        VLog.e(TAG, "bindData[" + id + "]: content:" + content);
//        Log.d(TAG, "bindData["+position+"]: video url:"+card.video.getVideoUrl());

//        ImageUtils.showImage(context, mCoverIv, mCoverImgUrl);

        PlayerManager.getInstance().preAttch(videoUrl, this);

        initViews();
    }

    public void transformIn(String videoUrl){
        PlayerManager instance = PlayerManager.getInstance();
        PlayerWrapper player = instance.getPlayer(null);
        player.transformIn(mPlayerView);
    }

    public void transformOut(){
        PlayerManager instance = PlayerManager.getInstance();
        PlayerWrapper player = instance.getPlayer(null);
        player.transformOut(mPlayerView);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VLog.d(TAG, "mStartPlayIv onClick: ...");
            start(true);
        }
    };

    public void start(boolean userAction) {
        mOnClickTime = System.currentTimeMillis();
        mStartPlayIv.setVisibility(View.GONE);
        // 点击时初始化player
        initPlayer(userAction);
        mPlayer.play();
    }


    private SimpleExoPlayer.VideoListener videoListener = new SimpleExoPlayer.VideoListener() {
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            VLog.d(TAG, "onVideoSizeChanged: ...width:" + width + ", height:" + height
                    + ", pixelWidthHeightRatio:" + pixelWidthHeightRatio);
        }

        @Override
        public void onRenderedFirstFrame() {
            VLog.d(TAG, "onRenderedFirstFrame: ...");
            long playingTime = System.currentTimeMillis();
            long useTime = playingTime - mOnClickTime;
            if (useTime < 100000) {
                VLog.d(TAG, "播放: use:" + (useTime) + "ms");
                showToast("播放: 耗时:" + (useTime) + "ms");
            }

        }
    };

    private Player.EventListener listener = new Player.EventListener() {
//        @Override
//        public void onTimelineChanged(Timeline timeline, Object manifest) {
//            VLog.d(TAG, "onTimelineChanged: timeline:" + timeline.toString());
//        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            VLog.d(TAG, "onLoadingChanged content:" + mContent);
            VLog.d(TAG, "onLoadingChanged: isLoading:" + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            VLog.d(TAG, "onPlayerStateChanged content:" + mContent);
            VLog.d(TAG, "onPlayerStateChanged: playWhenReady:" + playWhenReady + ", playbackState:" + playbackState);
            mProgressBar.setVisibility(View.GONE);
            if (playbackState == Player.STATE_BUFFERING) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            // 开始播放
            if (playbackState == Player.STATE_READY) {

                if (playWhenReady) {
                    mCoverIv.setVisibility(View.GONE);
                }
            }

            if (playbackState == Player.STATE_ENDED) {
                mPlayer.onPlayFinished();
                mStartPlayIv.setImageResource(R.drawable.ic_replay_normal);
                mStartPlayIv.setVisibility(View.VISIBLE);
//                mCoverIv.setAlpha(1f);
                mCoverIv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            VLog.d(TAG, "onPlayerError content:" + mContent);
            VLog.d(TAG, "onPlayerError mId:" + mId);
            VLog.e(TAG, "onPlayerError: ", error);
            IOException sourceException = error.getSourceException();
            if (sourceException != null && sourceException.getClass().getName().contains("InvalidResponseCodeException")) {
                Toast.makeText(mContext, "这个视频403了, 请一会重试", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            VLog.d(TAG, "onLoadingChanged content:" + mContent);
            VLog.d(TAG, "onPositionDiscontinuity: reason:" + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

    private void showToast(final String s) {
        mPlayerView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViews() {
        mProgressBar.setVisibility(View.GONE);
        mStartPlayIv.setImageResource(R.drawable.ic_play_video_normal);
        mStartPlayIv.setVisibility(View.VISIBLE);
        mCoverIv.setAlpha(1f);
        mCoverIv.setVisibility(View.VISIBLE);
    }

    public void initPlayer(boolean userAction) {
        if (!TextUtils.isEmpty(mVideoUrl)) {

            String newUrl = VideoUrlCache.getVideoUrl(mId);
            if (!TextUtils.isEmpty(newUrl)) {
                mVideoUrl = newUrl;
            }

            PlayerManager.getInstance().attchPlayer(mContext, this, mPlayerView,
                    mVideoUrl, listener, videoListener, userAction);

        }
    }

    public void resetUi() {
        initViews();
    }

    public void onViewAttachedToWindow() {
        VLog.d(TAG, "onViewAttachedToWindow content:" + mContent + ", articleId:" + mId);
        PlayerManager.getInstance().preAttch(mVideoUrl, this);
        resetUi();
    }


    public void onViewDetachedFromWindow() {
        VLog.d(TAG, "onViewDetachedFromWindow content:" + mContent + ", articleId:" + mId);
        VLog.d(TAG, "onViewDetachedFromWindow: ...videoUrl:" + mVideoUrl);
        PlayerManager.getInstance().detachPlayer(this, mVideoUrl);
    }

    public void release(){
        VLog.d(TAG, "release content:" + mContent + ", articleId:" + mId);
        PlayerManager.getInstance().release(mVideoUrl);
    }

    @Override
    public void onDetached() {
        // 跟player分离后, 恢复为默认卡片显示
        resetUi();
    }

    @Override
    public void preload() {
        initPlayer(false);
    }
}
