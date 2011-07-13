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

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public class ChatListener extends PlayerListener {
    
    public final static String MESSAGE_FORMAT = "<%prefix%player%suffix> %message";
    public final static String GLOBAL_MESSAGE_FORMAT = "<%prefix%player%suffix> &e%message";
    public final static Boolean RANGED_MODE = false;
    public final static double CHAT_RANGE = 100d;

    protected String messageFormat = MESSAGE_FORMAT;
    protected String globalMessageFormat = GLOBAL_MESSAGE_FORMAT;
    protected boolean rangedMode = RANGED_MODE;
    protected double chatRange = CHAT_RANGE;
    
    protected String optionChatRange = "chat-range";
    protected String optionMessageFormat = "message-format";
    protected String optionGlobalMessageFormat = "global-message-format";
    protected String optionRangedMode = "force-ranged-mode";

    public ChatListener(Configuration config) {
        this.messageFormat = config.getString("message-format", this.messageFormat);
        this.globalMessageFormat = config.getString("global-message-format", this.globalMessageFormat);
        this.rangedMode = config.getBoolean("ranged-mode", this.rangedMode);
        this.chatRange = config.getDouble("chat-range", this.chatRange);
        
        this.optionChatRange = config.getString("options.chat-range", this.optionChatRange);
        this.optionGlobalMessageFormat = config.getString("options.global-message-format", this.optionGlobalMessageFormat);
        this.optionMessageFormat = config.getString("options.message-format", this.optionMessageFormat);
        this.optionRangedMode = config.getString("options.ranged-mode", this.optionRangedMode);
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user == null) {
            return;
        }

        String message = user.getOption(this.optionMessageFormat, player.getWorld().getName(), messageFormat);
        boolean localChat = user.getOptionBoolean(this.optionRangedMode, player.getWorld().getName(), rangedMode);

        String chatMessage = event.getMessage();
        if (chatMessage.startsWith("!") && user.has("chatmanager.chat.global", player.getWorld().getName())) {
            localChat = false;
            chatMessage = chatMessage.substring(1);

            message = user.getOption(this.optionGlobalMessageFormat, player.getWorld().getName(), globalMessageFormat);
        }

        message = this.colorize(message);

        if (user.has("chatmanager.chat.color", player.getWorld().getName())) {
            chatMessage = this.colorize(chatMessage);
        }

        message = message.replace("%prefix", this.colorize(user.getPrefix())).replace("%suffix", this.colorize(user.getSuffix())).replace("%world", player.getWorld().getName()).replace("%message", chatMessage).replace("%player", player.getName());

        message = this.replaceTime(message);


        event.setFormat("%2$s");
        event.setMessage(message);

        if (localChat) {
            double range = user.getOptionDouble(this.optionChatRange, player.getWorld().getName(), chatRange);
            
            event.getRecipients().clear();
            event.getRecipients().addAll(this.getLocalRecipients(player, message, range));
        }
    }

    protected List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
            // Recipient are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }

            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) {
                continue;
            }

            recipients.add(recipient);
        }
        return recipients;
    }

    protected String replaceTime(String message) {
        Calendar calendar = Calendar.getInstance();

        if (message.contains("%h")) {
            message = message.replace("%h", String.format("%02d", calendar.get(Calendar.HOUR)));
        }

        if (message.contains("%H")) {
            message = message.replace("%H", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
        }

        if (message.contains("%g")) {
            message = message.replace("%g", Integer.toString(calendar.get(Calendar.HOUR)));
        }

        if (message.contains("%G")) {
            message = message.replace("%G", Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        }

        if (message.contains("%i")) {
            message = message.replace("%i", String.format("%02d", calendar.get(Calendar.MINUTE)));
        }

        if (message.contains("%s")) {
            message = message.replace("%s", String.format("%02d", calendar.get(Calendar.SECOND)));
        }

        if (message.contains("%a")) {
            message = message.replace("%a", (calendar.get(Calendar.AM_PM) == 0) ? "am" : "pm");
        }

        if (message.contains("%A")) {
            message = message.replace("%A", (calendar.get(Calendar.AM_PM) == 0) ? "AM" : "PM");
        }

        return message;
    }

    protected String colorize(String string) {
        return string.replaceAll("&([a-z0-9])", "\u00A7$1");
    }
}
