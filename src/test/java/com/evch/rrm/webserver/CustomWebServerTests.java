package com.evch.rrm.webserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomWebServerTests   {
    static CustomWebServer customWebServer = new CustomWebServer(8080, 1, true);

    @AfterAll
    static void afterAll() throws InterruptedException {
        customWebServer.stop();
    }

    @Test
    public void checkRequestShouldReturnFalseIfRequestIsNullNegative(){
        Request request = null;
        assertFalse(customWebServer.checkRequest(request));
    }

    @Test
    public void checkRequestShouldReturnFalseIfHttpVersionIsNot11Negative(){
        Request request = new Request();
        request.setMethod("GET");
        request.setUri("/api/stats");
        request.setVersion("HTTP/1.0");
        Map<String, String> headers = new HashMap<>();
        headers.put("ACCEPT", "TEXT/HTML");
        headers.put("HOST", "blablabla");
        headers.put("USER-AGENT", "blablabla");
        request.setHeaders(headers);
        assertFalse(customWebServer.checkRequest(request));
    }

    @Test
    public void checkRequestShouldReturnTrueIfAcceptIsTextHtmlAndAcceptHostUserAgentArePresent(){
        Request request = new Request();
        request.setMethod("GET");
        request.setUri("/api/stats");
        request.setVersion("HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("ACCEPT", "TEXT/HTML");
        headers.put("HOST", "blablabla");
        headers.put("USER-AGENT", "blablabla");
        request.setHeaders(headers);
        assertTrue(customWebServer.checkRequest(request));
    }

    @Test
    public void checkRequestShouldReturnFalseIfAcceptIsNotPresentNegative(){
        Request request = new Request();
        request.setMethod("GET");
        request.setUri("/api/stats");
        request.setVersion("HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("HOST", "blablabla");
        headers.put("USER-AGENT", "blablabla");
        request.setHeaders(headers);
        assertFalse(customWebServer.checkRequest(request));
    }

    @Test
    public void checkRequestShouldReturnFalseIfHostIsNotPresentNegative(){
        Request request = new Request();
        request.setMethod("GET");
        request.setUri("/api/stats");
        request.setVersion("HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("ACCEPT", "TEXT/HTML");
        headers.put("USER-AGENT", "blablabla");
        request.setHeaders(headers);
        assertFalse(customWebServer.checkRequest(request));
    }

    @Test
    public void checkRequestShouldReturnFalseIfUserAgentIsNotPresentNegative(){
        Request request = new Request();
        request.setMethod("GET");
        request.setUri("/api/stats");
        request.setVersion("HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("ACCEPT", "TEXT/HTML");
        headers.put("HOST", "blablabla");
        request.setHeaders(headers);
        assertFalse(customWebServer.checkRequest(request));
    }

    @Test
    public void checkRequestShouldReturnFalseIfAcceptHasNoTextOrImageOrApplicationNegative(){
        Request request = new Request();
        request.setMethod("GET");
        request.setUri("/api/stats");
        request.setVersion("HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("ACCEPT", "SOMETHING/ELSE");
        headers.put("HOST", "blablabla");
        headers.put("USER-AGENT", "blablabla");
        request.setHeaders(headers);
        assertFalse(customWebServer.checkRequest(request));
    }
}
