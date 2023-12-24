package io.github.lucunji.noitaqb.gui;

import javax.swing.*;
import java.awt.*;


import static io.github.lucunji.noitaqb.utils.SwingUIBuilder.create;

public class BackupTab extends JPanel {
    final JPanel backupsPanel;
    final ButtonGroup backupEntryGroup;

    public BackupTab() {
        super();
        BackupTabController controller = new BackupTabController(this);

        this.setLayout(new BorderLayout());

        // pane listing backups
        this.add(new JScrollPane(
                create(backupsPanel = new JPanel()).boxLayout(BoxLayout.Y_AXIS).finish()
        ), BorderLayout.CENTER);

        // buttons
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
}
