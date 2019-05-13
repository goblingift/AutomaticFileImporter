/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.io.filevisitor;

import gift.goblin.automaticfileimporter.model.Configuration;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author andre
 */
public class RecursiveFileVisitor extends SimpleFileVisitor<Path> {

    private final Configuration configuration;
    private static final String SEARCH_STRING = "glob:**{%s}*";
    private static final String SEARCH_STRING_FILETYPE = "glob:*{%s}";
    private static final String SUBDIRECTORY_PATH = "recursive";
    private static final String SUBDIRECTORY_PATH_OTHER_FILETYPE = "recursive_other_filetypes";
    private final PathMatcher pathMatcher;
    private final PathMatcher fileTypeMatcher;
    private final boolean onlyGivenFiletypes;

    public RecursiveFileVisitor(Configuration configuration, boolean onlyGivenFiletypes) {
        this.configuration = configuration;
        this.onlyGivenFiletypes = onlyGivenFiletypes;

        String directoryRegex = configuration.getExcludedDirectories().stream()
                .collect(Collectors.joining(","));
        String directoryRegexFormatted = String.format(SEARCH_STRING, directoryRegex);
        this.pathMatcher = FileSystems.getDefault().getPathMatcher(directoryRegexFormatted);

        String filetypeRegex = configuration.getFileTypes().stream()
                .collect(Collectors.joining(","));
        String filetypeRegexFormatted = String.format(SEARCH_STRING_FILETYPE, filetypeRegex);
        this.fileTypeMatcher = FileSystems.getDefault().getPathMatcher(filetypeRegexFormatted);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        if (dir.getNameCount() == 0) {
            System.out.println("Starting with reading root: " + dir.toString());
            return FileVisitResult.CONTINUE;
        }

        String folderName = dir.getFileName().toString().toLowerCase();

        // If this directory name is in the list of excluded folders, skip em
        if (!configuration.getExcludedDirectories().isEmpty() && pathMatcher.matches(dir)) {
            System.out.println("Skip directory, cause its included in the list of excluded-dirs: " + folderName);
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        try {
            boolean hasMatched = fileTypeMatcher.matches(file.getFileName());
            
            if (onlyGivenFiletypes && hasMatched) {
                copyFile(file);
            } else if(!onlyGivenFiletypes && !hasMatched) {
                copyFile(file);
            }
        } catch (Exception e) {
            System.out.println("Exception while copying file:" + file);
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * Copies the given file to the configured target path.
     *
     * @param file
     */
    private void copyFile(Path file) {

        try {
            Path targetFile;
            if (onlyGivenFiletypes) {
                targetFile = configuration.getTargetDirectoryPath().resolve(SUBDIRECTORY_PATH).resolve(file.subpath(0, file.getNameCount()));
            } else {
                targetFile = configuration.getTargetDirectoryPath().resolve(SUBDIRECTORY_PATH_OTHER_FILETYPE).resolve(file.subpath(0, file.getNameCount()));
            }
            
            targetFile.toFile().getParentFile().mkdirs();

            Files.copy(file, targetFile,
                    StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            System.out.println("Exception while copying file:" + file);
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.printf("Visiting failed for %s\n", file);
        return FileVisitResult.SKIP_SUBTREE;
    }

}
