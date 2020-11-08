/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.events;

import gift.goblin.automaticfileimporter.TrayIconRenderer;
import gift.goblin.automaticfileimporter.io.DeviceManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 *
 * @author andre
 */
public class RefreshDevicesEvent implements ActionListener {

    private TrayIconRenderer trayIconRenderer;

    public RefreshDevicesEvent(TrayIconRenderer trayIconRenderer) {
        this.trayIconRenderer = trayIconRenderer;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        List<File> devices = new DeviceManager().getDevices();
        trayIconRenderer.renderDevices(devices);
    }
    
}
