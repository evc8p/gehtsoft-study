package com.evch.rrm.webserver;

import com.evch.rrm.customspring.annotation.CustomGetMapping;
import com.evch.rrm.customspring.annotation.CustomPostMapping;
import com.evch.rrm.customspring.annotation.CustomRequestMapping;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

import static com.evch.rrm.webserver.Response.DataType.FILE;
import static com.evch.rrm.webserver.Response.DataType.JSON;

@CustomRequestMapping()
public class CustomHtmlController extends CustomController {
    private final static String USER_DIR_RESOURCES = System.getProperty("user.dir") + "/src/main/resources/";
    private final static String USER_DIR_STATIC_RESOURCES = USER_DIR_RESOURCES + "static/";

    @CustomGetMapping({"/", "/index.html"})
    public Response getStartPageSlash(Map<String, String> requestEntries) {
        String filePath = requestEntries.get("GET");
        if (filePath.equals("/") || filePath.equals("/index.html")) {
            return new Response(FILE, USER_DIR_STATIC_RESOURCES + "index.html");
        }
        if (new File(USER_DIR_STATIC_RESOURCES + filePath.substring(1)).exists()) {
            return new Response(FILE, USER_DIR_STATIC_RESOURCES + filePath.substring(1));
        }
        return new Response(FILE, USER_DIR_STATIC_RESOURCES + "index.html");
    }

    @CustomGetMapping({"/static/", "/static/images/"})
    public Response getStatic(Map<String, String> requestEntries) {
        String filePath = USER_DIR_RESOURCES + requestEntries.get("GET");
        if (new File(filePath).exists()) {
            return new Response(FILE, filePath);
        }
        return new Response(FILE, USER_DIR_STATIC_RESOURCES + "index.html");
    }

    @CustomGetMapping("/api/time")
    public Response getTime(Map<String, String> requestEntries) {
        String response = """
                {
                    "currentServerTime": "%s"
                }""";
        return new Response(JSON, String.format(response, LocalDateTime.now()));
    }

    @CustomGetMapping("/api/stats")
    public Response getStats(Map<String, String> requestEntries) {
        return new Response(JSON, """
                {
                    "stats": "Server has no stats"
                }""");
    }

    @CustomPostMapping("/api/echo")
    public Response sendEcho(Map<String, String> requestEntries) {
        String response = """
                {
                    "echoOutput": "Server saw your message: %s"
                }""";
        return new Response(JSON, String.format(response,
                new JSONObject(requestEntries.get("BODY")).get("message")));
    }
}
