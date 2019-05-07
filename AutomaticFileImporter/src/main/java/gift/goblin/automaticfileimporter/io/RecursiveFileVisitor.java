/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.io;

import gift.goblin.automaticfileimporter.model.Configuration;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public class RecursiveFileVisitor extends SimpleFileVisitor<Path> {

    private final Configuration configuration;

    public RecursiveFileVisitor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        System.out.println("pre visiting dir:" + dir.toString());

        if (dir.getNameCount() == 0) {
            System.out.println("Starting with reading root: " + dir.toString());
            return FileVisitResult.CONTINUE;
        }

        String folderName = dir.getFileName().toString().toLowerCase();

        // First check for explicite included directories
        if (configuration.getIncludedDirectories().stream().anyMatch(s -> folderName.contains(s))) {
            System.out.println("Explicite directory found- its included in the list of included-dirs: " + folderName);
            return FileVisitResult.CONTINUE;

            // then check if directory is in excluded directories
        } else if (configuration.getExcludedDirectories().stream().anyMatch(s -> folderName.contains(s))) {
            System.out.println("Skip directory, cause its included in the list of excluded-dirs: " + folderName);
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        System.out.println("visiting file:" + file.toString());

        String fileName = file.getFileName().toString();

        if (configuration.getFileTypes().stream().anyMatch(s -> fileName.contains(s))) {
            System.out.println("Copy file: " + file.toString());

            //FileUtils.copyFileToDirectory(file, destDir, true);
            copyFile(file);
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * Copies the given file to the configured target path.
     *
     * @param file
     */
    private void copyFile(Path file) {

        Path subpath = file.subpath(0, file.getNameCount());
        Path resolved = configuration.getTargetDirectoryPath().resolve(subpath);
        
        try {
            System.out.println("Start copying file: Source:" + file + " target:" + resolved);
            Files.createDirectories(resolved.getParent());
            Files.copy(file, resolved, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            System.out.println("Successful copied file: " + resolved);
        } catch (IOException ex) {
            Logger.getLogger(RecursiveFileVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
