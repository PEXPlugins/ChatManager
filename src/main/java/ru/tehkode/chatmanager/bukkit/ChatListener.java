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

import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.tehkode.chatmanager.bukkit.channel.ChatManagerChannel;
import ru.tehkode.chatmanager.bukkit.utils.MultiverseConnector;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public class ChatListener implements Listener {

    protected static Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
    protected static Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
    public final static String MESSAGE_FORMAT = "<%prefix%player%suffix> %message";
    public final static String GLOBAL_MESSAGE_FORMAT = "<%prefix%player%suffix> &e%message";
    public final static Boolean RANGED_MODE = false;
    public final static double CHAT_RANGE = 100d;
    protected String messageFormat = MESSAGE_FORMAT;
    protected String globalMessageFormat = GLOBAL_MESSAGE_FORMAT;
    protected boolean rangedMode = RANGED_MODE;
    protected double chatRange = CHAT_RANGE;
    protected String displayNameFormat = "%prefix%player%suffix";
    protected String optionChatRange = "chat-range";
    protected String optionMessageFormat = "message-format";
    protected String optionGlobalMessageFormat = "global-message-format";
    protected String optionRangedMode = "force-ranged-mode";
    protected String optionDisplayname = "display-name-format";
    protected static boolean overrideMainGroup = true;
    protected static boolean reverseSuffixOrder = false;
    private MultiverseConnector multiverseConnector;

    public ChatListener(FileConfiguration config) {
        this.messageFormat = config.getString("message-format", this.messageFormat);
        this.globalMessageFormat = config.getString("global-message-format", this.globalMessageFormat);
        this.rangedMode = config.getBoolean("ranged-mode", this.rangedMode);
        this.chatRange = config.getDouble("chat-range", this.chatRange);
        this.displayNameFormat = config.getString("display-name-format", displayNameFormat);
        overrideMainGroup = config.getBoolean("override-main-group-prefix", overrideMainGroup);
        reverseSuffixOrder = config.getBoolean("reverse-suffix-order", reverseSuffixOrder);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        String worldName = player.getWorld().getName();

        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user == null) {
            return;
        }

        String message = user.getOption(this.optionMessageFormat, worldName, messageFormat);
        boolean localChat = user.getOptionBoolean(this.optionRangedMode, worldName, rangedMode);

        String chatMessage = event.getMessage();
        if (chatMessage.startsWith("!") && user.has("chatmanager.chat.global", worldName)) {
            localChat = false;
            chatMessage = chatMessage.substring(1);

            message = user.getOption(this.optionGlobalMessageFormat, worldName, globalMessageFormat);
        }

        message = this.colorize(message);
        message = this.magicify(message);

        if (user.has("chatmanager.chat.color", worldName)) {
            chatMessage = this.colorize(chatMessage);
        }
        if (user.has("chatmanager.chat.magic", worldName)) {
            chatMessage = this.magicify(chatMessage);
        }

        message = message.replace("%message", "%2$s").replace("%displayname", "%1$s");
        message = this.replacePlayerPlaceholders(player, message);
        message = this.replaceTime(message);

        event.setFormat(message);
        event.setMessage(chatMessage);

        if (localChat) {
            double range = user.getOptionDouble(this.optionChatRange, worldName, chatRange);

            event.getRecipients().clear();
            event.getRecipients().addAll(this.getLocalRecipients(player, message, range));
        }
        
        ChatManagerChannel.manageChannel(user, event);
    }

    protected void updateDisplayNames() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            updateDisplayName(player);
        }
    }

    protected void updateDisplayName(Player player) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user == null) {
            return;
        }

        String worldName = player.getWorld().getName();
        player.setDisplayName(this.magicify(this.colorize(this.replacePlayerPlaceholders(player, user.getOption(this.optionDisplayname, worldName, this.displayNameFormat)))));
    }

    protected String replacePlayerPlaceholders(Player player, String format) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        String worldName = player.getWorld().getName();
        String newString = format.replace("%prefix", getAllPrefixes(user, worldName));
        newString += newString.replace("%suffix", getAllSuffixes(user, worldName));
        newString += newString.replace("%world", this.getWorldAlias(worldName));
        newString += newString.replace("%player", player.getName());
        newString = this.colorize(newString);
        newString = this.magicify(newString);
        return newString;
    }

    protected List<Player> getLocalRecipients(Player sender, String message, double range) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new LinkedList<Player>();
        double squaredDistance = Math.pow(range, 2);
        PermissionManager manager = PermissionsEx.getPermissionManager();
        for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
            // Recipient are not from same world
            if (!recipient.getWorld().equals(sender.getWorld())) {
                continue;
            }

            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance && !manager.has(sender, "chatmanager.override.ranged")) {
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
        if (string == null) {
            return "";
        }

        return chatColorPattern.matcher(string).replaceAll("\u00A7$1");
    }

    protected String magicify(String string) {
        if (string == null) {
            return "";
        }

        return chatMagicPattern.matcher(string).replaceAll("\u00A7$1");
    }

    /**
     * Initializes the MVConnector.
     *
     * @param conn The MultiverseConnector instance
     */
    protected void setupMultiverseConnector(MultiverseConnector conn) {
        this.multiverseConnector = conn;
    }

    /**
     * Returns a colored world string provided by Multiverse
     *
     * @param world The world to retrieve the string about.
     * @return A colored worldstring if the connector is present, the normal
     * world if it is not.
     */
    private String getWorldAlias(String world) {
        if (this.multiverseConnector != null) {
            return multiverseConnector.getColoredAliasForWorld(world);
        }
        return world;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        this.checkForMultiverse(event.getPlugin());
    }

    public void checkForMultiverse(Plugin p) {
        if (p != null && p.getDescription().getName().equalsIgnoreCase("Multiverse-Core")) {
            this.setupMultiverseConnector(new MultiverseConnector((MultiverseCore) p));
            ChatManager.log.info("Multiverse 2 integration enabled!");
        }
    }

    /**
     * Returns the string of prefixes for the player
     *
     * @param user The player who is talking
     * @param world The world they are in
     * @return All the prefixes for that player
     */
    public String getAllPrefixes(PermissionUser user, String world) {
        PermissionGroup[] groups = user.getGroups(world);
        PermissionGroup main = groups[0];
        if (main == null) {
            return user.getPrefix(world);
        }
        String prefixes = user.getPrefix(world);
        int i = 0;
        if (overrideMainGroup) {
            i = 1;
        }
        for (; i < groups.length; i++) {
            prefixes += groups[i].getPrefix(world);
        }
        return prefixes;
    }

    /**
     * Returns the string of suffixes for the player
     *
     * @param user The player who is talking
     * @param world The world they are in
     * @return All the suffixes for that player
     */
    public String getAllSuffixes(PermissionUser user, String world) {
        String suffixes = "";
        PermissionGroup[] group1 = user.getGroups(world);
        PermissionGroup[] groups = new PermissionGroup[group1.length];
        if (reverseSuffixOrder) {
            for (int i = 0; i < group1.length; i++) {
                groups[(groups.length - 1) - i] = group1[i];
            }
        } else {
            groups = group1;
        }
        for (PermissionGroup group : groups) {
            suffixes += group.getSuffix(world);
        }
        return suffixes;
    }
}