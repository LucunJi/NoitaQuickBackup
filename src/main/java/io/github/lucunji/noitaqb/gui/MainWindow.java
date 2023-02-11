package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.config.ConfigManager;
import io.github.lucunji.noitaqb.model.Backup;
import io.github.lucunji.noitaqb.utils.ArchiveMode;
import io.github.lucunji.noitaqb.utils.BackupUtils;
import org.apache.commons.compress.archivers.ArchiveException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.lucunji.noitaqb.utils.SwingUIFactory.create;

public class MainWindow {
    public JPanel rootPane;
    private final JButton backupButton;
    private final JButton loadButton;
    private final JButton qbButton;
    private final JPanel backupsPanel;
    private final JButton refreshButton;

    private final ConfigManager cfgManager;

    private final ButtonGroup backupEntriesGroup;

    public MainWindow(ConfigManager cfgManager) {
        this.cfgManager = cfgManager;

        rootPane = create(new JPanel()).layout(new BorderLayout())
                .child(create(new JTabbedPane())
                        .child("Backup",
                                create(new JPanel()).layout(new BorderLayout())
                                        .child(new JScrollPane(
                                                        create(backupsPanel = new JPanel()).layout(new BoxLayout(backupsPanel, BoxLayout.Y_AXIS)).finish()
                                                ), BorderLayout.CENTER
                                        ).child(create(new JPanel()).layout(new FlowLayout())
                                                .children(
                                                        backupButton = new JButton("Backup"),
                                                        qbButton = new JButton("Quick Backup"),
                                                        loadButton = new JButton("Load"),
                                                        refreshButton = new JButton("Refresh")
                                                ).finish(), BorderLayout.SOUTH
                                        ).finish()).finish()).finish();

        backupButton.addActionListener(this::onBackupButtonClicked);
        loadButton.addActionListener(this::onLoadButtonClicked);
        qbButton.addActionListener(this::onQbButtonClicked);
        refreshButton.addActionListener(this::onRefreshButtonClicked);
        backupsPanel.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentRemoved(ContainerEvent e) {
                var component = e.getChild();
                if (component instanceof BackupEntryPanel) {
                    ((BackupEntryPanel) component).deregisterRadioButton(backupEntriesGroup);
                }
            }
        });

        backupEntriesGroup = new ButtonGroup();

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
        Path path = null;
        for (var c : backupsPanel.getComponents()) {
            if (c instanceof BackupEntryPanel && ((BackupEntryPanel) c).isSelected()) {
                path = ((BackupEntryPanel) c).getBackup().getPath();
                break;
            }
        }
        if (path == null) {
            JOptionPane.showMessageDialog(this.rootPane, "No backup is selected");
            return;
        }
        if (!path.toFile().isFile()) {
            JOptionPane.showMessageDialog(this.rootPane, "Backup not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var replacedSaveName = "quickbackup-" + System.currentTimeMillis() + " (replaced)";
        addBackup(replacedSaveName);

        try {
            BackupUtils.loadBackup(path, cfgManager.getConfigs().getGeneral().getSavePath());
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }
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
        return new BackupEntryPanel(backup, backupEntriesGroup);
    }
}
