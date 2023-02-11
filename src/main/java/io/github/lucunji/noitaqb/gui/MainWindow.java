package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.config.ConfigManager;

import javax.swing.*;
import java.awt.*;

import static io.github.lucunji.noitaqb.utils.SwingUIFactory.create;

public class MainWindow {
    private final JPanel rootPane;

    public MainWindow(ConfigManager cfgManager) {
        rootPane = create(new JPanel()).layout(new BorderLayout())
                .child(create(new JTabbedPane())
                        .child("Backup", new BackupTab(cfgManager)).finish()
                ).finish();
    }

    public JPanel getRootPane() {
        return rootPane;
    }
}
