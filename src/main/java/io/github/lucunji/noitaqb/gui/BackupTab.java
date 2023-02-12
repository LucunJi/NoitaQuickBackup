package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.model.Backup;

import javax.swing.*;
import java.awt.*;


import static io.github.lucunji.noitaqb.utils.SwingUIBuilder.create;

public class BackupTab extends JPanel {
    protected final JPanel backupsPanel;
    protected final ButtonGroup backupEntryGroup;

    public BackupTab() {
        super();
        BackupTabController controller = new BackupTabController(this);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(
                create(backupsPanel = new JPanel()).layout(new BoxLayout(backupsPanel, BoxLayout.Y_AXIS)).finish()
        ), BorderLayout.CENTER);
        this.add(create(new JPanel()).layout(new FlowLayout())
                .children(
                        create(new JButton("Backup")).onAction(controller::onBackupButtonClicked).finish(),
                        create(new JButton("Quick Backup")).onAction(controller::onQbButtonClicked).finish(),
                        create(new JButton("Load")).onAction(controller::onLoadButtonClicked).finish()
                ).finish(), BorderLayout.SOUTH);

        backupEntryGroup = new ButtonGroup();
        backupsPanel.addContainerListener(controller.backupEntryContainerListener);

        controller.reloadBackups();
    }

    protected void refreshBackupEntryPanel() {
        backupsPanel.repaint();
        backupsPanel.revalidate();
    }

    protected JPanel makeBackupPanel(Backup backup) {return new BackupEntryPanel(backup, backupEntryGroup);}
}
