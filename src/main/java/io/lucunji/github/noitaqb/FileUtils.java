package io.lucunji.github.noitaqb;

import io.lucunji.github.noitaqb.model.ArchiveData;
import io.lucunji.github.noitaqb.model.Backup;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.examples.Archiver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
    public static final String LINUX_SAVE_PATH =
            ".steam/steam/steamapps/compatdata/881100/pfx/drive_c/users/steamuser/AppData/LocalLow/Nolla_Games_Noita/";
    public static final String SAVE_SLOT_0 = "save00";
    public static final String DEFAULT_BACKUP_PATH = "noitaqb/";
    public static final long UNKNOWN_SEED = 0xDEADBEEFL;

    public static Backup makeBackup(String name) {
        var dir = Paths.get(Main.configs.getGeneral().getSavePath(), SAVE_SLOT_0).toFile();
        var backupDir = new File(Main.configs.getGeneral().getBackupPath());
        var backup = Paths.get(Main.configs.getGeneral().getBackupPath(), name + ".zip").toFile();

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        if (!backupDir.exists()) {
            throw new RuntimeException("Failed to create directory " + backupDir.getAbsolutePath());
        }

        try (var output = new ArchiveStreamFactory().createArchiveOutputStream(
                ArchiveStreamFactory.ZIP, new BufferedOutputStream(new FileOutputStream(backup)))) {
            new Archiver().create(output, dir);
        } catch (IOException | ArchiveException e) {
            throw new RuntimeException(e);
        }

        try {
            return new Backup(
                    name,
                    new ArchiveData(),
                    Files.readAttributes(backup.toPath(), BasicFileAttributes.class).creationTime(),
                    UNKNOWN_SEED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
