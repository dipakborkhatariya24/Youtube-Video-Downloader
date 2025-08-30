package com.youtube.downloader.model;
/**
 * Enum representing download format types
 */
public enum DownloadFormat {
    MP3("mp3"),
    MP4("mp4");

    private final String extension;

    DownloadFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
