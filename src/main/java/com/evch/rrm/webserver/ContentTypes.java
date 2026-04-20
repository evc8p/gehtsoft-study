package com.evch.rrm.webserver;

import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
public enum ContentTypes {
    SLASH("/", "text/html"),
    INDEX_HTML("index.html", "text/html"),
    HTML(".html", "text/html"),
    TEXT(".html", "text/html"),
    CSS(".css", "text/css"),
    ICO(".ico", "image/x-icon"),
    GIF(".gif", "image/gif"),
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    JS(".js", "application/javascript"),
    JSON(".json", "application/json");

    private final String extension;
    private final String contentType;

    ContentTypes(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentTypes getContentType(String extension) {
        for (ContentTypes contentType : values()) {
            if (extension.contains(contentType.extension)) {
                return contentType;
            }
        }
        throw new NoSuchElementException("Missing such extension: " + extension);
    }
}
