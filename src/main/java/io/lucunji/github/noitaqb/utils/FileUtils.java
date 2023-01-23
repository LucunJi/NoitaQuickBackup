package io.lucunji.github.noitaqb.utils;

import io.lucunji.github.noitaqb.Main;
import org.apache.commons.compress.utils.FileNameUtils;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileUtils {
    public static void ensureDir(Path dir) {
        var file = dir.toFile();
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("Failed to create directory or the path is not a directory " + dir);
        }
    }

    /**
     * <a href="https://stackoverflow.com/questions/2837263/how-do-i-get-the-directory-that-the-currently-executing-jar-file-is-in">reference 1</a>
     * <a href="https://stackoverflow.com/questions/2837263/how-do-i-get-the-directory-that-the-currently-executing-jar-file-is-in">reference 2</a>
     */
    public static String getExecutablePath() throws URISyntaxException {
        return Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    }

    public static String getExtension(Path path) {
        return FileNameUtils.getExtension(path.getFileName().toString());
    }
}
