package io.github.lucunji.noitaqb.utils;

import io.github.lucunji.noitaqb.Main;
import org.apache.commons.compress.utils.FileNameUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
    public static void ensureDir(Path dir, boolean canBeLink) throws IOException {
        boolean isDir = canBeLink ? Files.isDirectory(dir) : Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS);
        if (Files.exists(dir) && !isDir) {
            throw new NoSuchFileException("Failed to create directory or the path is not a directory: " + dir);
        }
        if (!Files.exists(dir) ) {
            Files.createDirectories(dir);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/2837263/how-do-i-get-the-directory-that-the-currently-executing-jar-file-is-in">reference</a>
     */
    public static Path getExecutablePath() {
        try {
            return Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            // should never go wrong
            throw new RuntimeException(e);
        }
    }

    public static String getExtension(Path path) {
        return FileNameUtils.getExtension(path.getFileName().toString());
    }

    /**
     * Does not follow symbolic links
     */
    public static void deleteDirectoryRecursive(Path dirToRemove) throws IOException {
        Files.walkFileTree(dirToRemove, new DeleteFileVisitor());
    }


    private static class DeleteFileVisitor implements FileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {return FileVisitResult.CONTINUE;}

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {return FileVisitResult.TERMINATE;}

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null) throw exc;
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
