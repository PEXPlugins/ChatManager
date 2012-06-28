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

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.chatmanager.ChatManager;

import java.io.File;
import java.util.logging.Logger;

/**
 *
 * @author t3hk0d3
 */
public class ChatManagerPlugin extends JavaPlugin {

    protected static Logger log;
    protected ChatManager manager;

    @Override
    public void onLoad() {
        this.log = this.getLogger();
    }

    @Override
    public void onEnable() {
        manager = new ChatManager(this.getServer());

        // load channels
        manager.loadConfig(this.getConfig());
        manager.loadChannels(YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "channels.yml")));
        manager.loadSpeakers(YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "speakers.yml")));

        //
        this.getServer().getPluginManager().registerEvents(manager, this);
        log.info("ChatManager enabled!");
    }

    @Override
    public void onDisable() {
        manager = null;

        
        log.info("ChatManager disabled!");
    }
    
    public static void warning(Object... message) {
        log.warning(compile(message));
    }
    
    public static void debug(Object... message) {
        log.severe(compile("[DEBUG] ", message));
    }
    
    private static String compile(Object... message) {
        StringBuilder builder = new StringBuilder();
        for (Object part : message) {
            if(part instanceof Object[]) {
                part = compile((Object[])part);
            }
            builder.append(part);
        }
        return builder.toString();
    }
}
