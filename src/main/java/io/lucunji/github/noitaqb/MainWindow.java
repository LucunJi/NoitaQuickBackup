package io.lucunji.github.noitaqb;

import io.lucunji.github.noitaqb.model.Backup;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainWindow {
    public JPanel rootPane;
    private JButton backupButton;
    private JButton loadButton;
    private JButton qbButton;
    private JPanel savesPanel;

    public MainWindow() {
        var layout = new BoxLayout(savesPanel, BoxLayout.Y_AXIS);
        savesPanel.setLayout(layout);

        backupButton.addActionListener(this::onSaveButtonClicked);
        loadButton.addActionListener(this::onLoadButtonClicked);
        qbButton.addActionListener(this::onQSaveButtonClicked);
    }

    private void onSaveButtonClicked(ActionEvent event) {
        var name = Optional.ofNullable(JOptionPane.showInputDialog(
                "Backup name",
                "backup-" + System.currentTimeMillis()
        ));
        if (name.isEmpty()) return;

        try {
            var backup = FileUtils.makeBackup(name.get());
            addBackup(backup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBackup(Backup backup) {
        // Components
        var save = new JPanel(new GridLayout(2, 2));
        save.add(new JLabel(backup.getName()));
        var time = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(backup.getTime().toInstant());
        save.add(new JLabel(time));
        // TODO: actual seed
        save.add(new JLabel("seed: ######"));

        // Styling
        save.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 0, 5),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));

        // Resizing
        save.setMaximumSize(new Dimension(save.getMaximumSize().width, save.getPreferredSize().height));

        // Add and refresh
        savesPanel.add(save, 0);
        savesPanel.revalidate();
    }

    private void onQSaveButtonClicked(ActionEvent event) {
        var name = "quickbackup-" + System.currentTimeMillis();
        try {
            var backup = FileUtils.makeBackup(name);
            addBackup(backup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLoadButtonClicked(ActionEvent event) {
    }
}
