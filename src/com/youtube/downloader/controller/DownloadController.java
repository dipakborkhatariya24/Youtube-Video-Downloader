package com.youtube.downloader.controller;

import com.youtube.downloader.model.*;
import com.youtube.downloader.view.DownloaderView;
import javax.swing.SwingWorker;
import java.util.Map;
import java.util.List;

/**
 * Controller class coordinating between Model and View
 */
public class DownloadController {

    private DownloadModel model;
    private DownloaderView view;
    private Map<String, String> currentResolutionMap;

    public DownloadController(DownloadModel model, DownloaderView view) {
        this.model = model;
        this.view = view;

        // Set up progress listener
        model.addProgressListener(new DownloadModel.DownloadProgressListener() {
            @Override
            public void onProgressUpdate(String message) {
                view.appendOutput(message);
            }

            @Override
            public void onPercentageUpdate(int percentage) {
                view.setProgress(percentage);
                view.setStatus("Downloading... " + percentage + "%");
            }

            @Override
            public void onDownloadComplete() {
                view.setStatus("Download completed successfully!");
                view.setProgress(100);
                view.appendOutput("\n=== Download completed successfully! ===");
                view.setDownloadButtonEnabled(true);
                view.setProgressIndeterminate(false);
            }

            @Override
            public void onDownloadFailed(String error) {
                view.setStatus("Download failed");
                view.setProgress(0);
                view.appendOutput("\n=== Download failed: " + error + " ===");
                view.setDownloadButtonEnabled(true);
                view.setProgressIndeterminate(false);
            }
        });
    }

    public void onFormatChanged(DownloadFormat format) {
        model.getConfiguration().setFormat(format);
    }

    public void onAudioQualityChanged(AudioQuality quality) {
        model.getConfiguration().setAudioQuality(quality);
    }

    public void onPlaylistChanged(boolean isPlaylist) {
        model.getConfiguration().setPlaylist(isPlaylist);
    }

    public void onPlaylistOptionChanged(String option) {
        model.getConfiguration().setPlaylistOption(option);
    }

    public void onPlaylistRangeChanged(String range) {
        model.getConfiguration().setPlaylistRange(range);
    }

    public void onSubtitleChanged(boolean downloadSubtitles) {
        model.getConfiguration().setDownloadSubtitles(downloadSubtitles);
    }

    public void onSubtitleLanguageChanged(String language) {
        model.getConfiguration().setSubtitleLanguage(language);
    }

    public void onSubtitleEmbedChanged(boolean embed) {
        model.getConfiguration().setEmbedSubtitles(embed);
    }

    public void onResolutionChanged(String resolution) {
        model.getConfiguration().setSelectedResolution(resolution);
    }

    public void onOutputDirectoryChanged(String directory) {
        model.getConfiguration().setOutputDirectory(directory);
    }

    public void onFetchFormats(String url) {
        if (!model.isValidUrl(url)) {
            view.showError("Please enter a valid YouTube URL.");
            return;
        }

        view.setFetchFormatsButtonEnabled(false);
        view.setStatus("Fetching available formats...");

        SwingWorker<Map<String, String>, Void> worker = new SwingWorker<Map<String, String>, Void>() {
            @Override
            protected Map<String, String> doInBackground() throws Exception {
                return model.fetchAvailableResolutions(url);
            }

            @Override
            protected void done() {
                try {
                    currentResolutionMap = get();
                    view.updateResolutions(currentResolutionMap);
                } catch (Exception e) {
                    view.setStatus("Failed to fetch formats");
                    view.appendOutput("Error: " + e.getMessage());
                }
                view.setFetchFormatsButtonEnabled(true);
            }
        };

        worker.execute();
    }

    public void onStartDownload(String url) {
        // Validate inputs
        if (!model.isValidUrl(url)) {
            view.showError("Please enter a valid YouTube URL.");
            return;
        }

        if (view.isMp4Selected() && !view.isPlaylistSelected() &&
                (view.getSelectedResolution() == null || view.getResolutionCount() == 0)) {
            view.showWarning("Please fetch available formats and select a resolution for MP4 download.");
            return;
        }

        // Update model configuration
        model.getConfiguration().setUrl(url);

        // Start download
        view.setDownloadButtonEnabled(false);
        view.setStatus("Downloading...");
        view.setProgressIndeterminate(true);

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                model.executeDownload();
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String line : chunks) {
                    view.appendOutput(line);
                }
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                } catch (Exception e) {
                    //model.getDownloadProgressListeners().get(0).onDownloadFailed(e.getMessage());
                    view.showError("Download failed: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }
}