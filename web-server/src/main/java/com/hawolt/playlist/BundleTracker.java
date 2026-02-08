package com.hawolt.playlist;

import com.hawolt.SoundcloudInternal;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;

import java.util.*;

public class BundleTracker {
    private static final Object lock = new Object();
    private final Map<String, Job> callbacks = new LinkedHashMap<>();
    private final Map<String, List<String>> groups = new LinkedHashMap<>();

    public String load(PlaylistCallback callback, String... playlists) {
        String id = UUID.randomUUID().toString();
        synchronized (lock) {
            callbacks.put(id, Job.start(callback, playlists));
            groups.put(id, new ArrayList<>());
            List<String> list = groups.get(id);
            list.addAll(Arrays.asList(playlists));
        }
        for (String playlist : playlists) {
            SoundcloudInternal.load(playlist);
        }
        return id;
    }

    public void loaded(String source, Playlist playlist) {
        synchronized (lock) {
            for (Job job : callbacks.values()) {
                job.loaded(source, playlist);
            }
            Iterator<String> it = groups.keySet().iterator();
            while (it.hasNext()) {
                String id = it.next();
                Job job = callbacks.get(id);
                List<String> playlists = groups.get(id);
                Map<String, List<List<Long>>> cache = job.getCache();
                int count = 0;
                for (String loaded : cache.keySet()) {
                    for (String required : playlists) {
                        if (required.equals(loaded)) count++;
                    }
                }
                if (count != playlists.size()) continue;

                List<Long> ordered = new ArrayList<>();

                for (String playlistId : playlists) {
                    List<List<Long>> lists = cache.get(playlistId);
                    if (lists == null) continue;

                    for (List<Long> list : lists) {
                        ordered.addAll(list);
                    }
                }

                job.getCallback().onPlaylist(id, ordered);
                callbacks.remove(id);
                it.remove();
            }
        }
    }
}
