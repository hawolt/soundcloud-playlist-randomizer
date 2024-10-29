package com.hawolt.handler;

import com.hawolt.Soundcloud;
import com.hawolt.data.media.ObjectCallback;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;
import com.hawolt.data.media.hydratable.impl.playlist.PlaylistManager;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.track.TrackManager;
import com.hawolt.data.media.search.Explorer;
import com.hawolt.data.media.search.query.CompleteObjectCollection;
import com.hawolt.data.media.search.query.impl.TrackQuery;
import com.hawolt.playlist.BundleTracker;
import com.hawolt.playlist.PlaylistCallback;
import io.javalin.http.Handler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SoundcloudHandler {
    private static final Map<String, List<Long>> RESULT_MAP = new HashMap<>();
    private static final BundleTracker BUNDLE_TRACKER = new BundleTracker();
    private static final Object lock = new Object();

    private static final PlaylistCallback REFERENCE = (id, playlist) -> {
        synchronized (lock) {
            List<Long> shuffled = new ArrayList<>(playlist);
            Collections.shuffle(shuffled);
            RESULT_MAP.put(id, shuffled);
        }
    };

    static {
        ObjectCallback<Playlist> playlistObjectCallback =
                (source, playlist, arguments) -> BUNDLE_TRACKER.loaded(source, playlist);
        Soundcloud.register(Playlist.class, new PlaylistManager(playlistObjectCallback));
    }


    public static final Handler LOAD_HANDLER = ctx -> {
        JSONArray array = new JSONArray(ctx.body());
        Set<String> set = new HashSet<>();
        for (Object reference : array) {
            set.add(reference.toString());
        }
        String id = BUNDLE_TRACKER.load(REFERENCE, set.toArray(String[]::new));
        JSONObject object = new JSONObject();
        object.put("id", id);
        ctx.result(object.toString());
    };

    public static final Handler LOAD_STATUS_HANDLER = ctx -> {
        synchronized (lock) {
            boolean status = RESULT_MAP.containsKey(ctx.pathParam("id"));
            JSONObject object = new JSONObject();
            object.put("status", status);
            ctx.result(object.toString());
        }
    };

    public static final Handler FETCH_HANDLER = ctx -> {
        synchronized (lock) {
            String id = ctx.pathParam("id");
            if (RESULT_MAP.containsKey(id)) {
                JSONArray response = new JSONArray();
                List<Long> set = RESULT_MAP.get(id);
                for (Long trackId : set) {
                    response.put(trackId);
                }
                ctx.result(response.toString());
            } else {
                ctx.status(400);
            }
        }
    };

    public static final Handler TRACK_HANDLER = ctx -> {
        String id = ctx.pathParam("track");
        TrackQuery query = new TrackQuery(Long.parseLong(id));
        CompleteObjectCollection<Track> collection = Explorer.search(query);
        List<Track> list = collection.getList();
        if (list.isEmpty()) {
            ctx.status(400);
        } else {
            Track reference = list.remove(0);
            JSONObject track = new JSONObject();
            track.put("uri", reference.getLink());
            track.put("title", reference.getTitle());
            track.put("artwork", reference.getArtwork());
            track.put("duration", reference.getFullDuration());
            track.put("artist", reference.getUser().getUsername());
            track.put("waveform", reference.getWaveformURL());
            track.put("pro", reference.isPro());
            ctx.result(track.toString());
        }
    };
}
