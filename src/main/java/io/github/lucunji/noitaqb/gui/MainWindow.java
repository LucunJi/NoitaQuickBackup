package io.github.lucunji.noitaqb.gui;

import javax.swing.*;
import java.awt.*;

import static io.github.lucunji.noitaqb.utils.SwingUIBuilder.create;

public class MainWindow {
    private final JPanel rootPane;

    public MainWindow() {
        rootPane = create(new JPanel()).layout(new BorderLayout())
                .child(create(new JTabbedPane())
                        .child("Backup", new BackupTab()).finish()
                ).finish();
    }

    public JPanel getRootPane() {
        return rootPane;
    }
}
