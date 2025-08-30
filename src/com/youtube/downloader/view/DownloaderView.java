package com.youtube.downloader.view;

import com.youtube.downloader.model.*;
import com.youtube.downloader.controller.DownloadController;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * View class managing the GUI
 */
public class DownloaderView extends JFrame {

    // GUI Components
    private JTextField urlField;
    private JRadioButton mp3Radio, mp4Radio;
    private JComboBox<AudioQuality> audioQualityBox;
    private JCheckBox playlistCheckBox;
    private JComboBox<String> playlistOptionBox;
    private JTextField playlistRangeField;
    private JCheckBox subtitleCheckBox;
    private JTextField subtitleLangField;
    private JRadioButton embedSubsRadio, separateSubsRadio;
    private JComboBox<String> resolutionBox;
    private JTextField outputDirField;
    private JButton browseButton, fetchFormatsButton, downloadButton, clearButton;
    private JTextArea outputArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JScrollPane outputScrollPane;

    private DownloadController controller;

    public DownloaderView() {
        initializeGUI();
        initializeDefaults();
    }

    public void setController(DownloadController controller) {
        this.controller = controller;
        setupEventHandlers();
    }

    private void initializeGUI() {
        setTitle("YT Downloader v3.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add components
        mainPanel.add(createTitlePanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createUrlPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createFormatPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createPlaylistPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createSubtitlePanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createResolutionPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createOutputPanel());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createButtonPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createProgressPanel());
        mainPanel.add(Box.createVerticalStrut(10));

        // Create scroll pane for the main content
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add output area at bottom
        JPanel outputPanel = createOutputAreaPanel();

        // Add to frame
        add(mainScrollPane, BorderLayout.CENTER);
        //add(outputPanel, BorderLayout.SOUTH);

        // Set size and center
        setSize(900, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("YouTube Video Downloader");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createUrlPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(createTitledBorder("YouTube URL"));

        JLabel label = new JLabel("Enter URL:");
        urlField = new JTextField();
        urlField.setFont(new Font("Arial", Font.PLAIN, 12));
        urlField.setPreferredSize(new Dimension(0, 30));

        panel.add(label, BorderLayout.WEST);
        panel.add(urlField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormatPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createTitledBorder("Download Format"));

        // Format selection
        JPanel formatRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup formatGroup = new ButtonGroup();
        mp3Radio = new JRadioButton("MP3 (Audio Only)", true);
        mp4Radio = new JRadioButton("MP4 (Video)");
        formatGroup.add(mp3Radio);
        formatGroup.add(mp4Radio);
        formatRadioPanel.add(mp3Radio);
        formatRadioPanel.add(mp4Radio);

        // Audio quality selection
        JPanel qualityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        qualityPanel.add(new JLabel("Audio Quality:"));
        audioQualityBox = new JComboBox<>(AudioQuality.values());
        audioQualityBox.setSelectedItem(AudioQuality.MEDIUM);
        qualityPanel.add(audioQualityBox);

        panel.add(formatRadioPanel);
        panel.add(qualityPanel);

        return panel;
    }

    private JPanel createPlaylistPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createTitledBorder("Playlist Options"));

        playlistCheckBox = new JCheckBox("This is a playlist");

        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPanel.add(new JLabel("Option:"));
        playlistOptionBox = new JComboBox<>(new String[]{"Download entire playlist", "Download specific range"});
        playlistOptionBox.setEnabled(false);
        optionPanel.add(playlistOptionBox);

        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rangePanel.add(new JLabel("Range (e.g., 1-5):"));
        playlistRangeField = new JTextField(10);
        playlistRangeField.setEnabled(false);
        rangePanel.add(playlistRangeField);

        panel.add(playlistCheckBox);
        panel.add(optionPanel);
        panel.add(rangePanel);

