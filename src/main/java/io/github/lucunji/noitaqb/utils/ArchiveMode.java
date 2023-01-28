package io.github.lucunji.noitaqb.utils;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public enum ArchiveMode {
    ZIP_ARCHIVE("zip", false, ArchiveStreamFactory.ZIP);

    public final String extension; // without dot
    public final boolean compressed;
    public final String archiverName; // or the compressor name if compressed is true

    ArchiveMode(String extension, boolean compressed, String archiverName) {
        this.extension = extension;
        this.compressed = compressed;
        this.archiverName = archiverName;
    }
}
