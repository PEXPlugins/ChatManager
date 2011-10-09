/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.placeholders;

import org.bukkit.event.player.PlayerChatEvent;

/**
 *
 * @author code
 */
public interface Placeholder {
	public final static String SIGN = "%";

	public String[] getPatterns();
	
	public String getValue(String pattern, String value, PlayerChatEvent event);
	
	public void start(PlayerChatEvent event);
}
