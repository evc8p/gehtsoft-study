package com.evch.rrm.webserver;

import com.evch.rrm.CustomExecutorService;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class CustomWebServer {
    private final int port;
    private final CustomExecutorService executor;
    private final String userDirResources = System.getProperty("user.dir") + "/src/main/resources/";
    private final String userDirStaticResources = userDirResources + "static/";
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    @Getter
    @Setter
    private volatile boolean isKeepAlive = true;

    public CustomWebServer(int port, int threadPoolSize, boolean useVirtualThreads) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("The port number is less than 0 ot greater than 65535");
        }
        this.port = port;
        this.executor = new CustomExecutorService(threadPoolSize, useVirtualThreads);
    }

    public static void main(String[] args) {
        // Test with virtual threads
        CustomWebServer virtualServer = new CustomWebServer(8080, 100, true);

        // Test with platform threads
        CustomWebServer platformServer = new CustomWebServer(8081, 50, false);

        try {
            virtualServer.start();
            platformServer.start();

            System.out.println("Servers started:");
            System.out.println("Virtual thread server: http://localhost:8080");
            System.out.println("Platform thread server: http://localhost:8081");

            // Keep servers running
            Thread.sleep(60000); // Run for 1 minute
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            virtualServer.stop();
            platformServer.stop();
        }
    }

    public void start() throws IOException {
        running = true;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new SocketException("The server socket can not be created. " + e.getStackTrace());
        }
        serverSocket.setSoTimeout(1000);
        executor.setWaitingWorkersTimeoutMs(10000);
        Thread.ofVirtual().start(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.execute(() -> {
                        Socket finalClientSocket = null;
                        try {
                            boolean isKeepAlive = true;
                            finalClientSocket = clientSocket;
                            while (!finalClientSocket.isClosed() && running && !Thread.currentThread().isInterrupted() && isKeepAlive) {
                                isKeepAlive = handleClient(finalClientSocket) && this.isKeepAlive;
                            }
                        } finally {
                            if (finalClientSocket != null && !finalClientSocket.isClosed()) {
                                try {
                                    finalClientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (SocketTimeoutException ste) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        executor.shutdown();
        running = false;
        executor.awaitTermination(20, TimeUnit.SECONDS);
    }

    private boolean handleClient(Socket clientSocket) {
        Map<String, String> requestEntries = parseAndExtractRequest(clientSocket);
        if (checkRequest(requestEntries)) {
            if (requestEntries.get("get") != null) {
                String response;
                String source = requestEntries.get("get");
                switch (source) {
                    case "/api/time":
                        response = """
                                {
                                    "currentServerTime": "%s"
                                }""";
                        sendTextInResponse(ContentTypes.JSON, String.format(response, LocalDateTime.now()), clientSocket);
                        break;
                    case "/api/stats":
                        response = """
                                {
                                    "stats": "%s"
                                }""";
                        sendTextInResponse(ContentTypes.JSON, String.format(response, "Server has no stats"), clientSocket);
                        break;
                    default:
                        ContentTypes contentType = ContentTypes.HTML;
                        try {
                            contentType = ContentTypes.getContentType(getSourceExtensionOrSlash(source));
                        } catch (NoSuchElementException e) {
                            send404(clientSocket);
                        }
                        int questionMarkIndex = source.indexOf("?");
                        String sourceName = (questionMarkIndex == -1) ? source : source.substring(0, questionMarkIndex);
                        sendFileInResponse(contentType, userDirStaticResources + (source.equals("/") ? "index.html" : sourceName), clientSocket);
                }
            }

            if (requestEntries.get("post") != null) {
                String response;
                String source = requestEntries.get("post");
                if (source.equals("/api/echo")) {
                    response = """
                            {
                                "echoOutput": "Server saw your message: %s"
                            }""";
                    sendTextInResponse(ContentTypes.JSON, String.format(response,
                            new JSONObject(requestEntries.get("body")).get("message")), clientSocket);
                }
            }
        } else {
            send404(clientSocket);
        }
        return !(requestEntries.containsKey("connection") && requestEntries.get("connection").contains("close"));
    }

    private boolean checkRequest(Map<String, String> headers) {
        return (headers.containsKey("get") && headers.get("http_type").contains("http/1.1")
                && headers.containsKey("host") && (headers.get("host").contains("localhost:8080") || headers.get("host").contains("localhost:8081"))
                && headers.containsKey("user-agent") && headers.get("user-agent").contains("mozilla/5.0")
                && headers.containsKey("accept")
                && (headers.get("accept").contains("text") || headers.get("accept").contains("image")
                || headers.get("accept").contains("application") || headers.get("accept").contains("*/*")))
                || (headers.containsKey("post") && headers.get("http_type").contains("http/1.1")
                && headers.containsKey("content-type") && headers.get("content-type").contains("application/json")
                && headers.containsKey("content-length"));
    }

    private Map<String, String> parseAndExtractRequest(Socket clientSocket) {
        Map<String, String> headers = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = in.readLine();
            String[] data = line.split(" ", 3);
            headers.put(data[0].trim().toLowerCase(), data[1].trim().toLowerCase());
            headers.put("http_type", data[2].trim().toLowerCase());

            int contentLength = 0;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                data = line.split(":", 2);
                String key = data[0].trim().toLowerCase();
                headers.put(key, data[1].trim().toLowerCase());
                if (key.contains("content-length")) {
                    contentLength = Integer.parseInt(data[1].trim());
                }
            }

            if (contentLength > 0) {
                char[] body = new char[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = in.read(body, totalRead, contentLength - totalRead);
                    if (read == -1) {
                        send400(clientSocket);
                        headers.clear();
                        return headers;
                    }
                    totalRead += read;
                }
                headers.put("body", new String(body));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headers;
    }

    private void sendFileInResponse(ContentTypes contentType, String filePath, Socket socket) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            Path p = Paths.get(filePath);
            try (InputStream in = Files.newInputStream(p)) {
                out.write(makeResponseHeaders("200", "OK", contentType.getContentType(), Files.size(p)).getBytes(StandardCharsets.UTF_8));
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                out.flush();
            } catch (IOException e) {
                send404(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTextInResponse(ContentTypes contentType, String text, Socket socket) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            out.write(makeResponseHeaders("200", "OK", contentType.getContentType(), text.length()).getBytes(StandardCharsets.UTF_8));
            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makeResponseHeaders(String statusCode, String message, String contentType, long sourceLength) {
        final String responseImageHeaders = "HTTP/1.1 %s %s\r\nContent-Type: %s\r\nContent-Length: %d\r\n\r\n";
        return String.format(responseImageHeaders, statusCode, message, contentType, sourceLength);
    }

    private void send400(Socket socket) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            out.write(("HTTP/1.1 400 Bad Request\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send404(Socket socket) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            out.write(("HTTP/1.1 404 Not Found\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSourceExtensionOrSlash(String source) {
        if (source.equals("/")) {
            return "/";
        }
        int pointIndex = source.indexOf(".");
        int paramsIndex = source.contains("?") ? source.indexOf("?") : source.length();
        return source.substring(pointIndex, paramsIndex);
    }
}