        return panel;
    }

    private JPanel createSubtitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createTitledBorder("Subtitle Options (MP4 only)"));

        subtitleCheckBox = new JCheckBox("Download subtitles");

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        langPanel.add(new JLabel("Language:"));
        subtitleLangField = new JTextField("en", 8);
        subtitleLangField.setEnabled(false);
        langPanel.add(subtitleLangField);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup subtitleGroup = new ButtonGroup();
        embedSubsRadio = new JRadioButton("Embed in video", true);
        separateSubsRadio = new JRadioButton("Save separately");
        subtitleGroup.add(embedSubsRadio);
        subtitleGroup.add(separateSubsRadio);
        embedSubsRadio.setEnabled(false);
        separateSubsRadio.setEnabled(false);
        radioPanel.add(embedSubsRadio);
        radioPanel.add(separateSubsRadio);

        panel.add(subtitleCheckBox);
        panel.add(langPanel);
        panel.add(radioPanel);

        return panel;
    }

    private JPanel createResolutionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(createTitledBorder("Video Resolution (MP4 only)"));

        resolutionBox = new JComboBox<>();
        resolutionBox.setPreferredSize(new Dimension(200, 25));
        resolutionBox.setEnabled(false);

        fetchFormatsButton = new JButton("Fetch Available Formats");
        fetchFormatsButton.setEnabled(false);

        panel.add(resolutionBox);
        panel.add(fetchFormatsButton);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(createTitledBorder("Output Directory"));

        outputDirField = new JTextField("E:\\Java\\Java Projects\\Youtube Video Downloader\\Downloaded Videos");
        outputDirField.setEditable(false);
        outputDirField.setPreferredSize(new Dimension(0, 25));

        browseButton = new JButton("Browse...");

        panel.add(outputDirField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        downloadButton = new JButton("Start Download");
        downloadButton.setPreferredSize(new Dimension(150, 35));
        downloadButton.setBackground(new Color(52, 152, 219));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFont(new Font("Arial", Font.BOLD, 12));

        clearButton = new JButton("Clear Output");
        clearButton.setPreferredSize(new Dimension(150, 35));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(downloadButton);
        panel.add(clearButton);

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createTitledBorder("Progress"));

        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 20));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(progressBar);

        return panel;
    }


    private JPanel createOutputAreaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("Output Log"));
        panel.setPreferredSize(new Dimension(0, 200));

        outputArea = new JTextArea(10, 0);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        outputArea.setBackground(new Color(248, 248, 248));

        outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(outputScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupEventHandlers() {
        // Format selection handlers
        mp3Radio.addActionListener(e -> {
            controller.onFormatChanged(DownloadFormat.MP3);
            toggleFormatControls();
        });

        mp4Radio.addActionListener(e -> {
            controller.onFormatChanged(DownloadFormat.MP4);
            toggleFormatControls();
        });

        // Audio quality handler
        audioQualityBox.addActionListener(e -> {
            AudioQuality quality = (AudioQuality) audioQualityBox.getSelectedItem();
            if (quality != null) {
                controller.onAudioQualityChanged(quality);
            }
        });

        // Playlist handlers
        playlistCheckBox.addActionListener(e -> {
            controller.onPlaylistChanged(playlistCheckBox.isSelected());
            togglePlaylistControls();
        });

        playlistOptionBox.addActionListener(e -> {
            String option = (String) playlistOptionBox.getSelectedItem();
            if (option != null) {
                controller.onPlaylistOptionChanged(option);
            }
            toggleRangeField();
        });

        playlistRangeField.addActionListener(e -> {
            controller.onPlaylistRangeChanged(playlistRangeField.getText());
        });

        // Subtitle handlers
        subtitleCheckBox.addActionListener(e -> {
            controller.onSubtitleChanged(subtitleCheckBox.isSelected());
            toggleSubtitleControls();
        });

        subtitleLangField.addActionListener(e -> {
            controller.onSubtitleLanguageChanged(subtitleLangField.getText());
        });

        embedSubsRadio.addActionListener(e -> {
            controller.onSubtitleEmbedChanged(true);
        });

        separateSubsRadio.addActionListener(e -> {
            controller.onSubtitleEmbedChanged(false);
        });

        // Resolution handler
        resolutionBox.addActionListener(e -> {
            String resolution = (String) resolutionBox.getSelectedItem();
            if (resolution != null) {
                controller.onResolutionChanged(resolution);
            }
        });

        // Directory browser
        browseButton.addActionListener(e -> browseOutputDirectory());

        // Format fetcher
        fetchFormatsButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            controller.onFetchFormats(url);
        });

        // Download button
        downloadButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            controller.onStartDownload(url);
        });

        // Clear button
        clearButton.addActionListener(e -> {
            clearOutput();
        });
    }

    private void initializeDefaults() {
        toggleFormatControls();
        togglePlaylistControls();
        toggleSubtitleControls();
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title);
        border.setTitleFont(new Font("Arial", Font.BOLD, 12));
        return border;
    }

    public void toggleFormatControls() {
        boolean isMp4 = mp4Radio.isSelected();

        // Enable/disable MP4-specific controls
        fetchFormatsButton.setEnabled(isMp4);
        resolutionBox.setEnabled(isMp4 && resolutionBox.getItemCount() > 0);

        // Subtitle controls are only for MP4
        subtitleCheckBox.setEnabled(isMp4);
        if (!isMp4) {
            subtitleCheckBox.setSelected(false);
            toggleSubtitleControls();
        }
    }

    public void togglePlaylistControls() {
        boolean isPlaylist = playlistCheckBox.isSelected();
        playlistOptionBox.setEnabled(isPlaylist);

        if (isPlaylist) {
            toggleRangeField();
        } else {
            playlistRangeField.setEnabled(false);
        }
    }

    public void toggleRangeField() {
        boolean isRangeOption = playlistOptionBox.getSelectedItem() != null &&
                playlistOptionBox.getSelectedItem().equals("Download specific range");
        playlistRangeField.setEnabled(isRangeOption);
    }

    public void toggleSubtitleControls() {
        boolean subsEnabled = subtitleCheckBox.isSelected() && mp4Radio.isSelected();
        subtitleLangField.setEnabled(subsEnabled);
        embedSubsRadio.setEnabled(subsEnabled);
        separateSubsRadio.setEnabled(subsEnabled);
    }

    private void browseOutputDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Output Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(outputDirField.getText()).exists() ?
                new File(outputDirField.getText()) : new File(System.getProperty("user.home")));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String dir = chooser.getSelectedFile().getAbsolutePath();
            outputDirField.setText(dir);
            controller.onOutputDirectoryChanged(dir);
        }
    }

    public void updateResolutions(Map<String, String> resolutions) {
        SwingUtilities.invokeLater(() -> {
            resolutionBox.removeAllItems();
            for (String resolution : resolutions.keySet()) {
                resolutionBox.addItem(resolution);
            }
            if (!resolutions.isEmpty()) {
                resolutionBox.setEnabled(true);
                setStatus("Formats loaded successfully");
            } else {
                setStatus("No MP4 formats found");
            }
        });
    }

    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public void clearOutput() {
        outputArea.setText("");
        statusLabel.setText("Ready");
        progressBar.setValue(0);
        progressBar.setString("");
    }

    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }

    public void setProgress(int percentage) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setValue(percentage);
            progressBar.setString(percentage + "%");
        });
    }

    public void setProgressIndeterminate(boolean indeterminate) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(indeterminate);
        });
    }

    public void setDownloadButtonEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            downloadButton.setEnabled(enabled);
        });
    }

    public void setFetchFormatsButtonEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            fetchFormatsButton.setEnabled(enabled);
        });
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public boolean isMp4Selected() {
        return mp4Radio.isSelected();
    }

    public boolean isPlaylistSelected() {
        return playlistCheckBox.isSelected();
    }

    public String getSelectedResolution() {
        return (String) resolutionBox.getSelectedItem();
    }

    public int getResolutionCount() {
        return resolutionBox.getItemCount();
    }
}

/** for show the Ouput log in output screen just remove the comment from this line:
 add(outputPanel, BorderLayout.SOUTH);
 */
