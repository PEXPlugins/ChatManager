/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.placeholders;

import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author t3hk0d3
 */
public interface PlaceholderExecutor {

    public String replace(String pattern, PlayerChatEvent event, PermissionUser user) throws Throwable;
}
