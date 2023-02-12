package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.model.Backup;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static io.github.lucunji.noitaqb.utils.SwingUIBuilder.create;

public class BackupDisplayEntry extends JPanel {
    public static final String UNKNOWN_STRING = "unknown";
    public static final String BACKUP_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final String BACKUP_SEED_PATTERN = "seed: %s";

    private final JLabel nameLabel;
    protected final JRadioButton radioButton;

    @Getter
    private final Backup backup;
    private final BackupDisplayEntryController controller;

    public BackupDisplayEntry(Backup backup) {
        super();
        this.backup = backup;

        radioButton = new JRadioButton();
        var timeStr = backup.getFileAttributes().map(
                a -> DateTimeFormatter.ofPattern(BACKUP_TIME_PATTERN)
                        .withZone(ZoneId.systemDefault())
                        .format(a.creationTime().toInstant())
        ).orElse(UNKNOWN_STRING);
        var seedStr = String.format(BACKUP_SEED_PATTERN,
                backup.getSeed().map(Objects::toString).orElse(UNKNOWN_STRING)
        );

        this.setLayout(new BorderLayout());
        this.add(radioButton, BorderLayout.WEST);
        this.add(create(new JPanel()).layout(new GridLayout(2, 2))
                        .children(
                                nameLabel = new JLabel(backup.getName()), new JLabel(timeStr),
                                new JLabel(seedStr), new JLabel(UNKNOWN_STRING) // TODO: use the last label for file size and format
                        ).finish(),
                BorderLayout.CENTER);

        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 0, 5),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));

        this.controller = new BackupDisplayEntryController(this);
        this.addMouseListener(controller.backupEntryMouseListener);
    }

    public boolean isSelected() {
        return this.radioButton.isSelected();
    }

    public void showRightClickMenu() {
        this.setComponentPopupMenu(create(new JPopupMenu())
                .children(
                        create(new JMenuItem("Rename")).onAction(this.controller::onRename).finish(),
                        new JPopupMenu.Separator(),
                        create(new JMenuItem("Delete")).onAction(this.controller::onDelete).finish()
                ).finish()
        );
    }

    public void updateName(String newName) {
        this.nameLabel.setText(newName);
    }
}