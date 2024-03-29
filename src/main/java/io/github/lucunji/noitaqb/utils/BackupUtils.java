package io.github.lucunji.noitaqb.utils;

import io.github.lucunji.noitaqb.archive.ArchiveMode;
import io.github.lucunji.noitaqb.archive.ArchiveTask;
import io.github.lucunji.noitaqb.model.Backup;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.examples.Expander;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Set;

/**
 * Do not handle exception early here
 */
public class BackupUtils {
    private static final String SAVE_SLOT_0 = "save00";
    private static final Set<String> BACKUP_EXTENSIONS = Set.of("zip");
    private static final FileTime OLDEST_FILETIME = FileTime.fromMillis(Long.MIN_VALUE);
    private static final Expander DEFAULT_EXPANDER = new Expander();

    /**
     * All backups with valid extensions
     * Sorted in the reverse order of creation time
     */
    public static Backup[] listBackups(String backupPath) throws IOException {
        var path = Path.of(backupPath);
        if (!Files.exists(path)) return new Backup[]{};

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


    public static ArchiveTask getArchiveTask(String name, String backupPath, String savePath, ArchiveMode mode) throws IOException {

        var dir = Paths.get(savePath, SAVE_SLOT_0);
        var backupDir = Path.of(backupPath);
        var backupFile = Paths.get(backupPath, name + "." + mode.extension);

        // avoid file collision
        if (Files.exists(backupFile)) {
            throw new IOException("Backup file already exists: " + backupFile);
        }

        FileUtils.ensureDir(backupDir, true);

        if (mode.compressed) {
            throw new UnsupportedOperationException("No support for compressed archive");
        } else {
            return new ArchiveTask(dir, backupFile, mode);
        }
    }

    public static void loadBackup(Path backupPath, String savePath) throws IOException, ArchiveException {
        var currentSave = Paths.get(savePath, SAVE_SLOT_0);

        if (Files.exists(currentSave)) FileUtils.deleteDirectoryRecursive(currentSave);
        Files.createDirectories(currentSave);

        try (var input = new ArchiveStreamFactory().createArchiveInputStream(
                new BufferedInputStream(new FileInputStream(backupPath.toFile()))
        )) {
            DEFAULT_EXPANDER.expand(input, currentSave.toFile());
        }
    }

    public static void removeBackup(Path backupPath) throws IOException {
        Files.delete(backupPath);
    }

    public static void renameBackup(Path folder, String oldFullName, String newFullName) throws IOException {
        Files.move(folder.resolve(oldFullName), folder.resolve(newFullName));
    }

    public static Long findBackupSeed(Path backupFile) throws IOException, ArchiveException {
        try (var input = new ArchiveStreamFactory().createArchiveInputStream(
                new BufferedInputStream(new FileInputStream(backupFile.toFile()))
        )) {
            var nextEntry = input.getNextEntry();
            while (nextEntry != null) {
                while (!input.canReadEntryData(nextEntry) || nextEntry.isDirectory()) {
                    nextEntry = input.getNextEntry();
                }

                if (Path.of(nextEntry.getName()).getFileName().toString().equals(".stream_info")) {
                    input.skipNBytes(13);
                    var b = input.readNBytes(4);
                    long seed = 0;
                    for (byte bi : b) {
                        seed <<= 8;
                        seed |= ((long) bi) & 0xFF;
                    }
                    return seed;
                }
                
                nextEntry = input.getNextEntry();
            }
        }
        return null;
    }
}
