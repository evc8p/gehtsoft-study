package com.evch.rrm.webserver;

import com.evch.rrm.CustomExecutorService;
import com.evch.rrm.customspring.ApplicationContext;
import com.evch.rrm.customspring.annotation.CustomApplication;
import com.evch.rrm.customspring.annotation.CustomAutowired;
import com.evch.rrm.webserver.model.Controller;
import com.evch.rrm.webserver.util.Repository;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CustomApplication
public class CustomWebServer {
    private final static String USER_DIR_RESOURCES = System.getProperty("user.dir") + "/src/main/resources/";
    private final static String USER_DIR_STATIC_RESOURCES = USER_DIR_RESOURCES + "static";
    private final int port;
    private final CustomExecutorService executor;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    @Getter
    @Setter
    private volatile boolean isKeepAlive = true;
    @CustomAutowired
    private CustomController controller;
    private ApplicationContext context;

    public CustomWebServer(Integer port, Integer threadPoolSize, Boolean useVirtualThreads) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("The port number is less than 0 ot greater than 65535");
        }
        this.port = port;
        this.executor = new CustomExecutorService(threadPoolSize, useVirtualThreads);
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
                        try {
                            boolean isKeepAlive = true;
                            clientSocket.setSoTimeout(2000);
                            while (!clientSocket.isClosed() && running && !Thread.currentThread().isInterrupted() && isKeepAlive) {
                                isKeepAlive = handleClient(clientSocket) && this.isKeepAlive;
                            }
                        } catch (SocketException ignored) {
                        } finally {
                            if (clientSocket != null && !clientSocket.isClosed()) {
                                try {
                                    clientSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (SocketTimeoutException | SocketException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() throws InterruptedException {
        executor.shutdown();
        running = false;
        executor.awaitTermination(20, TimeUnit.SECONDS);
        if (Objects.nonNull(serverSocket)) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new InterruptedException("Server socket can not be closed");
            }
        }
        Repository.closeConnection();
    }

    private boolean handleClient(Socket clientSocket) {
        Map<String, String> requestEntries = parseAndExtractRequest(clientSocket);
        if (checkRequest(requestEntries)) {
            Response response = new Response();
            String http_method = requestEntries.get("http_method");
            String source = getSourceOrSlash(requestEntries.get(http_method));
            Controller invokedController = controller.getControllers().get(http_method + " " + source.replaceAll("[^/]+\\.[^/]+$", ""));
            if (invokedController != null) {
                try {
                    Method method = invokedController.getMethod();
                    Object object = invokedController.getObject();
                    if (method.getParameterCount() == 0) {
                        response = (Response) method.invoke(object);
                    } else {
                        response = (Response) method.invoke(object, requestEntries);
                    }
                } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException |
                         NullPointerException | ExceptionInInitializerError e) {
                    e.printStackTrace();
                }
                switch (response.getDataType()) {
                    case JSON -> sendTextInResponse(ContentTypes.JSON, response.getData(), clientSocket);
                    case FILE -> sendFileInResponse(response.getData(), clientSocket);
                    case TEXT -> sendTextInResponse(ContentTypes.TEXT, response.getData(), clientSocket);
                }
            } else {
                send404(clientSocket);
            }
        } else {
            send404(clientSocket);
        }
        return !(requestEntries.containsKey("CONNECTION") && requestEntries.get("CONNECTION").contains("CLOSE"));
    }

    private boolean checkRequest(Map<String, String> headers) {
        return (headers.containsKey("GET") && headers.get("http_type").contains("HTTP/1.1")
                && headers.containsKey("HOST")
                && headers.containsKey("USER-AGENT")
                && headers.containsKey("ACCEPT")
                && (headers.get("ACCEPT").contains("TEXT") || headers.get("ACCEPT").contains("IMAGE")
                || headers.get("ACCEPT").contains("APPLICATION") || headers.get("ACCEPT").contains("*/*")))
                || (headers.containsKey("POST") && headers.get("http_type").contains("HTTP/1.1")
                && headers.containsKey("CONTENT-TYPE") && headers.get("CONTENT-TYPE").contains("APPLICATION/JSON")
                && headers.containsKey("CONTENT-LENGTH"));
    }

    private Map<String, String> parseAndExtractRequest(final Socket clientSocket) {
        Map<String, String> requestEntries = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = in.readLine();
            String[] data = line.split(" ", 3);
            requestEntries.put("http_method", data[0].trim().toUpperCase());
            requestEntries.put(data[0].trim().toUpperCase(), data[1].trim());
            requestEntries.put("http_type", data[2].trim().toUpperCase());

            int contentLength = 0;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                data = line.split(":", 2);
                String key = data[0].trim().toUpperCase();
                requestEntries.put(key, data[1].trim().toUpperCase());
                if (key.contains("CONTENT-LENGTH")) {
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
                        requestEntries.clear();
                        return requestEntries;
                    }
                    totalRead += read;
                }
                requestEntries.put("BODY", new String(body));
            }
        } catch (SocketException e) {
            if ("Connection reset".equals(e.getMessage())) {
                System.out.println("Client closed connection prematurely");
            } else {
                e.printStackTrace();
            }
        } catch (SocketTimeoutException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestEntries;
    }

    private void sendFileInResponse(String filePath, Socket socket) {
        try {
            ContentTypes contentType = ContentTypes.getContentType(getSourceExtensionOrSlash(filePath));
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            Path p = Paths.get(filePath);
            InputStream in = null;
            try {
                in = Files.newInputStream(p);
                out.write(makeResponseHeaders("200", "OK", contentType.getContentType(),
                        Files.size(p)).getBytes(StandardCharsets.UTF_8));
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                out.flush();
            } catch (NoSuchElementException | IOException e) {
                send404(socket);
            } finally {
                if (in != null) {
                    in.close();
                }
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
        try (BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            out.write(("HTTP/1.1 400 Bad Request\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send404(Socket socket) {
        try (BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            out.write(("HTTP/1.1 404 Not Found\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSourceOrSlash(String source) {
        if (source.equals("/")) {
            return "/";
        }
        int paramsIndex = source.contains("?") ? source.indexOf("?") : source.length();
        return source.substring(0, paramsIndex);
    }

    private String getSourceExtensionOrSlash(String source) {
        if (source.equals("/")) {
            return "/";
        }
        int pointIndex = source.indexOf(".");
        if (pointIndex == -1) {
            return "";
        }
        return source.substring(pointIndex);
    }
}
