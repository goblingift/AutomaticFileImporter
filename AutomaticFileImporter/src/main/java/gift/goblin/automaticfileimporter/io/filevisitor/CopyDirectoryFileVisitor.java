/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.io.filevisitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File visitor implementation, which will copy whole directories (including
 * files) to another directory.
 *
 * @author andre
 */
public class CopyDirectoryFileVisitor extends SimpleFileVisitor<Path> {

    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final static String SUBDIRECTORY_PATH = "copy";

    public CopyDirectoryFileVisitor(Path sourceDirectory, Path targetDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        System.out.println("Start copying files/directories inside directory:" + dir);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attrs) throws IOException {
        
        try {
            Path targetFile = targetDirectory.resolve(SUBDIRECTORY_PATH).resolve(file.subpath(0, file.getNameCount()));
            targetFile.toFile().getParentFile().mkdirs();
            
            Files.copy(file, targetFile,
                StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (Exception e) {
            System.out.println("Exception while copying file:" + file);
        }
        
        
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.printf("Visiting failed for %s\n", file);
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir,
            IOException e) throws IOException {
        System.out.println("Done with copying files/directories inside directory:" + dir);
        return FileVisitResult.CONTINUE;
    }
}
