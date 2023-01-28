package io.github.lucunji.noitaqb.utils;

import io.github.lucunji.noitaqb.model.Backup;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.examples.Archiver;
import org.apache.commons.compress.archivers.examples.Expander;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Set;

public class BackupUtils {
    private static final String SAVE_SLOT_0 = "save00";
    private static final Set<String> BACKUP_EXTENSIONS = Set.of("zip");
    private static final FileTime OLDEST_FILETIME = FileTime.fromMillis(Long.MIN_VALUE);
    private static final Archiver DEFAULT_ARCHIVER = new Archiver();
    private static final Expander DEFAULT_EXPANDER = new Expander();

    /**
     * All backups with valid extensions
     * Sorted in the reverse order of creation time
     */
    public static Backup[] loadBackups(String backupPath) throws IOException {
        var path = Path.of(backupPath);
        if (!path.toFile().exists()) return new Backup[]{};

        try (var stream = Files.list(path)) {
            return stream
                    .filter(p -> BACKUP_EXTENSIONS.contains(FileUtils.getExtension(p).toLowerCase()))
                    .map(Backup::new)
                    .sorted(Comparator
                            .<Backup, FileTime>comparing(b -> b.getFileAttributes().map(BasicFileAttributes::creationTime).orElse(OLDEST_FILETIME))
                            .reversed())
                    .toArray(Backup[]::new);
        }
    }

    public static Backup makeBackup(String name, String backupPath, String savePath, ArchiveMode mode) throws IOException, ArchiveException {
        var dir = Paths.get(savePath, SAVE_SLOT_0);
        var backupDir = Path.of(backupPath);
        var backup = Paths.get(backupPath, name + "." + mode.extension);

        // avoid file collision
        for (int i = 0; backup.toFile().exists(); i++) {
            backup = Paths.get(backupPath, name + " (" + i + ")." + mode.extension);
        }

        FileUtils.ensureDir(backupDir);

        if (mode.compressed) {
            // FIXME
            throw new UnsupportedOperationException("Compressed archive");
        } else {
            try (var output = new ArchiveStreamFactory().createArchiveOutputStream(
                    mode.archiverName, new BufferedOutputStream(new FileOutputStream(backup.toFile()))
            )) {
                DEFAULT_ARCHIVER.create(output, dir);
            }
        }

        return new Backup(backup);
    }

    public static void loadBackup(Path backupPath, String savePath) throws IOException, ArchiveException {
        var dir = Paths.get(savePath, SAVE_SLOT_0);

        dir.toFile().delete();
        dir.toFile().mkdir();

        try (var input = new ArchiveStreamFactory().createArchiveInputStream(
                new BufferedInputStream(new FileInputStream(backupPath.toFile()))
        )) {
            DEFAULT_EXPANDER.expand(input, dir.toFile());
        }
    }
}
