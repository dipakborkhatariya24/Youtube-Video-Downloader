package com.youtube.downloader.model;

/**
 * Class representing video resolution information
 */
public class ResolutionInfo {
    private final String resolution;
    private final String formatCode;

    public ResolutionInfo(String resolution, String formatCode) {
        this.resolution = resolution;
        this.formatCode = formatCode;
    }

    public String getResolution() {
        return resolution;
    }

    public String getFormatCode() {
        return formatCode;
    }
}
