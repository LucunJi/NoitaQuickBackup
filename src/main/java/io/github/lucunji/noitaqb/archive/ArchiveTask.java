package io.github.lucunji.noitaqb.archive;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import javax.swing.SwingWorker;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import io.github.lucunji.noitaqb.model.Backup;
import lombok.Getter;

public class ArchiveTask extends SwingWorker<Backup, Path> {
    private final Path savePath;
    private final Path archiveFile;
    private final ArchiveMode mode;

    private final int nEntries;
    private int nEntriesFinished = 0;

    @Getter
    private Path lastFinished = null;

    public ArchiveTask(Path savePath, Path archiveFile, ArchiveMode mode) throws IOException {
        this.savePath = savePath;
        this.archiveFile = archiveFile;
        this.mode = mode;
        this.nEntries = (int) Files.walk(savePath).count();
    }

    private void updateProgress(Path finishedDir) {
        this.nEntriesFinished++;
        this.setProgress((int) (100D * this.nEntriesFinished / this.nEntries));
        this.firePropertyChange("finished", this.lastFinished, finishedDir);
        this.lastFinished = finishedDir;
    }


    @Override
    protected Backup doInBackground() throws IOException, ArchiveException {
        try (ArchiveOutputStream output = new ArchiveStreamFactory().createArchiveOutputStream(
                mode.archiverName, new BufferedOutputStream(new FileOutputStream(this.archiveFile.toFile())))) {
            Files.walkFileTree(savePath, new ArchiverFileVisitor(output, savePath) {
                @Override
                protected FileVisitResult visit(final Path path, final BasicFileAttributes attrs, final boolean isFile)
                        throws IOException {
                    var ret = super.visit(path, attrs, isFile);
                    ArchiveTask.this.updateProgress(savePath.relativize(path));
                    return ret;
                }
            });

            output.finish();
        }

        return new Backup(this.archiveFile);
    }

    /*
     * Licensed to the Apache Software Foundation (ASF) under one
     * or more contributor license agreements. See the NOTICE file
     * distributed with this work for additional information
     * regarding copyright ownership. The ASF licenses this file
     * to you under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance
     * with the License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing,
     * software distributed under the License is distributed on an
     * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
     * KIND, either express or implied. See the License for the
     * specific language governing permissions and limitations
     * under the License.
     */
    private static class ArchiverFileVisitor extends SimpleFileVisitor<Path> {

        private final ArchiveOutputStream target;
        private final Path directory;
        private final LinkOption[] linkOptions;

        private ArchiverFileVisitor(final ArchiveOutputStream target, final Path directory,
                final LinkOption... linkOptions) {
            this.target = target;
            this.directory = directory;
            this.linkOptions = linkOptions == null ? IOUtils.EMPTY_LINK_OPTIONS : linkOptions.clone();
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            return visit(dir, attrs, false);
        }

        protected FileVisitResult visit(final Path path, final BasicFileAttributes attrs, final boolean isFile)
                throws IOException {
            Objects.requireNonNull(path);
            Objects.requireNonNull(attrs);
            final String name = directory.relativize(path).toString().replace('\\', '/');
            if (!name.isEmpty()) {
                final ArchiveEntry archiveEntry = target.createArchiveEntry(path,
                        isFile || name.endsWith("/") ? name : name + "/", linkOptions);
                target.putArchiveEntry(archiveEntry);
                if (isFile) {
                    // Refactor this as a BiConsumer on Java 8
                    Files.copy(path, target);
                }
                target.closeArchiveEntry();
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            return visit(file, attrs, true);
        }
    }
}
