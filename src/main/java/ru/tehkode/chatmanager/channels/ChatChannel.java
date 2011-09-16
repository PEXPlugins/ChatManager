/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.channels;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.filters.MessageFilter;

/**
 *
 * @author t3hk0d3
 */
public abstract class ChatChannel {
    
    protected ChatManager manager;
    
    public ChatChannel(ChatManager manager){
        
    }
    
    public abstract List<MessageFilter> getFilters();
    
    public String processChatMessage(PlayerChatEvent event){
        
    }
}
