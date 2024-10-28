package com.hawolt.playlist;

import com.hawolt.data.media.hydratable.impl.playlist.Playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Job {

    private final Map<String, List<List<Long>>> cache = new HashMap<>();
    private final PlaylistCallback callback;
    private final String[] playlists;

    public Job(PlaylistCallback callback, String... playlists) {
        this.playlists = playlists;
        this.callback = callback;
    }

    public void loaded(String source, Playlist playlist) {
        for (String required : playlists) {
            if (required.equals(source)) {
                handle(source, playlist);
            }
        }
    }

    public PlaylistCallback getCallback() {
        return callback;
    }

    public Map<String, List<List<Long>>> getCache() {
        return cache;
    }

    private void handle(String source, Playlist playlist) {
        if (!cache.containsKey(source)) cache.put(source, new ArrayList<>());
        cache.get(source).add(playlist.getList());
    }

    public static Job start(PlaylistCallback callback, String... playlists) {
        return new Job(callback, playlists);
    }
}
