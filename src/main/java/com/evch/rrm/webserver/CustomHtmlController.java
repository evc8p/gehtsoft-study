package com.evch.rrm.webserver;

import com.evch.rrm.customspring.annotation.CustomGetMapping;
import com.evch.rrm.customspring.annotation.CustomPostMapping;
import com.evch.rrm.customspring.annotation.CustomRequestMapping;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Properties;

import static com.evch.rrm.customspring.contract.ServerResponse.DataType.*;

@CustomRequestMapping()
public class CustomHtmlController extends CustomController {
    @CustomGetMapping({"/", "/images"})
    public Response getStartPageOrStatic(Request request) {
        Properties properties = CustomWebServerProperties.get();
        String staticResourcesFolder = properties.getProperty("static_resources_folder");
        staticResourcesFolder = staticResourcesFolder != null ? staticResourcesFolder : "static";
        String path = request.getPath();
        Path root = Paths.get(staticResourcesFolder).toAbsolutePath().normalize();
        String relative = path.startsWith("/") ? path.substring(1) : path;
        Path pathFile = root.resolve(relative).normalize();
        if (!pathFile.startsWith(root)) {
            return new Response(ERROR, "400");
        }

        if (path.equals("/favicon.ico")) {
            String file = staticResourcesFolder + request.getPath();
            if (new File(file).exists()) {
                return new Response(FILE, file);
            }
        }
        if (path.equals("/") || path.equals("/index.html")) {
            String file = staticResourcesFolder + "/index.html";
            if (new File(file).exists()) {
                return new Response(FILE, file);
            }
        }
        String filePath = staticResourcesFolder + request.getPath();
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
