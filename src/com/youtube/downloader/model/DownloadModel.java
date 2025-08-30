package com.youtube.downloader.model;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 * Model class handling the core download logic
 */
public class DownloadModel {
    private static final Logger LOGGER = Logger.getLogger(DownloadModel.class.getName());
    private static final String DEFAULT_OUTPUT_TEMPLATE = "%(title)s.%(ext)s";

    private Map<String, String> resolutionMap = new LinkedHashMap<>();
    private DownloadConfiguration configuration;
    private List<DownloadProgressListener> progressListeners = new ArrayList<>();

    public DownloadModel() {
        this.configuration = new DownloadConfiguration();
    }

    // Progress listener interface
    public interface DownloadProgressListener {
        void onProgressUpdate(String message);
        void onPercentageUpdate(int percentage);
        void onDownloadComplete();
        void onDownloadFailed(String error);
    }

    public void addProgressListener(DownloadProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(DownloadProgressListener listener) {
        progressListeners.remove(listener);
    }

    private void notifyProgress(String message) {
        for (DownloadProgressListener listener : progressListeners) {
            listener.onProgressUpdate(message);
        }
    }

    private void notifyPercentage(int percentage) {
        for (DownloadProgressListener listener : progressListeners) {
            listener.onPercentageUpdate(percentage);
        }
    }

    private void notifyComplete() {
        for (DownloadProgressListener listener : progressListeners) {
            listener.onDownloadComplete();
        }
    }

    private void notifyFailed(String error) {
        for (DownloadProgressListener listener : progressListeners) {
            listener.onDownloadFailed(error);
        }
    }

    public DownloadConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DownloadConfiguration configuration) {
        this.configuration = configuration;
    }

    public Map<String, String> getResolutionMap() {
        return new LinkedHashMap<>(resolutionMap);
    }

    public boolean isValidUrl(String url) {
        return url != null && !url.isEmpty() &&
                (url.contains("youtube.com") || url.contains("youtu.be"));
    }

    public Map<String, String> fetchAvailableResolutions(String url) throws Exception {
        resolutionMap.clear();

        ProcessBuilder pb = new ProcessBuilder("yt-dlp", "-F", url);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("mp4")) {
                    ResolutionInfo resInfo = parseResolutionLine(line);
                    if (resInfo != null) {
                        resolutionMap.put(resInfo.getResolution(), resInfo.getFormatCode());
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading format list: " + e.getMessage());
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp format listing failed with exit code: " + exitCode);
        }

        return resolutionMap;
    }

    private ResolutionInfo parseResolutionLine(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length > 2 && parts[2].contains("x")) {
            try {
                String resolution = parts[2].split("x")[1] + "p";
                String formatCode = parts[0];
                return new ResolutionInfo(resolution, formatCode);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void executeDownload() throws Exception {
        List<String> command = buildCommand();

        notifyProgress("Starting download with command:");
        notifyProgress(command.toString().replaceAll("[\\[\\],]", "") + "\n");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notifyProgress(line);
                updateProgress(line);
            }
        } catch (IOException e) {
            throw new Exception("Error reading process output: " + e.getMessage());
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp process failed with exit code: " + exitCode);
        }

        notifyComplete();
    }

    private List<String> buildCommand() {
        List<String> command = new ArrayList<>();
        command.add("yt-dlp");

        Path outputPath = Paths.get(configuration.getOutputDirectory(), DEFAULT_OUTPUT_TEMPLATE);
        command.add("-o");
        command.add(outputPath.toString());

        if (configuration.getFormat() == DownloadFormat.MP3) {
            // Audio download
            command.add("-x");
            command.add("--audio-format");
            command.add("mp3");
            command.add("--audio-quality");
            command.add(configuration.getAudioQuality().getValue());
        } else {
            // Video download
            if (configuration.isPlaylist()) {
                command.add("-f");
                command.add("bestvideo+bestaudio");
            } else {
                String formatCode = resolutionMap.get(configuration.getSelectedResolution());
                command.add("-f");
                command.add(formatCode + "+bestaudio");
            }
            command.add("--merge-output-format");
            command.add("mp4");

            // Subtitle options
            if (configuration.isDownloadSubtitles()) {
                if (configuration.isEmbedSubtitles()) {
                    command.add("--embed-subs");
                } else {
                    command.add("--write-subs");
                }
                command.add("--sub-lang");
                command.add(configuration.getSubtitleLanguage());
            }
        }

        // Playlist options
        if (configuration.isPlaylist()) {
            if ("Download entire playlist".equals(configuration.getPlaylistOption())) {
                command.add("--yes-playlist");
            } else {
                String range = configuration.getPlaylistRange();
                if (range != null && !range.trim().isEmpty()) {
                    command.add("--playlist-items");
                    command.add(range.trim());
                }
            }
        }

        command.add(configuration.getUrl());
        return command;
    }

    private void updateProgress(String line) {
        if (line.matches(".*\\[download\\].*?(\\d+\\.\\d+%).*")) {
            try {
                String percentText = line.replaceAll(".*?(\\d+\\.\\d+%).*", "$1");
                double percent = Double.parseDouble(percentText.replace("%", ""));
                notifyPercentage((int) percent);
            } catch (NumberFormatException e) {
                // Ignore parsing errors
            }
        }
    }
}
