/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.events;

import gift.goblin.automaticfileimporter.TrayIconRenderer;
import gift.goblin.automaticfileimporter.io.filevisitor.CopyDirectoryFileVisitor;
import gift.goblin.automaticfileimporter.io.filevisitor.ExpliciteDirectoryFileVisitor;
import gift.goblin.automaticfileimporter.io.filevisitor.RecursiveFileVisitor;
import gift.goblin.automaticfileimporter.model.Configuration;
import gift.goblin.automaticfileimporter.model.enums.Status;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author andre
 */
public class ClickDeviceEvent implements ActionListener {

    private Configuration configuration;
    private TrayIconRenderer trayIconRenderer;

    public ClickDeviceEvent(Configuration configuration, TrayIconRenderer trayIconRenderer) {
        this.configuration = configuration;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem source = (MenuItem) e.getSource();
        String devicePath = source.getLabel();
        System.out.println("Clicked on device: " + devicePath);

//        trayIconRenderer.setStatus(Status.WORKING);
        try {
            if (!configuration.getIncludedDirectories().isEmpty()) {
                ExpliciteDirectoryFileVisitor directoryFinder = new ExpliciteDirectoryFileVisitor(configuration);
                Files.walkFileTree(Paths.get(devicePath), directoryFinder);
                List<Path> foundDirectories = directoryFinder.getFoundDirectories();
                System.out.println("DONE! FOUND DIRECTORIES:" + foundDirectories);

                // Start several copy tasks for the found directories
                for (Path actDirectory : foundDirectories) {
                    Files.walkFileTree(actDirectory, new CopyDirectoryFileVisitor(actDirectory, configuration.getTargetDirectoryPath()));
                }
            } else {
                System.out.println("No explicite directories entered- skip that task.");
            }

            // Start the recursive directory crawler (Only for given filetypes)
            Files.walkFileTree(Paths.get(devicePath), new RecursiveFileVisitor(configuration, true));
            System.out.println("Finished recursive directory crawler (Given filetypes)");

            // Start the recursive directory crawler (For other filetypes)
            Files.walkFileTree(Paths.get(devicePath), new RecursiveFileVisitor(configuration, false));
            System.out.println("Finished recursive directory crawler (Other filetypes)");

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Finished crawling:" + devicePath);
//        trayIconRenderer.setStatus(Status.WAITING);
    }

}
