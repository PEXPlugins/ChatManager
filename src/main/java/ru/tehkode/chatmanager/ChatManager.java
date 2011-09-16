/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.ConfigurationNode;
import ru.tehkode.chatmanager.filters.ConfigMessageFilter;
import ru.tehkode.chatmanager.filters.MessageFilter;
import ru.tehkode.chatmanager.placeholders.PlaceholderCollection;

/**
 *
 * @author t3hk0d3
 */
public class ChatManager extends PlayerListener {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    protected List<MessageFilter> filters = new LinkedList<MessageFilter>();
    protected List<PlaceholderCollection> placeholders = new LinkedList<PlaceholderCollection>();
    
    public ChatManager(ConfigurationNode config) {
        
    }
    
    public void registerFilters(ConfigurationNode config) {
        Map<String, ConfigurationNode> configFilters = config.getNodes("filters");
        
        for (Map.Entry<String, ConfigurationNode> entry : configFilters.entrySet()) {
            try {
                this.filters.add(new ConfigMessageFilter(entry.getValue()));
            } catch (Throwable e) {
                logger.warning("Failed to initialize filter \"" + entry.getKey() + "\": " + e.getMessage());
            }
        }
    }
    
    public void registerEvents(Plugin plugin) {
        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvent(Type.PLAYER_CHAT, this, Priority.Normal, plugin);
    }
    
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
    }
}
