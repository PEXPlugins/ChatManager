/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.ConfigurationNode;
import ru.tehkode.chatmanager.channels.ChatChannel;
import ru.tehkode.chatmanager.filters.ConfigMessageFilter;
import ru.tehkode.chatmanager.placeholders.BuiltinPlaceholders;

/**
 *
 * @author t3hk0d3
 */
public class ChatManager extends ChatChannel {
	
	protected final static Logger logger = Logger.getLogger("Minecraft");
	protected List<ChatChannel> channels = new LinkedList<ChatChannel>();
	
	public ChatManager(ConfigurationNode config) {
		this.addPlaceholder(new BuiltinPlaceholders());
		this.loadFilters(config);
	}
	
	protected void loadFilters(ConfigurationNode node) {
		List<String> configFilters = node.getKeys("filters");
		
		if(configFilters == null){
			return;
		}
		
		for (String filterName : configFilters) {
			this.addFilter(new ConfigMessageFilter(node.getNode("filters." + filterName)));
		}
	}
	
	@Override
	public void processMessage(PlayerChatEvent event) {
		for (ChatChannel channel : this.channels) {
			if (channel.isApplicable(event)) {
				channel.processMessage(event);
				
				return;
			}
		}
		
		super.processMessage(event);
	}
	
	public void registerEvents(Plugin plugin) {
		PluginManager manager = plugin.getServer().getPluginManager();
		manager.registerEvent(Type.PLAYER_CHAT, new ChatListener(), Priority.Normal, plugin);
	}
	
	protected class ChatListener extends PlayerListener {
		
		@Override
		public void onPlayerChat(PlayerChatEvent event) {
			if (event.isCancelled()) {
				return;
			}
			
			processMessage(event);
		}
	}
}
