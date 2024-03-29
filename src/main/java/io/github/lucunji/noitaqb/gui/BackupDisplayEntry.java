package io.github.lucunji.noitaqb.gui;

import io.github.lucunji.noitaqb.model.Backup;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

import static io.github.lucunji.noitaqb.utils.SwingUIBuilder.create;

public class BackupDisplayEntry extends JPanel {
    public static final String UNKNOWN_STRING = "unknown";
    public static final String BACKUP_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final String BACKUP_SEED_PATTERN = "seed: %s";

    private final JLabel nameLabel;
    protected final JRadioButton radioButton;

    @Getter
    private final Backup backup;

    public BackupDisplayEntry(Backup backup) {
        super();
        this.backup = backup;
        var controller = new BackupDisplayEntryController(this);

        radioButton = new JRadioButton();
        var timeStr = backup.getFileAttributes().map(
                a -> DateTimeFormatter.ofPattern(BACKUP_TIME_PATTERN)
                        .withZone(ZoneId.systemDefault())
                        .format(a.creationTime().toInstant())
        ).orElse(UNKNOWN_STRING);
        var seedStr = String.format(BACKUP_SEED_PATTERN,
                backup.getSeed().map(Objects::toString).orElse(UNKNOWN_STRING)
        );
        var sizeStr = backup.getFileAttributes()
            .map(attrs -> FileUtils.byteCountToDisplaySize(attrs.size())).orElse(UNKNOWN_STRING);

        this.setLayout(new BorderLayout());
        this.add(radioButton, BorderLayout.WEST);
        this.add(create(new JPanel()).layout(new GridLayout(2, 2))
                        .children(
                                nameLabel = new JLabel(backup.getName()), new JLabel(timeStr),
                                new JLabel(seedStr), new JLabel(sizeStr)
                        ).finish(),
                BorderLayout.CENTER);

        // right-click menu, set once is enough
        this.setComponentPopupMenu(create(new JPopupMenu())
                .children(
                        create(new JMenuItem("Rename")).onAction(controller::onRename).finish(),
                        new JPopupMenu.Separator(),
                        create(new JMenuItem("Delete")).onAction(controller::onDelete).finish()
                ).finish()
        );

        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 0, 5),
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        this.setMaximumSize(new Dimension(this.getMaximumSize().width, this.getPreferredSize().height));

        this.addMouseListener(controller.backupEntryMouseListener);
    }

    public boolean isSelected() {
        return this.radioButton.isSelected();
    }

    public void updateName(String newName) {
        this.nameLabel.setText(newName);
    }
}
