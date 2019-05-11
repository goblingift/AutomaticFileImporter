/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter;

import gift.goblin.automaticfileimporter.events.Watcher.DeviceWatcher;
import gift.goblin.automaticfileimporter.io.DeviceManager;
import gift.goblin.automaticfileimporter.model.Configuration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author andre
 */
public class MainClass {
    
    public static void main(String[] args) {
        new MainClass().startApplication();
    }
    
    public void startApplication() {

        // Configure the application by user-input
        Configuration configuration = readConfiguration();
        
        // Rendering the icon
        TrayIconRenderer trayIconRenderer = new TrayIconRenderer(configuration);
        
        List<File> initialDevices = new DeviceManager().getDevices();
        
        
        // Start the watcher Thread
        new Thread(new DeviceWatcher(trayIconRenderer, initialDevices, configuration)).start();
    }
    
    private Configuration readConfiguration() {
        
        Configuration configuration = new Configuration();
        Scanner in = new Scanner(System.in);
        
        System.out.println("Please enter the targetDirectory (E.g.: C:\\test)");
        String inputTargetDir = in.nextLine();
        
        if (!Files.isDirectory(Paths.get(inputTargetDir))) {
            System.out.println("EXIT, caused that was no valid directory: " + inputTargetDir);
            System.exit(-1);
        } else {
            Path targetDirectoryPath = Paths.get(inputTargetDir);
            configuration.setTargetDirectoryPath(targetDirectoryPath);
            System.out.println("Successful set target directory to: " + inputTargetDir);
        }
        
        System.out.println("Please enter the wanted fileTypes, comma-seperated (E.g. JPG,JPEG,DOC)");
        String inputFileType = in.nextLine();
        List<String> fileTypes = seperateInput(inputFileType);
        configuration.setFileTypes(fileTypes);
        System.out.println("Successful set fileTypes to: " + fileTypes);
        
        System.out.println("Please enter the excluded directories, which wont get scanned. (comma-seperated)");
        String inputExcludedDirectories = in.nextLine();
        List<String> excludedDirectories = seperateInput(inputExcludedDirectories);
        configuration.setExcludedDirectories(excludedDirectories);
        System.out.println("Successful set excluded directories to: " + excludedDirectories);
        
        System.out.println("Please enter the explicite directories, which will get scanned first. (comma-seperated)");
        String inputExpliciteDirectories = in.nextLine();
        List<String> expliciteDirectories = seperateInput(inputExpliciteDirectories);
        configuration.setIncludedDirectories(expliciteDirectories);
        System.out.println("Successful set explicite directories to: " + expliciteDirectories);
        
        return configuration;
    }
    
    /**
     * Seperates the string by ',' character to a list of Strings.
     * @param input input string, with comma-seperated values.
     * @return list of strings, can be empty but never null.
     */
    private List<String> seperateInput(String input) {
        List<String> returnValue = new ArrayList<>();
        
        Scanner seperator = new Scanner(input).useDelimiter(",");
        while (seperator.hasNext()) {
            returnValue.add(seperator.next());
        }
        
        return returnValue;
    }
    
    
    
}
