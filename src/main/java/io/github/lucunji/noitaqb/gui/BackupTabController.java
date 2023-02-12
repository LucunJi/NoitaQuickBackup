package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.Main;
import io.github.lucunji.noitaqb.model.Backup;
import io.github.lucunji.noitaqb.utils.ArchiveMode;
import io.github.lucunji.noitaqb.utils.BackupUtils;
import io.github.lucunji.noitaqb.utils.SwingUtils;
import org.apache.commons.compress.archivers.ArchiveException;

import javax.swing.*;
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
        var name = JOptionPane.showInputDialog("Backup name", "backup-" + System.currentTimeMillis());
        if (name == null) return;
        try {
            addNewBackup(name);
        } catch (IOException | ArchiveException e) {
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

        try {
            var replacedSaveName = "preload-backup-" + System.currentTimeMillis();
            addNewBackup(replacedSaveName);
            BackupUtils.loadBackup(path.get(), Main.cfgManager.getConfigs().getGeneral().getSavePath());
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

    private void addNewBackup(String name) throws IOException, ArchiveException {
        final ArchiveMode mode = ArchiveMode.ZIP_ARCHIVE;
        var cfgGeneral = Main.cfgManager.getConfigs().getGeneral();
        Backup backup = BackupUtils.makeBackup(name, cfgGeneral.getBackupPath(), cfgGeneral.getSavePath(), mode);
        var save = addBackupDisplayEntry(backup);
        backupTab.backupsPanel.add(save, 0);
        SwingUtils.refreshDisplay(backupTab);
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
