package io.github.lucunji.noitaqb.model;

import lombok.Getter;
import org.apache.commons.compress.utils.FileNameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Optional;

public class Backup {
    @Getter
    private String name;
    @Getter
    private final String extension;
    @Getter
    private final Path directory;
    private final BasicFileAttributes fileAttributes;
    private final Long seed;

    public Backup(Path path) {
        final String fullname = path.getFileName().toString();
        this.name = FileNameUtils.getBaseName(fullname);
        this.extension = FileNameUtils.getExtension(fullname);
        this.directory = path.getParent();
        BasicFileAttributes attr = null;
        try {
             attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            // leave it null
            System.out.println("Could not get file attributes for " + path);
        }
        this.fileAttributes = attr;
        this.seed = null;
    }

    public void setName(String name) {
        // TODO: update file name
        this.name = name;
    }

    public Optional<BasicFileAttributes> getFileAttributes() {
        return Optional.ofNullable(fileAttributes);
    }

    public Optional<Long> getSeed() {
        return Optional.ofNullable(seed);
    }

    public Path getPath() {
        return this.directory.resolve(this.name + "." + this.extension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Backup backup = (Backup) o;
        return name.equals(backup.name) && extension.equals(backup.extension) && directory.equals(backup.directory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, extension, directory);
    }
}
