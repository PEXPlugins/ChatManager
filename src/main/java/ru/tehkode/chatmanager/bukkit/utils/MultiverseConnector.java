package ru.tehkode.chatmanager.bukkit.utils;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * Non-Intrusive Connector class. This allows users to continue freely using ChatManager without having MV installed.
 */
public class MultiverseConnector {
    private MultiverseCore plugin;

    public MultiverseConnector(MultiverseCore multiverseCore) {
        this.plugin = multiverseCore;
    }

    /**
     * Return a nicely formated colored string from Multiverse.
     * 
     * @param world The world name to retrieve the name for.
     * @return A colored string if the world is managed by Multiverse, otherwise, just returns the string.
     */
    public String getColoredAliasForWorld(String world) {
        MVWorld mvWorld = this.plugin.getWorldManager().getMVWorld(world);
        if (mvWorld != null) {
            return mvWorld.getColoredWorldString();
        }
        return world;
    }
}
