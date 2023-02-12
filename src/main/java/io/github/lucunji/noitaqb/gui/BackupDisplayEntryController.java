package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.utils.BackupUtils;
import io.github.lucunji.noitaqb.utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;

public class BackupDisplayEntryController {
    private final BackupDisplayEntry backupDisplayEntry;

    protected final MouseAdapter backupEntryMouseListener;

    public BackupDisplayEntryController(BackupDisplayEntry entry) {
        this.backupDisplayEntry = entry;

        this.backupEntryMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        backupDisplayEntry.radioButton.doClick();
                        break;
                    case MouseEvent.BUTTON3:
                        backupDisplayEntry.showRightClickMenu();
                        break;
                }
            }
        };
    }

    public void onDelete(ActionEvent ignoredEvent) {
        var backupFile = backupDisplayEntry.getBackup().getPath();
        var option = JOptionPane.showConfirmDialog(this.backupDisplayEntry, "Confirm to delete backup file: " + backupFile, "Delete", JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION != option) return;

        try {
            if (Files.exists(backupFile)) BackupUtils.removeBackup(backupFile);
            else JOptionPane.showMessageDialog(this.backupDisplayEntry,
                    "Backup does not exist: " + backupFile, "Delete", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupDisplayEntry, e.getMessage(), "Delete", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var parent = this.backupDisplayEntry.getParent();
        parent.remove(this.backupDisplayEntry);
        SwingUtils.refreshDisplay(parent);
    }

    public void onRename(ActionEvent ignoredEvent) {
        var backup = backupDisplayEntry.getBackup();
        var newName = JOptionPane.showInputDialog(this.backupDisplayEntry, "Rename: " + backup.getName(), backup.getName());
        if (newName == null) return;

        try {
            BackupUtils.renameBackup(backup.getDirectory(),
                    backup.getName() + "." + backup.getExtension(),
                    newName + "." + backup.getExtension());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.backupDisplayEntry, e.getMessage(), "Rename", JOptionPane.ERROR_MESSAGE);
            return;
        }

        backup.setName(newName);
        backupDisplayEntry.updateName(newName);
    }
}
