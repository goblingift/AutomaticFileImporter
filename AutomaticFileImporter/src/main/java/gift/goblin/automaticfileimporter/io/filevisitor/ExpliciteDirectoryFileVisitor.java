/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.automaticfileimporter.io.filevisitor;

import gift.goblin.automaticfileimporter.model.Configuration;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Searches for explicite folders and will copy their files. Also avoids
 * scanning blacklisted directories.
 *
 * @author andre
 */
public class ExpliciteDirectoryFileVisitor extends SimpleFileVisitor<Path> {

    private final Configuration configuration;
    private final PathMatcher pathMatcher;
    private static final String SEARCH_STRING = "glob:**{%s}*";
    private final List<Path> foundDirectories = new ArrayList<>();

    public ExpliciteDirectoryFileVisitor(Configuration configuration) {
        this.configuration = configuration;

        String joinedString = configuration.getIncludedDirectories().stream()
                .collect(Collectors.joining(","));

        String searchRegex = String.format(SEARCH_STRING, joinedString);

        this.pathMatcher = FileSystems.getDefault().getPathMatcher(searchRegex);
    }

    public List<Path> getFoundDirectories() {
        return foundDirectories;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        if (dir.getNameCount() == 0) {
            System.out.println("Starting with reading root: " + dir.toString());
            return FileVisitResult.CONTINUE;
        }

        String folderName = dir.getFileName().toString().toLowerCase();

        if (pathMatcher.matches(dir)) {
            System.out.println("Explicite directory found- its included in the list of included-dirs: " + folderName);
            foundDirectories.add(dir);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.err.printf("Visiting failed for %s\n", file);
        return FileVisitResult.SKIP_SUBTREE;
    }

}
