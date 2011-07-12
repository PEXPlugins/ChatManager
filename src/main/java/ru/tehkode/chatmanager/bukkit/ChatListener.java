/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    public ChatListener(Configuration config) {
        this.messageFormat = config.getString("permissions.chat.format", this.messageFormat);
        this.globalMessageFormat = config.getString("permissions.chat.global-format", this.globalMessageFormat);
        this.rangedMode = config.getBoolean("permissions.chat.force-ranged", this.rangedMode);
        this.chatRange = config.getDouble("permissions.chat.chat-range", this.chatRange);
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

        String message = user.getOption("message-format", player.getWorld().getName(), messageFormat);
        boolean localChat = user.getOptionBoolean("force-ranged-chat", player.getWorld().getName(), rangedMode);

        String chatMessage = event.getMessage();
        if (chatMessage.startsWith("!") && user.has("permissions.chat.global", player.getWorld().getName())) {
            localChat = false;
            chatMessage = chatMessage.substring(1);

            message = user.getOption("global-message-format", player.getWorld().getName(), globalMessageFormat);
        }

        message = this.colorize(message);

        if (user.has("permissions.chat.color", player.getWorld().getName())) {
            chatMessage = this.colorize(chatMessage);
        }

        message = message.replace("%prefix", this.colorize(user.getPrefix())).replace("%suffix", this.colorize(user.getSuffix())).replace("%world", player.getWorld().getName()).replace("%message", chatMessage).replace("%player", player.getName());

        message = this.replaceTime(message);


        event.setFormat("%2$s");
        event.setMessage(message);

        if (localChat) {
            event.getRecipients().clear();
            event.getRecipients().addAll(this.getLocalRecipients(player, message, user.getOptionDouble("chat-range", player.getWorld().getName(), chatRange)));
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
