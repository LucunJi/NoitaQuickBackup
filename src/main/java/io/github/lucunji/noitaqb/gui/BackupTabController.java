package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.Main;
import io.github.lucunji.noitaqb.model.Backup;
import io.github.lucunji.noitaqb.archive.ArchiveMode;
import io.github.lucunji.noitaqb.utils.BackupUtils;
import io.github.lucunji.noitaqb.utils.SwingUtils;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class BackupTabController {
    private final BackupTab backupTab;

    protected final ContainerAdapter backupEntryContainerListener;

    protected BackupTabController(BackupTab backupTab) {
        this.backupTab = backupTab;

        this.backupEntryContainerListener = new ContainerAdapter() {
            @Override
            public void componentRemoved(ContainerEvent e) {
                var component = e.getChild();
                if (component instanceof BackupDisplayEntry) removeBackupDisplayEntry(((BackupDisplayEntry) component));
            }
        };
    }

    protected void onBackupButtonClicked(ActionEvent ignoredEvent) {
        var name = JOptionPane.showInputDialog(this.backupTab, "Backup name", "backup-" + System.currentTimeMillis());
        if (name == null) return;
        try {
            addNewBackup(name);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Backup", JOptionPane.ERROR_MESSAGE);
            reloadBackups();
        }
    }

    protected void onQbButtonClicked(ActionEvent ignoredEvent) {
        var name = "quick-backup-" + System.currentTimeMillis();
        try {
            addNewBackup(name);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Quick backup", JOptionPane.ERROR_MESSAGE);
            reloadBackups();
        }
    }

    protected void onLoadButtonClicked(ActionEvent ignoredEvent) {
        var path = getSelectedBackupDisplayEntry();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(backupTab, "No backup is selected");
            return;
        }

        var replacedSaveName = "preload-backup-" + System.currentTimeMillis();
        try {
            addNewBackup(replacedSaveName, () -> {
                try {
                    BackupUtils.loadBackup(path.get(), Main.cfgManager.getConfigs().getGeneral().getSavePath());
                    JOptionPane.showMessageDialog(this.backupTab, "Save loaded", "Load", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Load", JOptionPane.ERROR_MESSAGE);
                    reloadBackups();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Load", JOptionPane.ERROR_MESSAGE);
            reloadBackups();
        }
    }

    protected void reloadBackups() {
        String backupPath = Main.cfgManager.getConfigs().getGeneral().getBackupPath();
        Backup[] backups;
        try {
            backups = BackupUtils.listBackups(backupPath);
        } catch (Exception e) {
            // skip if failed
            e.printStackTrace();
            System.out.println("Could not load backups in directory: " + backupPath);
            return;
        }

        backupTab.backupsPanel.removeAll();
        for (Backup backup : backups) {
            var save = addBackupDisplayEntry(backup);
            backupTab.backupsPanel.add(save);
        }
        SwingUtils.refreshDisplay(backupTab);
    }

    private void addNewBackup(String name) throws IOException {
        this.addNewBackup(name, () -> {});
    }


    private void addNewBackup(String name, Runnable callback) throws IOException {
        final ArchiveMode mode = ArchiveMode.ZIP_ARCHIVE;
        var cfgGeneral = Main.cfgManager.getConfigs().getGeneral();

        // instead of ProgressMonitor, this disables window when showing up
        var label = new JLabel();
        var progress = new JProgressBar();
        var panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Backup to " + name), BorderLayout.NORTH);
        panel.add(label, BorderLayout.CENTER);
        panel.add(progress, BorderLayout.SOUTH);
        var monitor = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null)
            .createDialog(this.backupTab, "Dialog");
        monitor.setPreferredSize(new Dimension(400, monitor.getHeight()));
        monitor.pack();

        var task = BackupUtils.getArchiveTask(name, cfgGeneral.getBackupPath(), cfgGeneral.getSavePath(), mode);
        task.addPropertyChangeListener(event -> {
            switch (event.getPropertyName()) {
                case "finished":
                    label.setText(((Path) event.getNewValue()).toString());
                    break;
                case "progress":
                    progress.setValue((int) event.getNewValue());
                    break;
                case "state":
                    if (task.isDone()) {
                        try {
                            var save = addBackupDisplayEntry(task.get());
                            backupTab.backupsPanel.add(save, 0);
                            SwingUtils.refreshDisplay(backupTab);
                            callback.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Archive", JOptionPane.ERROR_MESSAGE);
                            reloadBackups();
                        }
                        monitor.dispose();
                    }
                    break;
            }
        });
        task.execute();
        monitor.setVisible(true);
    }

    private JPanel addBackupDisplayEntry(Backup backup) {
        var entry = new BackupDisplayEntry(backup);
        backupTab.backupEntryGroup.add(entry.radioButton);
        return entry;
    }

    private void removeBackupDisplayEntry(BackupDisplayEntry entry) {
        backupTab.backupEntryGroup.remove(entry.radioButton);
    }

    private Optional<Path> getSelectedBackupDisplayEntry() {
        Path path = null;
        for (var c : backupTab.backupsPanel.getComponents()) {
            if (c instanceof BackupDisplayEntry && ((BackupDisplayEntry) c).isSelected()) {
                path = ((BackupDisplayEntry) c).getBackup().getPath();
                break;
            }
        }
        return Optional.ofNullable(path);
    }
}
