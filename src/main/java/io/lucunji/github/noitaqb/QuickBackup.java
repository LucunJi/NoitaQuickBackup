package io.lucunji.github.noitaqb;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class QuickBackup {
    public JPanel rootPane;
    private JButton backupButton;
    private JButton loadButton;
    private JButton qbButton;
    private JPanel savesPanel;

    public QuickBackup() {
        var layout = new BoxLayout(savesPanel, BoxLayout.Y_AXIS);
        savesPanel.setLayout(layout);

        backupButton.addActionListener(this::onSaveButtonClicked);
        loadButton.addActionListener(this::onLoadButtonClicked);
        qbButton.addActionListener(this::onQSaveButtonClicked);
    }

    private void onQSaveButtonClicked(ActionEvent event) {
        var name = "quickbackup-" + System.currentTimeMillis();
        try {
            FileUtils.makeBackup(name);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        addBackup(name);
    }

    private void onSaveButtonClicked(ActionEvent event) {
        var name = Optional.ofNullable(JOptionPane.showInputDialog(
                "Backup name",
                "backup-" + System.currentTimeMillis()
        ));
        if (name.isEmpty()) return;

        try {
            FileUtils.makeBackup(name.get());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        addBackup(name.get());
    }

    private void addBackup(String name) {
        // Components
        var save = new JPanel(new GridLayout(2, 2));
        save.add(new JLabel(name));
        save.add(new JLabel("yyyy-mm-dd hh:mm:ss"));
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

    private void onLoadButtonClicked(ActionEvent event) {
    }
}
