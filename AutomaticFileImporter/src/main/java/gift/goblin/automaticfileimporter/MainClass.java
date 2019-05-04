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
        
        // Rendering the icon
        TrayIconRenderer trayIconRenderer = new TrayIconRenderer();
        
        List<File> initialDevices = new DeviceManager().getDevices();
        
        Configuration configuration = readConfiguration();
        trayIconRenderer.setConfiguration(configuration);
        
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
        List<String> fileTypes = convertsToList(inputFileType);
        configuration.setFileTypes(fileTypes);
        System.out.println("Successful set fileTypes to: " + fileTypes);
        
        
        return configuration;
    }
    
    /**
     * Seperates the string by ',' character to a list of Strings.
     * @param input input string, with comma-seperated values.
     * @return list of strings, can be empty but never null.
     */
    private List<String> convertsToList(String input) {
        List<String> returnValue = new ArrayList<>();
        
        Scanner seperator = new Scanner(input).useDelimiter(",");
        while (seperator.hasNext()) {
            returnValue.add(seperator.next());
        }
        
        return returnValue;
    }
    
    
    
}
