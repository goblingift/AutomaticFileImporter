/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.events.Watcher;

import gift.goblin.automaticfileimporter.TrayIconRenderer;
import gift.goblin.automaticfileimporter.io.DeviceManager;
import gift.goblin.automaticfileimporter.io.filevisitor.CopyDirectoryFileVisitor;
import gift.goblin.automaticfileimporter.io.filevisitor.ExpliciteDirectoryFileVisitor;
import gift.goblin.automaticfileimporter.io.filevisitor.HiddenFileVisitor;
import gift.goblin.automaticfileimporter.io.filevisitor.RecursiveFileVisitor;
import gift.goblin.automaticfileimporter.model.Configuration;
import gift.goblin.automaticfileimporter.model.enums.Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Watches the connected devices and triggers following tasks if devices are
 * connected.
 *
 * @author andre
 */
public class DeviceWatcher implements Runnable {

    /**
     * Updates the UI of the tray icon
     */
    private TrayIconRenderer trayIconRenderer;

    /**
     * List of initial connected devices at application start
     */
    private List<File> initialDevices;

    private DeviceManager deviceManager;

    private Configuration configuration;

    private static final long SLEEP_MS = 10_000;

    public DeviceWatcher(TrayIconRenderer trayIconRenderer, List<File> initialDevices, Configuration configuration) {
        this.trayIconRenderer = trayIconRenderer;
        this.initialDevices = initialDevices;
        this.configuration = configuration;
        this.deviceManager = new DeviceManager();
    }

    @Override
    public void run() {

        boolean watchForDevices = true;

        do {

            // Looking for newly added devices
            List<File> devices = deviceManager.getDevices();
            List<File> newDevices = devices.stream().filter(f -> !initialDevices.contains(f))
                    .collect(Collectors.toList());

            if (!newDevices.isEmpty()) {
                if (newDevices.size() > 1) {
                    // More than 1 new device found, user has to select the right one
                    System.out.println("Found more than 1 device- please select it: " + newDevices.toString());
                    trayIconRenderer.renderDevices(devices);
                    trayIconRenderer.setStatus(Status.WAITING);
                } else {
                    // Found 1 device, start crawling
                    File newDevice = newDevices.get(0);
                    System.out.println("Found one new device, start crawling in 10s: " + newDevice.toString());
                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DeviceWatcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    trayIconRenderer.renderDevices(devices);
                    trayIconRenderer.setStatus(Status.WORKING);
                    try {
                        if (!configuration.getIncludedDirectories().isEmpty()) {
                            ExpliciteDirectoryFileVisitor directoryFinder = new ExpliciteDirectoryFileVisitor(configuration);
                            Files.walkFileTree(newDevice.toPath().getRoot(), directoryFinder);
                            List<Path> foundDirectories = directoryFinder.getFoundDirectories();
                            System.out.println("DONE! FOUND DIRECTORIES:" + foundDirectories);

                            // Start several copy tasks for the found directories
                            for (Path actDirectory : foundDirectories) {
                                Files.walkFileTree(actDirectory, new CopyDirectoryFileVisitor(actDirectory, configuration.getTargetDirectoryPath()));
                                System.out.println("Finished scanning explicite directories.");
                            }
                        } else {
                            System.out.println("No explicite directories entered- skip that task.");
                        }

                        // Start the hidden file crawler
                        HiddenFileVisitor hiddenFileVisitor = new HiddenFileVisitor(configuration);
                        Files.walkFileTree(newDevice.toPath().getRoot(), hiddenFileVisitor);
                        System.out.println("Finished crawling hidden files- copied files: " + hiddenFileVisitor.getCopiedFiles());

                        // Start the recursive directory crawler (Only for given filetypes)
                        Files.walkFileTree(newDevice.toPath().getRoot(), new RecursiveFileVisitor(configuration, true));
                        System.out.println("Finished recursive directory crawler (Given filetypes)");

                        // Start the recursive directory crawler (For other filetypes)
                        Files.walkFileTree(newDevice.toPath().getRoot(), new RecursiveFileVisitor(configuration, false));
                        System.out.println("Finished recursive directory crawler (Other filetypes)");

                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                System.out.println("Stop watching devices - to start again, trigger in tray icon.");
                trayIconRenderer.setStatus(Status.WAITING);
                watchForDevices = false;
            } else {
                System.out.println("No new devices found, sleep for " + SLEEP_MS + " milliseconds.");

                try {
                    Thread.sleep(SLEEP_MS);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        } while (watchForDevices);
    }

}
