package io.lucunji.github.noitaqb.gui;

import io.lucunji.github.noitaqb.model.Backup;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class BackupEntryPanel extends JPanel {
    public static final String UNKNOWN_STRING = "unknown";
    public static final String BACKUP_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final String BACKUP_SEED_PATTERN = "seed: %s";

    private final JRadioButton radioButton;
    private final JLabel nameLabel;
    private final JLabel timeLabel;
    private final JLabel seedlabel;
    private final JLabel miscLabel;

    @Getter
    private final Backup backup;

    public BackupEntryPanel(Backup backup, ButtonGroup entriesGroup) {
        super();
        this.backup = backup;

        radioButton = new JRadioButton();
        var timeStr = backup.getFileAttributes().map(
                a -> DateTimeFormatter.ofPattern(BACKUP_TIME_PATTERN)
                        .withZone(ZoneId.systemDefault())
                        .format(a.creationTime().toInstant())
        ).orElse(UNKNOWN_STRING);
        var seedStr = BACKUP_SEED_PATTERN.formatted(
                backup.getSeed().map(Objects::toString).orElse(UNKNOWN_STRING)
        );
        nameLabel = new JLabel(backup.getName());
        timeLabel = new JLabel(timeStr);
        seedlabel = new JLabel(seedStr);
        // TODO: miscLabel of file size and format
        miscLabel = new JLabel(UNKNOWN_STRING);
        final var innerPanel = new JPanel(new GridLayout(2, 2));

        this.setLayout(new BorderLayout());
        this.add(radioButton, BorderLayout.WEST);
        this.add(innerPanel, BorderLayout.CENTER);
        innerPanel.add(nameLabel);
        innerPanel.add(timeLabel);
        innerPanel.add(seedlabel);
        innerPanel.add(miscLabel);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 0, 5),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));

        entriesGroup.add(radioButton);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radioButton.doClick();
            }
        });
    }

    public void deregisterRadioButton(ButtonGroup group) {
        group.remove(this.radioButton);
    }

    public boolean isSelected() {
        return this.radioButton.isSelected();
    }
}
