/*
 * PermissionsEx - Permissions plugin for Bukkit
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
package ru.tehkode.chatmanager;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatMessage {
	
	protected String rawMessage;
	protected Player sender;
	protected Set<Player> recipients;
	
	public ChatMessage(PlayerChatEvent event){
		this.rawMessage = event.getMessage();
		this.recipients = event.getRecipients();
		this.sender = event.getPlayer();
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public Set<Player> getRecipients() {
		return recipients;
	}

	public Player getSender() {
		return sender;
	}	
	
}
