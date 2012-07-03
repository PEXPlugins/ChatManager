package ru.tehkode.chatmanager.bukkit.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * Non-Intrusive Connector class. This allows users to continue freely using ChatManager without having MV installed.
 */
public class MultiverseConnector {
    private MultiverseCore plugin;

    public MultiverseConnector(MultiverseCore multiverseCore) {
        this.plugin = multiverseCore;
    }

    /**
     * Return a nicely formatted colored string from Multiverse.
     *
     * @param world The world name to retrieve the name for.
     * @return A colored string if the world is managed by Multiverse, otherwise, just returns the string.
     */
    public String getColoredAliasForWorld(String world) {
        MultiverseWorld mvWorld = this.plugin.getMVWorldManager().getMVWorld(world);
        if (mvWorld != null) {
            return mvWorld.getColoredWorldString();
        }
        return world;
    }
}