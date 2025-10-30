package com.evch.rrm.webserver;

import com.evch.rrm.CustomExecutorService;
import com.evch.rrm.webserver.annotations.Delete;
import com.evch.rrm.webserver.annotations.Get;
import com.evch.rrm.webserver.annotations.Post;
import com.evch.rrm.webserver.annotations.Put;
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
import java.util.concurrent.TimeUnit;

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
    private Object controllerInstance;
    private final Map<String, Method> controllerMethods = new HashMap<>();

    public CustomWebServer(int port, int threadPoolSize, boolean useVirtualThreads) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("The port number is less than 0 ot greater than 65535");
        }
        this.port = port;
        this.executor = new CustomExecutorService(threadPoolSize, useVirtualThreads);
        registerController(RestController.class);
    }

    public static void main(String[] args) {
        // Test with virtual threads
        CustomWebServer virtualServer = new CustomWebServer(8080, 100, true);
        virtualServer.registerController(RestController.class);

        // Test with platform threads
        CustomWebServer platformServer = new CustomWebServer(8081, 50, false);
        platformServer.registerController(RestController.class);

        try {
            virtualServer.start();
            platformServer.start();

            System.out.println("Servers started:");
            System.out.println("Virtual thread server: http://localhost:8080");
            System.out.println("Platform thread server: http://localhost:8081");

            // Keep servers running
            Thread.sleep(30000); // Run for 1 minute
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                virtualServer.stop();
                platformServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerController(Class<?> controller) {
        if (this.controllerInstance != null && this.controllerInstance.getClass().equals(controller)) return;

        try {
            this.controllerInstance = controller.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        readControllerMethods();
    }

    private void readControllerMethods() {
        Method[] methods = controllerInstance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Get.class)) {
                Get getAnnotation = method.getAnnotation(Get.class);
                controllerMethods.put("get." + getAnnotation.endPoint(), method);
            }
            if (method.isAnnotationPresent(Post.class)) {
                Post postAnnotation = method.getAnnotation(Post.class);
                controllerMethods.put("post." + postAnnotation.endPoint(), method);
            }
            if (method.isAnnotationPresent(Put.class)) {
                Put putAnnotation = method.getAnnotation(Put.class);
                controllerMethods.put("put." + putAnnotation.endPoint(), method);
            }
            if (method.isAnnotationPresent(Delete.class)) {
                Delete deleteAnnotation = method.getAnnotation(Delete.class);
                controllerMethods.put("delete." + deleteAnnotation.endPoint(), method);
            }
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
                try (Socket clientSocket = serverSocket.accept()) {
                    executor.execute(() -> {
                        Socket finalClientSocket = null;
                        try {
                            boolean isKeepAlive = true;
                            finalClientSocket = clientSocket;
                            finalClientSocket.setSoTimeout(2000);
                            while (!finalClientSocket.isClosed() && running && !Thread.currentThread().isInterrupted() && isKeepAlive) {
                                isKeepAlive = handleClient(finalClientSocket) && this.isKeepAlive;
                            }
                        } catch (SocketException ignored) {
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
    }

    private boolean handleClient(Socket clientSocket) {
        Map<String, String> requestEntries = parseAndExtractRequest(clientSocket);
        if (checkRequest(requestEntries)) {
            Response response = new Response();
            String http_method = requestEntries.get("http_method");
            String source = getSourceOrSlash(requestEntries.get(http_method));
            Method method = controllerMethods.get(http_method + "." + source);
            if (method != null) {
                try {
                    response = (Response) method.invoke(controllerInstance, requestEntries);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                switch (response.getDataType()) {
                    case JSON -> sendTextInResponse(ContentTypes.JSON, response.getData(), clientSocket);
                    case FILE -> sendFileInResponse(response.getData(), clientSocket);
                }
            } else {
                sendFileInResponse(USER_DIR_STATIC_RESOURCES + source, clientSocket);
            }
        } else {
            send404(clientSocket);
        }
        return !(requestEntries.containsKey("connection") && requestEntries.get("connection").contains("close"));
    }

    private boolean checkRequest(Map<String, String> headers) {
        return (headers.containsKey("get") && headers.get("http_type").contains("http/1.1")
                && headers.containsKey("host")
                && headers.containsKey("user-agent")
                && headers.containsKey("accept")
                && (headers.get("accept").contains("text") || headers.get("accept").contains("image")
                || headers.get("accept").contains("application") || headers.get("accept").contains("*/*")))
                || (headers.containsKey("post") && headers.get("http_type").contains("http/1.1")
                && headers.containsKey("content-type") && headers.get("content-type").contains("application/json")
                && headers.containsKey("content-length"));
    }

    private Map<String, String> parseAndExtractRequest(final Socket clientSocket) {
        Map<String, String> headers = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = in.readLine();
            String[] data = line.split(" ", 3);
            headers.put("http_method", data[0].trim().toLowerCase());
            headers.put(data[0].trim().toLowerCase(), data[1].trim());
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
        return headers;
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
