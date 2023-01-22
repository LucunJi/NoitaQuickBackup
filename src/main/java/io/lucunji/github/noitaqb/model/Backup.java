package io.lucunji.github.noitaqb.model;

import lombok.Getter;
import org.apache.commons.compress.utils.FileNameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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
        final String fullname = path.toFile().getName();
        this.name = FileNameUtils.getBaseName(fullname);
        this.extension = FileNameUtils.getExtension(fullname);
        this.directory = path.getParent();
        BasicFileAttributes attr = null;
        try {
             attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
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
}
