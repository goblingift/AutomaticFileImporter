/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.automaticfileimporter.io.filevisitor;

import gift.goblin.automaticfileimporter.model.Configuration;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Extracts the hidden files under the given path.
 *
 * @author andre
 */
public class HiddenFileVisitor extends SimpleFileVisitor<Path> {

    private int copiedFiles;
    private Configuration configuration;
    private static final String SUBDIRECTORY_PATH = "hidden";

    public HiddenFileVisitor(Configuration configuration) {
        this.configuration = configuration;
    }

    public int getCopiedFiles() {
        return copiedFiles;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (file.toFile().isHidden()) {
            copyFile(file);
            copiedFiles++;
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
            Path targetFile = configuration.getTargetDirectoryPath().resolve(SUBDIRECTORY_PATH).resolve(file.subpath(0, file.getNameCount()));

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
