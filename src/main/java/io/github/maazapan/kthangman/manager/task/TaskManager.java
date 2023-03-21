package io.github.maazapan.kthangman.manager.task;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.task.ArenaTask;

public class TaskManager {

    public KTHangman plugin;

    public TaskManager(KTHangman plugin) {
        this.plugin = plugin;
    }

    /**
     * Register and run the all plugin task.
     */
    public void runTask() {
        new ArenaTask(plugin).runTaskTimer(plugin, 0, 10);
    }
}
