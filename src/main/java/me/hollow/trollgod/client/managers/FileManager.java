package me.hollow.trollgod.client.managers;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;
import me.hollow.trollgod.client.modules.*;

public class FileManager
{
    private final Path base;
    
    private Path lookupPath(final Path root, final String... paths) {
        return Paths.get(root.toString(), paths);
    }
    
    private Path getRoot() {
        return Paths.get("", new String[0]);
    }
    
    private void createDirectory(final Path dir) {
        try {
            if (!Files.isDirectory(dir, new LinkOption[0])) {
                if (Files.exists(dir, new LinkOption[0])) {
                    Files.delete(dir);
                }
                Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Path getMkDirectory(final Path parent, final String... paths) {
        if (paths.length < 1) {
            return parent;
        }
        final Path dir = this.lookupPath(parent, paths);
        this.createDirectory(dir);
        return dir;
    }
    
    public FileManager() {
        this.base = this.getMkDirectory(this.getRoot(), "TrollGod/modules/");
        for (final Module.Category category : Module.Category.values()) {
            this.getMkDirectory(this.base, category.name());
        }
    }
}
