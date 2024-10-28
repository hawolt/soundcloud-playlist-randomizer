package com.hawolt.playlist;

import com.hawolt.Soundcloud;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;

import java.util.*;
import java.util.stream.Stream;

public class BundleTracker {
    private static final Object lock = new Object();
    private final Map<String, Job> callbacks = new HashMap<>();
    private final Map<String, List<String>> groups = new HashMap<>();

    public String load(PlaylistCallback callback, String... playlists) {
        String id = UUID.randomUUID().toString();
        synchronized (lock) {
            callbacks.put(id, Job.start(callback, playlists));
            groups.put(id, new ArrayList<>());
            List<String> list = groups.get(id);
            list.addAll(Arrays.asList(playlists));
        }
        for (String playlist : playlists) {
            Soundcloud.load(playlist);
        }
        return id;
    }

    public void loaded(String source, Playlist playlist) {
        synchronized (lock) {
            for (Job job : callbacks.values()) {
                job.loaded(source, playlist);
            }
            for (String id : groups.keySet()) {
                Job job = callbacks.get(id);
                List<String> playlists = groups.get(id);
                Map<String, List<List<Long>>> cache = job.getCache();
                int count = 0;
                for (String loaded : cache.keySet()) {
                    for (String required : playlists) {
                        if (required.contains(loaded)) count++;
                    }
                }
                if (count != playlists.size()) continue;
                Stream<Long> stream = cache.values().stream().flatMap(List::stream).flatMap(List::stream);
                callbacks.get(id).getCallback().onPlaylist(id, new HashSet<>(stream.toList()));
                callbacks.remove(id);
                groups.remove(id);
            }
        }
    }
}
