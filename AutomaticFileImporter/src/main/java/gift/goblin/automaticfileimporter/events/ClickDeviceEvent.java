/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.events;

import gift.goblin.automaticfileimporter.TrayIconRenderer;
import gift.goblin.automaticfileimporter.io.ExpliciteDirectoryFileVisitor;
import gift.goblin.automaticfileimporter.io.RecursiveFileVisitor;
import gift.goblin.automaticfileimporter.model.Configuration;
import gift.goblin.automaticfileimporter.model.enums.Status;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author andre
 */
public class ClickDeviceEvent implements ActionListener {

    private Configuration configuration;
    private TrayIconRenderer trayIconRenderer;

    public ClickDeviceEvent(Configuration configuration, TrayIconRenderer trayIconRenderer) {
        this.configuration = configuration;
        this.trayIconRenderer = trayIconRenderer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem source = (MenuItem) e.getSource();
        String devicePath = source.getLabel();
        System.out.println("Clicked on device: " + devicePath);

        trayIconRenderer.setStatus(Status.WORKING);
        try {
            ExpliciteDirectoryFileVisitor directoryFinder = new ExpliciteDirectoryFileVisitor(configuration);
            Files.walkFileTree(Paths.get(devicePath), directoryFinder);
            
            System.out.println("DONE! FOUND DIRECTORIES:");
            System.out.println(directoryFinder.getFoundDirectories());
            
//            Files.walkFileTree(Paths.get(devicePath), new RecursiveFileVisitor(configuration));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Finished crawling:" + devicePath);
        trayIconRenderer.setStatus(Status.WAITING);
    }

}
