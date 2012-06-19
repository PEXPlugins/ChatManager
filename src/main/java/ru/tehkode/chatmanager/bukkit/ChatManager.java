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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public class ChatManager extends JavaPlugin {

    protected static Logger log;
    protected ChatListener listener;

    @Override
    public void onEnable() {
    	log = this.getLogger();
    	
        // At first check PEX existence
        try {
            PermissionsEx.getPermissionManager();
        } catch (Throwable e) {
            log.severe("PermissionsEx not found, disabling");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        FileConfiguration config = this.getConfig();

        if (config.get("enable") == null) { // Migrate
            this.initializeConfiguration(config);
        }

        this.listener = new ChatListener(config);

        if (config.getBoolean("enable", false)) {
            this.getServer().getPluginManager().registerEvents(listener, this);
            log.info("ChatManager enabled!");
            // Make sure MV didn't load before we did.
            this.listener.checkForMultiverse(this.getServer().getPluginManager().getPlugin("Multiverse-Core"));
        } else {
        	log.info("ChatManager disabled. Check config.yml!");
            this.getPluginLoader().disablePlugin(this);
        }

        this.saveConfig();
    }

    @Override
    public void onDisable() {
        this.listener = null;
        
        log.info("ChatManager disabled!");
    }

    protected void initializeConfiguration(FileConfiguration config) {
        // At migrate and setup defaults
        PermissionsEx pex = (PermissionsEx) this.getServer().getPluginManager().getPlugin("PermissionsEx");

        FileConfiguration pexConfig = pex.getConfig();

        // Flags
        config.set("enable", pexConfig.getBoolean("permissions.chat.enable", false));
        config.set("message-format", pexConfig.getString("permissions.chat.format", ChatListener.MESSAGE_FORMAT));
        config.set("global-message-format", pexConfig.getString("permissions.chat.global-format", ChatListener.GLOBAL_MESSAGE_FORMAT));
        config.set("ranged-mode", pexConfig.getBoolean("permissions.chat.force-ranged", ChatListener.RANGED_MODE));
        config.set("chat-range", pexConfig.getDouble("permissions.chat.chat-range", ChatListener.CHAT_RANGE));
        
        pex.saveConfig();
    }

}
