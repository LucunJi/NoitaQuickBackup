package io.lucunji.github.noitaqb.model;

import lombok.Getter;

import java.nio.file.attribute.FileTime;
import java.time.LocalTime;

public class Backup {
    @Getter
    private ArchiveData archiveData;
    @Getter
    private String name;
    @Getter
    private final FileTime time;
    @Getter
    private final long seed;

    public Backup(String name, ArchiveData archiveData, FileTime time, long seed) {
        this.name = name;
        this.archiveData = archiveData;
        this.time = time;
        this.seed = seed;
    }

    public void setName(String name) {
        // TODO: update file name
        this.name = name;
    }
}
