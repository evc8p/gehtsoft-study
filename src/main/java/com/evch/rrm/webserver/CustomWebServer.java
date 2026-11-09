package com.evch.rrm.webserver;

import com.evch.rrm.CustomExecutorService;
import com.evch.rrm.customspring.ControllerHandler;
import com.evch.rrm.customspring.annotation.CustomApplication;
import com.evch.rrm.customspring.annotation.CustomAutowired;
import com.evch.rrm.webserver.util.Repository;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private ControllerHandler controllerHandler;

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
        Request request = parseAndExtractRequest(clientSocket);
        Response response;
        if (checkRequest(request) && Objects.nonNull(response = (Response) controllerHandler.handleRequest(request)) && Objects.nonNull(response.getDataType())) {
            switch (response.getDataType()) {
                case JSON -> sendTextInResponse(ContentTypes.JSON, response.getData(), clientSocket);
                case FILE -> sendFileInResponse(response.getData(), clientSocket);
                case TEXT -> sendTextInResponse(ContentTypes.TEXT, response.getData(), clientSocket);
                case ERROR -> {
                    if (response.getData().equals("404")) {
                        send404(clientSocket);
                    }
                }
            }
        } else {
            send404(clientSocket);
        }
        return !(request.getHeaders().containsKey("CONNECTION") && request.getHeaders().get("CONNECTION").contains("CLOSE"));
    }

    // package-private для тестов
    boolean checkRequest(Request request) {
        if (request == null || !"HTTP/1.1".equals(request.getVersion())) {
            return false;
        }
        return switch (request.getMethod()) {
            case "GET" -> isValidGet(request);
            case "POST" -> isValidPost(request);
            default -> false;
        };
    }

    private boolean isValidGet(Request request) {
        return request.getHeaders().keySet().containsAll(List.of("HOST", "USER-AGENT", "ACCEPT"))
                && List.of("TEXT", "IMAGE", "APPLICATION", "*/*").stream().anyMatch(s -> request.getHeaders().get("ACCEPT").contains(s));
    }

    private boolean isValidPost(Request request) {
        return request.getHeaders().containsKey("CONTENT-TYPE") && request.getHeaders().get("CONTENT-TYPE").contains("APPLICATION/JSON")
                && request.getHeaders().containsKey("CONTENT-LENGTH");
    }

    private Request parseAndExtractRequest(final Socket clientSocket) {
        Request request = new Request();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //get a request 1st line
            String line = in.readLine();
            String[] data = line.split(" ", 3);
            request.setMethod(data[0].trim().toUpperCase());
            request.setUri(data[1].trim());
            request.setVersion(data[2].trim().toUpperCase());

            //get request headers
            int contentLength = 0;
            Map<String, String> headers = new HashMap<>();
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                data = line.split(":", 2);
                String key = data[0].trim().toUpperCase();
                headers.put(key, data[1].trim().toUpperCase());
                if (key.contains("CONTENT-LENGTH")) {
                    contentLength = Integer.parseInt(data[1].trim());
                }
            }
            request.setHeaders(headers);

            //get a request body
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = in.read(body, totalRead, contentLength - totalRead);
                    if (read == -1) {
                        request.reset();
                        return request;
                    }
                    totalRead += read;
                }
                request.setBody(new String(body));
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
        return request;
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
