package com.hawolt;

import com.hawolt.handler.CORSHandler;
import com.hawolt.handler.SoundcloudHandler;
import io.javalin.Javalin;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Javalin.create()
                .before("*", CORSHandler.ACCESS_CONTROL_HANDLER)
                .options("*", CORSHandler.OPTIONS_HANDLER)
                .post("/load", SoundcloudHandler.LOAD_HANDLER)
                .get("/fetch/{id}", SoundcloudHandler.FETCH_HANDLER)
                .post("/track/{track}", SoundcloudHandler.TRACK_HANDLER)
                .get("/status/{id}", SoundcloudHandler.LOAD_STATUS_HANDLER)
                .start(13999);
    }
}