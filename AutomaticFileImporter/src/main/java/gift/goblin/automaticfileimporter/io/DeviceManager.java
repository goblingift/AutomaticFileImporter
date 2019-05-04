/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.automaticfileimporter.io;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Detects and watches devices
 * @author andre
 */
public class DeviceManager {
    
    
    public List<File> getDevices() {
        File[] rootDrive = File.listRoots();
        List<File> devices = Arrays.asList(rootDrive).stream()
                .filter(s -> Files.isDirectory(s.toPath()))
                .collect(Collectors.toList());
        
        return devices;
    }
    
    
}
