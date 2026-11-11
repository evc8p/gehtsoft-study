package com.evch.rrm.webserver;

import com.evch.rrm.customspring.annotation.CustomGetMapping;
import com.evch.rrm.customspring.annotation.CustomPostMapping;
import com.evch.rrm.customspring.annotation.CustomRequestMapping;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;

import static com.evch.rrm.customspring.contract.ServerResponse.DataType.*;

@CustomRequestMapping()
public class CustomHtmlController extends CustomController {
    private final static String USER_DIR_RESOURCES = System.getProperty("user.dir") + "/src/main/resources/";
    private final static String USER_DIR_STATIC_RESOURCES = USER_DIR_RESOURCES + "static/";

    @CustomGetMapping("/")
    public Response getStartPageSlash(Request request) {
        String source = request.getSource();
        if (source.equals("/") || source.equals("/index.html")) {
            if (new File(USER_DIR_STATIC_RESOURCES + "index.html").exists()) {
                return new Response(FILE, USER_DIR_STATIC_RESOURCES + "index.html");
            }
        }
        return new Response(ERROR, "404");
    }

    @CustomGetMapping(isStaticContent = true, value = {"/static/", "/static/images/"})
    public Response getStatic(Request request) {
        String filePath = USER_DIR_RESOURCES + request.getSource();
        if (new File(filePath).exists()) {
            return new Response(FILE, filePath);
        }
        return new Response(ERROR, "404");
    }

    @CustomGetMapping("/api/time")
    public Response getTime() {
        String response = """
                {
                    "currentServerTime": "%s"
                }""";
        return new Response(JSON, String.format(response, LocalDateTime.now()));
    }

    @CustomGetMapping("/api/stats")
    public Response getStats() {
        return new Response(JSON, """
                {
                    "stats": "Server has no stats"
                }""");
    }

    @CustomPostMapping("/api/echo")
    public Response sendEcho(Request request) {
        String response = """
                {
                    "echoOutput": "Server saw your message: %s"
                }""";
        return new Response(JSON, String.format(response,
                new JSONObject(request.getBody()).get("message")));
    }
}
