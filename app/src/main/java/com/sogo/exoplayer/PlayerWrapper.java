package com.sogo.exoplayer;


import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.spx.exoplayertest.R;

import java.io.File;
import java.io.IOException;


/**
 * Manages the {@link ExoPlayer}, the IMA plugin and all video playback.
 */
public final class PlayerWrapper {

    private static final String TAG = "Player.Wrapper";
    private static PlayerWrapper instance = new PlayerWrapper();

    public static PlayerWrapper getInstance() {
        return instance;
    }

//    private final ImaAdsLoader adsLoader;

    private SimpleExoPlayer player;
    private SimpleExoPlayerView simpleExoPlayerView;
    private long contentPosition;
    private boolean playFinished = false;
    private boolean isPlaying = false;

    private TrackSelector mTrackSelector;
    private String mVideoUrl;
    private Player.EventListener mEventListener;
    private VideoListener mVideoListener;


    private DataSource.Factory dataSourceFactory = null;
    private ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

    private File cacheFile = null;

    SimpleCache simpleCache = null;
    DataSource.Factory cachedDataSourceFactory = null;
    // This is the MediaSource representing the content media (i.e. not the ad).
//        String contentUrl = context.getString(R.string.content_url);
//        MediaSource contentMediaSource = new ExtractorMediaSource(
//                Uri.parse(contentUrl), dataSourceFactory, extractorsFactory, null, null);

    // Compose the content media source into a new AdsMediaSource with both ads and content.
//        MediaSource mediaSourceWithAds = new AdsMediaSource(contentMediaSource, dataSourceFactory,
//                adsLoader, simpleExoPlayerView.getOverlayFrameLayout());


    // This is the MediaSource representing the media to be played.
    MediaSource videoSource = null;

    private PlayerWrapper() {
        dataSourceFactory = new DefaultDataSourceFactory(VUtil.getApplication(),
                Util.getUserAgent(VUtil.getApplication(), VUtil.getApplication().getString(R.string.app_name)));
        cacheFile = new File(VUtil.getApplication().getExternalCacheDir().getAbsolutePath(), "video");
        VLog.d(TAG, "PlayerWrapper()  cache file:" + cacheFile.getAbsolutePath());
        simpleCache = new SimpleCache(cacheFile, new LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024));
        cachedDataSourceFactory = new CacheDataSourceFactory(simpleCache, dataSourceFactory);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

    }

    public void preload(String videoUri) {
        DataSpec dataSpec = new DataSpec(Uri.parse(videoUri), 0, 512 * 1024, null);
        CacheUtil.CachingCounters counters = new CacheUtil.CachingCounters();
        try {
            CacheUtil.cache(dataSpec, simpleCache, dataSourceFactory.createDataSource(), counters, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init(Context context, SimpleExoPlayerView simpleExoPlayerView, String videoUri,
                     Player.EventListener eventListener, SimpleExoPlayer.VideoListener videoListener,
                     boolean userAction) {
        this.mVideoUrl = videoUri;
        this.mEventListener = eventListener;
        this.mVideoListener = videoListener;
        initPlayer(context, simpleExoPlayerView, userAction);
    }

    private void initPlayer(Context context, SimpleExoPlayerView simpleExoPlayerView, boolean userAction) {
        // Create a default track selector.
        VLog.d(TAG, "initPlayer: ... " + userAction);
        this.simpleExoPlayerView = simpleExoPlayerView;
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(context),
                mTrackSelector, new LdDefaultLoadControl(userAction));

        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.

//        DataSource.Factory dataSourceFactory = new OkHttpDataSourceFactory();
        // Produces Extractor instances for parsing the content media (i.e. not the ad).


        // Prepare the player with the source.
        isPlaying = false;
//        player.seekTo(contentPosition);

        new Thread(new Runnable() {
            @Override
            public void run() {
                videoSource = new ExtractorMediaSource(Uri.parse(mVideoUrl),
                        cachedDataSourceFactory, extractorsFactory, null, null);
                player.prepare(videoSource);
            }
        }).start();

//        player.setPlayWhenReady(true);

        player.addListener(mEventListener);
        player.addVideoListener(mVideoListener);
        hasReleased = false;
    }

    public void transformIn(SimpleExoPlayerView newSimpleExoPlayerView){
        newSimpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setPlayer(null);
    }
    public void transformOut(SimpleExoPlayerView newSimpleExoPlayerView){
        simpleExoPlayerView.setPlayer(player);
        newSimpleExoPlayerView.setPlayer(null);
    }

    public void onPlayFinished() {
        VLog.d(TAG, "onPlayFinished: ...");
        playFinished = true;
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void play() {
        VLog.d(TAG, "play: ...");
        if (playFinished) {
            player.seekTo(0);
            playFinished = false;
        }
        player.setPlayWhenReady(true);

        if (player.getPlaybackState() == Player.STATE_IDLE) {
            VLog.d(TAG, "player is IDLE!!!");
        }

        isPlaying = true;
        hasReleased = false;
    }

    public boolean isIdle() {
        if (player != null) {
            if (player.getPlaybackState() == Player.STATE_IDLE) {
                return true;
            }
        }
        return false;
    }

    public void pause() {
        VLog.d(TAG, "pause: ...");
        if (player != null) {
            isPlaying = false;
            player.stop();
        }
    }

    private boolean hasReleased = false;

    public void release() {
        VLog.d(TAG, "release: ..."+this);
        if (player != null) {
            isPlaying = false;
            player.removeListener(mEventListener);
            player.removeVideoListener(mVideoListener);
            player.release();
            hasReleased = true;
            VLog.d(TAG, "release: ...hasReleased!");
//            player = null;
        }
//        adsLoader.release();
    }

    public String getPlayUrl() {
        return mVideoUrl;
    }

    public boolean isReleased() {
        return hasReleased;
    }
}
