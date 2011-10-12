/*
 * ChatManager - PermissionsEx Chat management plugin for Bukkit
 * Copyright (C) 2011 t3hk0d3 http://www.tehkode.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ru.tehkode.chatmanager.bukkit;

import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public class ChatManager extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    
    protected ChatListener listener;
    protected PluginListener pluginListener;

    public ChatManager() {
    }

    @Override
    public void onEnable() {
        // At first check PEX existance
        try {
            PermissionsEx.getPermissionManager();
        } catch (Throwable e) {
            logger.severe("[ChatManager] PermissionsEx not found, disabling");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        Configuration config = this.getConfiguration();

        if (config.getProperty("enable") == null) { // Migrate
            this.initializeConfiguration(config);
        }

        this.listener = new ChatListener(config);
        this.pluginListener = new PluginListener(this);

        if (config.getBoolean("enable", false)) {
            this.getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, this.listener, Priority.Normal, this);
            this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, this.pluginListener, Priority.Normal, this);
            logger.info("[ChatManager] ChatManager enabled!");
            // Make sure MV didn't load before we did.
            this.pluginListener.checkForMultiverse(this.getServer().getPluginManager().getPlugin("Multiverse-Core"));
        } else {
            logger.info("[ChatManager] ChatManager disabled. Check config.yml!");
            this.getPluginLoader().disablePlugin(this);
        }

        config.save();
    }

    @Override
    public void onDisable() {
        this.listener = null;
        
        logger.info("[ChatManager] ChatManager disabled!");
    }

    protected void initializeConfiguration(Configuration config) {
        // At migrate and setup defaults
        PermissionsEx pex = (PermissionsEx) this.getServer().getPluginManager().getPlugin("PermissionsEx");

        Configuration pexConfig = pex.getConfiguration();

        // Flags
        config.setProperty("enable", pexConfig.getBoolean("permissions.chat.enable", false));
        config.setProperty("message-format", pexConfig.getString("permissions.chat.format", ChatListener.MESSAGE_FORMAT));
        config.setProperty("global-message-format", pexConfig.getString("permissions.chat.global-format", ChatListener.GLOBAL_MESSAGE_FORMAT));
        config.setProperty("ranged-mode", pexConfig.getBoolean("permissions.chat.force-ranged", ChatListener.RANGED_MODE));
        config.setProperty("chat-range", pexConfig.getDouble("permissions.chat.chat-range", ChatListener.CHAT_RANGE));

        config.save();
    }

}
