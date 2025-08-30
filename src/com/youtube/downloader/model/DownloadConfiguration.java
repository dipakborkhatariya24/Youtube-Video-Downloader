package com.youtube.downloader.model;
/**
 * Model class representing download configuration
 */
public class DownloadConfiguration {
    private String url;
    private DownloadFormat format;
    private AudioQuality audioQuality;
    private boolean isPlaylist;
    private String playlistOption;
    private String playlistRange;
    private boolean downloadSubtitles;
    private String subtitleLanguage;
    private boolean embedSubtitles;
    private String selectedResolution;
    private String outputDirectory;

    // Constructor
    public DownloadConfiguration() {
        // Set defaults
        this.format = DownloadFormat.MP3;
        this.audioQuality = AudioQuality.MEDIUM;
        this.subtitleLanguage = "en";
        this.embedSubtitles = true;
        this.outputDirectory = "E:\\Java\\Youtube Video Downloader\\downloads";
    }

    // Getters and setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public DownloadFormat getFormat() { return format; }
    public void setFormat(DownloadFormat format) { this.format = format; }

    public AudioQuality getAudioQuality() { return audioQuality; }
    public void setAudioQuality(AudioQuality audioQuality) { this.audioQuality = audioQuality; }

    public boolean isPlaylist() { return isPlaylist; }
    public void setPlaylist(boolean isPlaylist) { this.isPlaylist = isPlaylist; }

    public String getPlaylistOption() { return playlistOption; }
    public void setPlaylistOption(String playlistOption) { this.playlistOption = playlistOption; }

    public String getPlaylistRange() { return playlistRange; }
    public void setPlaylistRange(String playlistRange) { this.playlistRange = playlistRange; }

    public boolean isDownloadSubtitles() { return downloadSubtitles; }
    public void setDownloadSubtitles(boolean downloadSubtitles) { this.downloadSubtitles = downloadSubtitles; }

    public String getSubtitleLanguage() { return subtitleLanguage; }
    public void setSubtitleLanguage(String subtitleLanguage) { this.subtitleLanguage = subtitleLanguage; }

    public boolean isEmbedSubtitles() { return embedSubtitles; }
    public void setEmbedSubtitles(boolean embedSubtitles) { this.embedSubtitles = embedSubtitles; }

    public String getSelectedResolution() { return selectedResolution; }
    public void setSelectedResolution(String selectedResolution) { this.selectedResolution = selectedResolution; }

    public String getOutputDirectory() { return outputDirectory; }
    public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }
}
