package com.hawolt.playlist;

import java.util.List;

public interface PlaylistCallback {
    void onPlaylist(String id, List<Long> tracks);
}