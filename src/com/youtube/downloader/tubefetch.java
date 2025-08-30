package com.youtube.downloader;

import com.youtube.downloader.model.DownloadModel;
import com.youtube.downloader.view.DownloaderView;
import com.youtube.downloader.controller.DownloadController;
import javax.swing.*;

/**
 * Main application class for YouTube Video Downloader with MVC architecture
 * @author Dipak Borkhatariya
 */
public class tubefetch {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create MVC components
            DownloadModel model = new DownloadModel();
            DownloaderView view = new DownloaderView();
            DownloadController controller = new DownloadController(model, view);

            // Connect view to controller
            view.setController(controller);

            // Display the view
            view.setVisible(true);
        });
    }
}