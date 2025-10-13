package com.evch.rrm.webserver;

import com.evch.rrm.webserver.annotations.CustomRestController;
import com.evch.rrm.webserver.annotations.Get;
import com.evch.rrm.webserver.annotations.Post;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Map;

import static com.evch.rrm.webserver.Response.DataType.FILE;
import static com.evch.rrm.webserver.Response.DataType.JSON;

@CustomRestController
public class RestController {
    private final static String USER_DIR_RESOURCES = System.getProperty("user.dir") + "/src/main/resources/";
    private final static String USER_DIR_STATIC_RESOURCES = USER_DIR_RESOURCES + "static/";

    @Get(endPoint = "/")
    public Response getStartPageSlash(Map<String, String> requestEntries) {
        return new Response(FILE, USER_DIR_STATIC_RESOURCES + "index.html");
    }

    @Get(endPoint = "/api/time")
    public Response getTime(Map<String, String> requestEntries) {
        String response = """
                {
                    "currentServerTime": "%s"
                }""";
        return new Response(JSON, String.format(response, LocalDateTime.now()));
    }

    @Get(endPoint = "/api/stats")
    public Response getStats(Map<String, String> requestEntries) {
        return new Response(JSON, """
                {
                    "stats": "Server has no stats"
                }""");
    }

    @Post(endPoint = "/api/echo")
    public Response sendEcho(Map<String, String> requestEntries) {
        String response = """
                {
                    "echoOutput": "Server saw your message: %s"
                }""";
        return new Response(JSON, String.format(response,
                new JSONObject(requestEntries.get("body")).get("message")));
    }
}
