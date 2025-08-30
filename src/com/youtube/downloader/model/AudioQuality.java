package com.youtube.downloader.model;

/**
 * Enum representing audio quality options
 */
public enum AudioQuality {
    LOW("128K", "128 kbps"),
    MEDIUM("192K", "192 kbps"),
    HIGH("320K", "320 kbps");

    private final String value;
    private final String description;

    AudioQuality(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
