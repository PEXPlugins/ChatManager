/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.bukkit.channel;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author Joshua
 */
public class ChatManagerChannel {
    
    public static List<String> channels;
    
    public static void manageChannel(PermissionUser user, PlayerChatEvent event)
    {
        String channel = user.getOption("channel").toLowerCase();
        user.setOption("channel", channel);
        if(!channels.contains(channel))
            return;
    }
    
}
