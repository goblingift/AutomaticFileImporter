/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter;

import gift.goblin.automaticfileimporter.events.ClickDeviceEvent;
import gift.goblin.automaticfileimporter.events.ExitEvent;
import gift.goblin.automaticfileimporter.io.DeviceManager;
import gift.goblin.automaticfileimporter.model.Configuration;
import gift.goblin.automaticfileimporter.model.enums.Status;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Renders the tray icon and updates menu-entries there.
 *
 * @author andre
 */
public class TrayIconRenderer {

    private SystemTray tray;
    private PopupMenu popup;
    private Menu deviceMenu;
    private MenuItem statusItem;
    private Configuration configuration;
    
    private static final String TRAY_ICON_PATH = "/goblin_shadow.png";

    public TrayIconRenderer() {
        initializeTray();
    }

    private void initializeTray() {

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported!");
        } else {
            System.out.println("SystemTray supported!");
        }

        tray = SystemTray.getSystemTray();

        final TrayIcon trayIcon = new TrayIcon(createImage(TRAY_ICON_PATH));
        trayIcon.setImageAutoSize(true);

        popup = new PopupMenu();

        statusItem = new MenuItem();
        setStatus(Status.SCANNING);
        popup.add(statusItem);
        
        popup.addSeparator();
        
        deviceMenu = new Menu("Devices");
        List<File> devices = new DeviceManager().getDevices();
        renderDevices(devices);
        popup.add(deviceMenu);
        

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ExitEvent());
        popup.add(exitItem);


        try {
            trayIcon.setPopupMenu(popup);
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

    }

    private Image createImage(String path) {
        try {
            URL url = getClass().getResource(path);
            Image image = ImageIO.read(url);
            return image;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void renderDevices(List<File> devices) {
        deviceMenu.removeAll();
        
        for (File device : devices) {
            MenuItem deviceItem = new MenuItem(device.getPath());
            deviceItem.addActionListener(new ClickDeviceEvent(configuration, this));
            deviceMenu.add(deviceItem);
        }
    }
    
    public void setStatus(Status status) {
       statusItem.setLabel(status.toString());
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
}
