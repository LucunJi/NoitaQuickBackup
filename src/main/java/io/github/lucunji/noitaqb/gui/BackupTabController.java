package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.Main;
import io.github.lucunji.noitaqb.model.Backup;
import io.github.lucunji.noitaqb.utils.ArchiveMode;
import io.github.lucunji.noitaqb.utils.BackupUtils;
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
                if (component instanceof BackupEntryPanel) {
                    ((BackupEntryPanel) component).deregisterRadioButton(backupTab.backupEntryGroup);
                }
            }
        };
    }

    protected void onBackupButtonClicked(ActionEvent ignoredEvent) {
        var name = JOptionPane.showInputDialog("Backup name", "backup-" + System.currentTimeMillis());
        if (name == null) return;
        try {
            addBackupEntry(name);
        } catch (IOException | ArchiveException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Backup", JOptionPane.ERROR_MESSAGE);
            reloadBackups();
        }
    }

    protected void onQbButtonClicked(ActionEvent ignoredEvent) {
        var name = "quick-backup-" + System.currentTimeMillis();
        try {
            addBackupEntry(name);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Quick backup", JOptionPane.ERROR_MESSAGE);
            reloadBackups();
        }
    }

    protected void onLoadButtonClicked(ActionEvent ignoredEvent) {
        var path = getSelectedBackupEntry();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(backupTab, "No backup is selected");
            return;
        }

        try {
            var replacedSaveName = "preload-backup-" + System.currentTimeMillis();
            addBackupEntry(replacedSaveName);
            BackupUtils.loadBackup(path.get(), Main.cfgManager.getConfigs().getGeneral().getSavePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupTab, e.getMessage(), "Load save", JOptionPane.ERROR_MESSAGE);
            reloadBackups();
        }
    }

    protected void reloadBackups() {
        String backupPath = Main.cfgManager.getConfigs().getGeneral().getBackupPath();
        Backup[] backups;
        try {
            backups = BackupUtils.loadBackups(backupPath);
        } catch (Exception e) {
            // skip if failed
            e.printStackTrace();
            System.out.println("Could not load backups in directory " + backupPath);
            return;
        }

        backupTab.backupsPanel.removeAll();
        for (Backup backup : backups) {
            var save = backupTab.makeBackupPanel(backup);
            backupTab.backupsPanel.add(save);
        }
        backupTab.refreshBackupEntryPanel();
    }

    private void addBackupEntry(String name) throws IOException, ArchiveException {
        final ArchiveMode mode = ArchiveMode.ZIP_ARCHIVE;
        var cfgGeneral = Main.cfgManager.getConfigs().getGeneral();
        Backup backup = BackupUtils.makeBackup(name, cfgGeneral.getBackupPath(), cfgGeneral.getSavePath(), mode);
        var save = backupTab.makeBackupPanel(backup);
        backupTab.backupsPanel.add(save, 0);
        backupTab.refreshBackupEntryPanel();
    }

    private Optional<Path> getSelectedBackupEntry() {
        Path path = null;
        for (var c : backupTab.backupsPanel.getComponents()) {
            if (c instanceof BackupEntryPanel && ((BackupEntryPanel) c).isSelected()) {
                path = ((BackupEntryPanel) c).getBackup().getPath();
                break;
            }
        }
        return Optional.ofNullable(path);
    }
}
