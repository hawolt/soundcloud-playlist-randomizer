package com.hawolt.playlist;

import java.util.HashSet;

public interface PlaylistCallback {
    void onPlaylist(String id, HashSet<Long> tracks);
}