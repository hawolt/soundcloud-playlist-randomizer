package com.hawolt.handler;

import io.javalin.http.Handler;

public class CORSHandler {
    public static final Handler ACCESS_CONTROL_HANDLER = ctx -> {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "*");
        ctx.header("Access-Control-Allow-Headers", "*");
        ctx.header("Access-Control-Allow-Credentials", "true");
    };

    public static final Handler OPTIONS_HANDLER = ctx -> {
        ctx.status(200);
    };
}
