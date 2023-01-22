package io.lucunji.github.noitaqb;

import io.lucunji.github.noitaqb.config.ConfigManager;
import io.lucunji.github.noitaqb.utils.ArchiveMode;
import io.lucunji.github.noitaqb.model.Backup;
import io.lucunji.github.noitaqb.utils.BackupUtils;
import org.apache.commons.compress.archivers.ArchiveException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MainWindow {
    public static final String BACKUP_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final String UNKNOWN_STRING = "unknown";

    public JPanel rootPane;
    private JButton backupButton;
    private JButton loadButton;
    private JButton qbButton;
    private JPanel backupsPanel;
    private JButton refreshButton;

    private final ConfigManager cfgManager;

    public MainWindow(ConfigManager cfgManager) {
        this.cfgManager = cfgManager;

        // UI editor in Intellij IDEA does not have BoxLayout
        var layout = new BoxLayout(backupsPanel, BoxLayout.Y_AXIS);
        backupsPanel.setLayout(layout);

        backupButton.addActionListener(this::onBackupButtonClicked);
        loadButton.addActionListener(this::onLoadButtonClicked);
        qbButton.addActionListener(this::onQbButtonClicked);
        refreshButton.addActionListener(this::onRefreshButtonClicked);

        reloadBackups();
    }

    private void onBackupButtonClicked(ActionEvent event) {
        var name = JOptionPane.showInputDialog(
                "Backup name",
                "backup-" + System.currentTimeMillis()
        );
        if (name == null) return;
        addBackup(name);
    }

    private void onQbButtonClicked(ActionEvent event) {
        var name = "quickbackup-" + System.currentTimeMillis();
        addBackup(name);
    }

    private void onLoadButtonClicked(ActionEvent event) {
    }

    private void onRefreshButtonClicked(ActionEvent event) {
        reloadBackups();
    }

    private void reloadBackups() {
        this.backupsPanel.removeAll();

        String backupPath = cfgManager.getConfigs().getGeneral().getBackupPath();
        Backup[] backups;
        try {
            backups = BackupUtils.loadBackups(backupPath);
        } catch (Exception e) {
            backups = new Backup[]{};
            System.out.println("Could not load backups in directory " + backupPath);
            e.printStackTrace();
        }
        for (Backup backup : backups) {
            var save = makeBackupPanel(backup);
            backupsPanel.add(save);
        }
        backupsPanel.repaint();
        backupsPanel.revalidate();
    }

    private void addBackup(String name) {
        final ArchiveMode mode = ArchiveMode.ZIP_ARCHIVE;

        var cfgGeneral = cfgManager.getConfigs().getGeneral();
        Backup backup;
        try {
            backup = BackupUtils.makeBackup(name, cfgGeneral.getBackupPath(), cfgGeneral.getSavePath(), mode);
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }
        var save = makeBackupPanel(backup);
        backupsPanel.add(save, 0);

        backupsPanel.repaint();
        backupsPanel.revalidate();
    }

    private JPanel makeBackupPanel(Backup backup) {
        // Components
        var save = new JPanel(new GridLayout(2, 2));
        save.add(new JLabel(backup.getName()));
        var time = backup.getFileAttributes().map(
                f -> DateTimeFormatter.ofPattern(BACKUP_TIME_PATTERN)
                        .withZone(ZoneId.systemDefault())
                        .format(f.creationTime().toInstant())
        ).orElse(UNKNOWN_STRING);
        save.add(new JLabel(time));
        // TODO: actual seed
        save.add(new JLabel("seed: " + UNKNOWN_STRING));

        // Styling
        save.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 0, 5),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));

        // Resizing
        save.setMaximumSize(new Dimension(save.getMaximumSize().width, save.getPreferredSize().height));
        return save;
    }
}
