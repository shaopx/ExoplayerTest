package com.sogo.exoplayer;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaopengxiang on 2017/12/18.
 */

public class PlayerManager {
    private static final String TAG = "PlayerManager";
    private static final boolean USE_PRELOAD = true;

    private static PlayerManager instance = new PlayerManager();

    private String sVideoUrl = null;
    private PlayerHolder sPlayerHolder = null;

    private Map<String, PlayerHolder> currentPlayerHolders = new HashMap<>();

    private PlayerManager() {
    }

    public static PlayerManager getInstance() {
        return instance;
    }

    private static PlayerWrapper player = PlayerWrapper.getInstance();

    public PlayerWrapper getPlayer(PlayerHolder viewHolder) {
        return player;
    }

    public void preAttch(final String videoUrl, PlayerHolder playerHolder) {
        currentPlayerHolders.put(videoUrl, playerHolder);
        if (USE_PRELOAD) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    player.preload(videoUrl);
                }
            }).start();

        }
    }

    public interface PlayerHolder {
        void onDetached();

        void preload();
    }

    public void preload(int pos, String videoUrl) {
        if (!VUtil.isWifiConnected(VUtil.getApplication())) {
            VLog.d(TAG, "preload[" + pos + "]: NO WIFI NO PRELOAD!" + videoUrl);
            return;
        }

        VLog.d(TAG, "preload[" + pos + "]: videoUrl:" + videoUrl);
        VLog.d(TAG, "preload[" + pos + "]: sVideoUrl:" + sVideoUrl);
        VLog.d(TAG, "preload[" + pos + "]: player:" + player);
        VLog.d(TAG, "preload[" + pos + "]: player.isPlaying():" + player.isPlaying());
        if (player.isPlaying() && videoUrl.equals(player.getPlayUrl())) {
            return;
        }
        PlayerHolder playerHolder = currentPlayerHolders.get(videoUrl);
        VLog.d(TAG, "preload[" + pos + "]: playerHolder:" + playerHolder);
        if (playerHolder != null) {
            playerHolder.preload();
        }

    }

    // 初始化 exoplayer
    public void attchPlayer(Context context, PlayerHolder playerHolder, SimpleExoPlayerView simpleExoPlayerView,
                            String videoUri,
                            Player.EventListener eventListener,
                            SimpleExoPlayer.VideoListener videoListener,
                            boolean userAction) {
        VLog.d(TAG, "attchPlayer  videoUrl:" + videoUri + ", playerHolder:" + playerHolder);
        if ((sPlayerHolder == playerHolder || videoUri.equals(player.getPlayUrl())&& !player.isReleased())) {
//            if (player.isPlaying()) {
            Log.d(TAG, "attchPlayer: return!");
                return;
//            }
        }

        long start = System.currentTimeMillis();
        if (player != null) {
            player.release();

            if (!videoUri.equals(sVideoUrl) && sPlayerHolder != null && sPlayerHolder != playerHolder) {
                sPlayerHolder.onDetached();
            }
        }
        sPlayerHolder = playerHolder;
        sVideoUrl = videoUri;

        player.init(context, simpleExoPlayerView, videoUri, eventListener, videoListener, userAction);
        currentPlayerHolders.put(videoUri, playerHolder);
        long end = System.currentTimeMillis();
        VLog.d(TAG, "attchPlayer  finished!  use:" + (end - start) + "ms");
    }

    public void detachPlayer(PlayerHolder playerHolder, String videoUrl) {
        VLog.d(TAG, "detachPlayer: videoUrl:" + videoUrl);
        if (sPlayerHolder != null
                && sPlayerHolder == playerHolder
                & videoUrl.equals(sVideoUrl)) {

            VLog.d(TAG, "detachPlayer: will pause player!");
            if (player != null) {
                player.pause();
            }

            sPlayerHolder.onDetached();
            sPlayerHolder = null;
            sVideoUrl = null;
            currentPlayerHolders.remove(videoUrl);
        }
    }

    public void release(String videoUrl){
        if (player != null) {
            player.release();
        }
    }
}
