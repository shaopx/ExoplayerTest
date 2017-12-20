package com.sogo.exoplayer;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther shaopx
 * @date 2017/12/13.
 */

public class VideoUrlCache {

    private static Map<Integer, String> videoUrls = new HashMap<>();

    public static void putVideoUrl(int id, String data) {
        videoUrls.put(id, data);
    }

    public static String getVideoUrl(int id){
        return videoUrls.get(id);
    }
}
