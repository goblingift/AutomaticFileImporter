/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andre
 */
public class Configuration {

    /**
     * Contains a list of strings which can include the fileType, e.g. "JPG" or
     * else.
     */
    private List<String> fileTypes;

    /**
     * Contains a list of strings for directory-names, which shall get skipped.
     */
    private List<String> excludedDirectories;

    /**
     * Contains a list of strings for directory-names, which always shall get
     * scanned.
     */
    private List<String> includedDirectories;

    /**
     * Directory where the files shall get copied to.
     */
    private Path targetDirectoryPath;

    public Configuration(List<String> fileTypes, List<String> excludedDirectories, List<String> includedDirectories, Path targetDirectoryPath) {
        this.fileTypes = fileTypes;
        this.excludedDirectories = excludedDirectories;
        this.includedDirectories = includedDirectories;
        this.targetDirectoryPath = targetDirectoryPath;
    }

    public Configuration() {
        fileTypes = new ArrayList<>();
        excludedDirectories = new ArrayList<>();
        includedDirectories = new ArrayList<>();
    }

    public List<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public List<String> getExcludedDirectories() {
        return excludedDirectories;
    }

    public void setExcludedDirectories(List<String> excludedDirectories) {
        this.excludedDirectories = excludedDirectories;
    }

    public List<String> getIncludedDirectories() {
        return includedDirectories;
    }

    public void setIncludedDirectories(List<String> includedDirectories) {
        this.includedDirectories = includedDirectories;
    }

    public Path getTargetDirectoryPath() {
        return targetDirectoryPath;
    }

    public void setTargetDirectoryPath(Path targetDirectoryPath) {
        this.targetDirectoryPath = targetDirectoryPath;
    }

    @Override
    public String toString() {
        return "Configuration{" + "fileTypes=" + fileTypes + ", excludedDirectories=" + excludedDirectories + ", includedDirectories=" + includedDirectories + ", targetDirectoryPath=" + targetDirectoryPath + '}';
    }

}
