package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.config.ConfigManager;
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

public class BackupTabController {
    private final BackupTab backupTab;

    private final ConfigManager cfgManager;
    protected final ContainerAdapter backupEntryContainerListener;

    protected BackupTabController(BackupTab backupTab, ConfigManager cfgManager) {
        this.backupTab = backupTab;
        this.cfgManager = cfgManager;

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
        addBackupEntry(name);
    }

    protected void onQbButtonClicked(ActionEvent ignoredEvent) {
        var name = "quickbackup-" + System.currentTimeMillis();
        addBackupEntry(name);
    }

    protected void onLoadButtonClicked(ActionEvent ignoredEvent) {
        Path path = null;
        for (var c : backupTab.backupsPanel.getComponents()) {
            if (c instanceof BackupEntryPanel && ((BackupEntryPanel) c).isSelected()) {
                path = ((BackupEntryPanel) c).getBackup().getPath();
                break;
            }
        }
        if (path == null) {
            JOptionPane.showMessageDialog(backupTab, "No backup is selected");
            return;
        }
        if (!path.toFile().isFile()) {
            JOptionPane.showMessageDialog(backupTab, "Backup not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var replacedSaveName = "quickbackup-" + System.currentTimeMillis() + " (replaced)";
        addBackupEntry(replacedSaveName);

        try {
            BackupUtils.loadBackup(path, cfgManager.getConfigs().getGeneral().getSavePath());
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onRefreshButtonClicked(ActionEvent ignoredEvent) {reloadBackups();}

    protected void reloadBackups() {
        backupTab.backupsPanel.removeAll();

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
            var save = backupTab.makeBackupPanel(backup);
            backupTab.backupsPanel.add(save);
        }

        backupTab.refreshBackupEntryPanel();
    }

    protected void addBackupEntry(String name) {
        final ArchiveMode mode = ArchiveMode.ZIP_ARCHIVE;

        var cfgGeneral = cfgManager.getConfigs().getGeneral();
        Backup backup;
        try {
            backup = BackupUtils.makeBackup(name, cfgGeneral.getBackupPath(), cfgGeneral.getSavePath(), mode);
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }
        var save = backupTab.makeBackupPanel(backup);
        backupTab.backupsPanel.add(save, 0);

        backupTab.refreshBackupEntryPanel();
    }
}
