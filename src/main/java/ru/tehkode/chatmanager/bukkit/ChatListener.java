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
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

import ru.tehkode.chatmanager.bukkit.utils.MultiverseConnector;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public class ChatListener implements Listener {
	protected static Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
	protected static Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
	protected static Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
	protected static Pattern chatStrikethroughPattern = Pattern.compile("(?i)&([M])");
	protected static Pattern chatUnderlinePattern = Pattern.compile("(?i)&([N])");
	protected static Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
	protected static Pattern chatResetPattern = Pattern.compile("(?i)&([R])");

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
	protected String permissionChatColor = "chatmanager.chat.color";
	protected String permissionChatMagic = "chatmanager.chat.magic";
	protected String permissionChatBold = "chatmanager.chat.bold";
	protected String permissionChatStrikethrough = "chatmanager.chat.strikethrough";
	protected String permissionChatUnderline = "chatmanager.chat.underline";
	protected String permissionChatItalic = "chatmanager.chat.italic";
        protected boolean prefixBuffer = false; //determines if prefixes should have spaces
        protected boolean suffixBuffer = false; //determines if suffixes should have spaces
	private MultiverseConnector multiverseConnector;

	public ChatListener(FileConfiguration config) {
		this.messageFormat = config.getString("message-format", this.messageFormat);
		this.globalMessageFormat = config.getString("global-message-format", this.globalMessageFormat);
		this.rangedMode = config.getBoolean("ranged-mode", this.rangedMode);
		this.chatRange = config.getDouble("chat-range", this.chatRange);
		this.displayNameFormat = config.getString("display-name-format", this.displayNameFormat);
                this.prefixBuffer = config.getBoolean("buffer.prefix");
                this.suffixBuffer = config.getBoolean("buffer.suffix");
	}

	@EventHandler(priority = EventPriority.MONITOR) // so that this will be the last thing to occur
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

		message = this.translateColorCodes(message);

		chatMessage = this.translateColorCodes(chatMessage, user, worldName);

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
		player.setDisplayName(this.translateColorCodes(this.replacePlayerPlaceholders(player, user.getOption(this.optionDisplayname, worldName, this.displayNameFormat))));
	}

	protected String replacePlayerPlaceholders(Player player, String format) {
                //cleaned this up so it was more readable and moved color code formatting to the end
                //so that it is done at once instead of multiple times
		PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
                String worldName = player.getWorld().getName();
                format = format.replace("%prefix", getPrefixes(user, worldName));
                format = format.replace("%suffix", getSuffixes(user, worldName));
                format = format.replace("%world", getWorldAlias(worldName));
                format = format.replace("%player", player.getName());
                format = translateColorCodes(format);

                return format;
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

	protected String translateColorCodes(String string) {
		if (string == null) {
			return "";
		}

		String newstring = string;
		newstring = chatColorPattern.matcher(newstring).replaceAll("\u00A7$1");
		newstring = chatMagicPattern.matcher(newstring).replaceAll("\u00A7$1");
		newstring = chatBoldPattern.matcher(newstring).replaceAll("\u00A7$1");
		newstring = chatStrikethroughPattern.matcher(newstring).replaceAll("\u00A7$1");
		newstring = chatUnderlinePattern.matcher(newstring).replaceAll("\u00A7$1");
		newstring = chatItalicPattern.matcher(newstring).replaceAll("\u00A7$1");
		newstring = chatResetPattern.matcher(newstring).replaceAll("\u00A7$1");
		return newstring;
	}

	protected String translateColorCodes(String string, PermissionUser user, String worldName) {
		if (string == null) {
			return "";
		}

		String newstring = string;
		if (user.has(permissionChatColor, worldName)) {
			newstring = chatColorPattern.matcher(newstring).replaceAll("\u00A7$1");
		}
		if (user.has(permissionChatMagic, worldName)) {
			newstring = chatMagicPattern.matcher(newstring).replaceAll("\u00A7$1");
		}
		if (user.has(permissionChatBold, worldName)) {
			newstring = chatBoldPattern.matcher(newstring).replaceAll("\u00A7$1");
		}
		if (user.has(permissionChatStrikethrough, worldName)) {
			newstring = chatStrikethroughPattern.matcher(newstring).replaceAll("\u00A7$1");
		}
		if (user.has(permissionChatUnderline, worldName)) {
			newstring = chatUnderlinePattern.matcher(newstring).replaceAll("\u00A7$1");
		}
		if (user.has(permissionChatItalic, worldName)) {
			newstring = chatItalicPattern.matcher(newstring).replaceAll("\u00A7$1");
		}
		newstring = chatResetPattern.matcher(newstring).replaceAll("\u00A7$1");
		return newstring;
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
     * @return A colored worldstring if the connector is present, the normal world if it is not.
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
     * Handles the prefixes. This uses permissions and group-defined prefixes.
     * Permissions will have the final say which the group is the default
     *
     * @param user The name of the player
     * @param world The world the player is in
     * @return The prefix for the player
     */
    public String getPrefixes(PermissionUser user, String world) {
        String personalPrefix = user.getOwnPrefix();
        PermissionGroup[] allGroups = PermissionsEx.getPermissionManager().getGroups();
        String finalPrefix = "";

        //this sets the player's personal prefix to be the prefix
        //which will be the final one if no perms are set
        if (personalPrefix.isEmpty()) {
            finalPrefix = personalPrefix;
        }

        //this loops though a player's own permissions to see if they have any prefixes defined
        //which is added to the list so the prefix for that group is added
        //CURRENTLY USELESS UNTIL THE PERMISSIONUSER GETS THIS METHOD OVERRIDDEN >_>
        //will work on a solution
        List<String> specialGroups = new ArrayList<String>();
        for (String perm : user.getOwnPermissions(world)) {
            if (perm.startsWith("prefix.")) {
                String testGroup = perm.substring(8).trim().toLowerCase();
                specialGroups.add(testGroup);
            }
        }

        //adds in the user's groups first in order they are in
        PermissionGroup[] userGroups = user.getGroups();
        for (PermissionGroup group : userGroups) {
            if (!group.has("*")) { //this is used to make sure all perms does not cause all suffixes
                String prefix = group.getOwnPrefix();
                if (prefix != null && !prefix.isEmpty()) { //just to make sure there is in fact a prefix to add
                    if (prefixBuffer) {
                        if (finalPrefix.isEmpty()) { //in case there is no prefixes beforehand, prevents a ghost space
                            finalPrefix = prefix;
                        } else {
                            finalPrefix += " " + prefix;
                        }
                    } else {
                        finalPrefix += prefix;

                    }
                }
            }
        }

        //checks the remaining groups to ensure all prefixes are added
        for (PermissionGroup group : allGroups) {
            if (!user.inGroup(group)) { //this is used to make the groups they are already in are not re-added
                if (user.has("prefix." + group.getName().toLowerCase())) {
                    String prefix = group.getOwnPrefix();
                    if (prefix != null && !prefix.isEmpty()) {
                        if (prefixBuffer) {
                            if (finalPrefix.isEmpty()) {
                                finalPrefix = prefix;
                            } else {
                                finalPrefix += " " + prefix;
                            }
                        } else {
                            finalPrefix += prefix;
                        }
                    }
                }
            }
        }

        //if there are no prefixes found perm-wise, this will set the group default prefix
        if (finalPrefix.isEmpty()) {
            finalPrefix = user.getGroups()[0].getOwnPrefix();
        }

        //if there is no prefix so far, just sets it to "" to remove the %prefix and not add anything
        if (finalPrefix == null || finalPrefix.isEmpty()) {
            finalPrefix = "";
        }
        return finalPrefix;
    }

    /**
     * Handles the suffixes. This uses permissions and group-defined suffixes.
     * Permissions will have the final say which the group is the default
     *
     * @param user The name of the player
     * @param world The world the player is in
     * @return The modified message
     */
    public String getSuffixes(PermissionUser user, String world) {
        //check prefix method for comments as they aer the same here
        String personalSuffix = user.getOwnSuffix();
        PermissionGroup[] allGroups = PermissionsEx.getPermissionManager().getGroups();
        String finalSuffix = "";

        if (personalSuffix != null && !personalSuffix.isEmpty()) {
            finalSuffix = personalSuffix;
        }

        List<String> specialGroups = new ArrayList<String>();
        for (String perm : user.getOwnPermissions(world)) {
            if (perm.startsWith("suffix.")) {
                String testGroup = perm.substring(8).trim().toLowerCase();
                specialGroups.add(testGroup);
            }
        }

        PermissionGroup[] userGroups = user.getGroups();
        for (PermissionGroup group : userGroups) {
            if (!group.has("*")) {
                String suffix = group.getOwnSuffix();
                if (suffix != null && !suffix.isEmpty()) {
                    if (suffixBuffer) {
                        if (finalSuffix.isEmpty()) {
                            finalSuffix = suffix;
                        } else {
                            finalSuffix += " " + suffix;
                        }
                    } else {
                        finalSuffix += suffix;

                    }
                }
            }
        }

        for (PermissionGroup group : allGroups) {
            if (!user.inGroup(group)) {
                if (user.has("suffix." + group.getName().toLowerCase())) {
                    String suffix = group.getOwnSuffix();
                    if (suffix != null && !suffix.isEmpty()) {
                        if (suffixBuffer) {
                            if (finalSuffix.isEmpty()) {
                                finalSuffix = suffix;
                            } else {
                                finalSuffix += " " + suffix;
                            }
                        } else {
                            finalSuffix += suffix;

                        }
                    }
                }
            }
        }

        if (finalSuffix.isEmpty()) {
            finalSuffix = user.getGroups()[0].getOwnSuffix();
        }

        if (finalSuffix == null || finalSuffix.isEmpty()) {
            finalSuffix = "";
        }
        return finalSuffix;
    }
}
