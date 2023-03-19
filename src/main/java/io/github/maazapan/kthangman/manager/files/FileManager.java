package io.github.maazapan.kthangman.manager.files;

import io.github.maazapan.kthangman.KTHangman;

public class FileManager {

    private FileCreator messages;
    private FileCreator lobby;
    private FileCreator config;

    private final KTHangman plugin;

    public FileManager(KTHangman plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all configuration files.
     */
    public void loadFiles() {
        try {
            messages = new FileCreator("messages.yml", plugin.getDataFolder().getPath(), plugin).create();
            config = new FileCreator("config.yml", plugin.getDataFolder().getPath(), plugin).create();
            lobby = new FileCreator("lobby.yml", plugin.getDataFolder().getPath(), plugin).create();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileCreator getLobby() {
        return lobby;
    }

    public FileCreator getConfig() {
        return config;
    }

    public FileCreator getMessages() {
        return messages;
    }
}
