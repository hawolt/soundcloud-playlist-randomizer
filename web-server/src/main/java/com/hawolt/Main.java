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
    https://soundcloud.com/hawolt/sets/test1/s-zEAXlICO3Rg?si=7fbf421006a648c882ec8b06d2576c9e&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing
    https://soundcloud.com/hawolt/sets/test2-2/s-Jti83VGdNlu?si=a8b08479c86e4ae7a2fa6d19a5df3c9c&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing
}