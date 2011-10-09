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
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public class ChatManagerPlugin extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    
    protected ChatManager manager;

    public ChatManagerPlugin() {
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
		
        if (config.getBoolean("enable", false)) {
            this.manager = new ChatManager(config);
            this.manager.registerEvents(this);
            logger.info("[ChatManager] ChatManager enabled!");
        } else {
            logger.info("[ChatManager] ChatManager disabled. Check config.yml!");
            this.getPluginLoader().disablePlugin(this);
        }

        config.save();
    }

    @Override
    public void onDisable() {
        this.manager = null;
        
        logger.info("[ChatManager] ChatManager disabled!");
    }
}
