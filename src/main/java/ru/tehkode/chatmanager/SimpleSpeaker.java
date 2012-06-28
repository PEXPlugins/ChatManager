package ru.tehkode.chatmanager;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.channels.ManageableChannel;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.MessageFormatFactory;
import ru.tehkode.chatmanager.format.SimpleMessageFormat;

import java.util.HashSet;
import java.util.Set;

public class SimpleSpeaker implements Speaker {

    public final static MessageFormat DISPLAY_NAME_FORMAT = MessageFormatFactory.create("%prefix%player%suffix");

    protected final transient ChatManager manager;

    protected final transient Server server;

    protected final String playerName;

    protected Set<Speaker> ignoreList = new HashSet<Speaker>();

    protected Channel defaultChannel;

    protected boolean muted = false;

    protected MessageFormat displayNameFormat = DISPLAY_NAME_FORMAT;

    public SimpleSpeaker(final ChatManager manager, final Server server, final String playerName) {
        this.manager = manager;
        this.server = server;
        this.playerName = playerName;
    }

    public SimpleSpeaker(final ChatManager manager, final Server server, final OfflinePlayer player) {
        this(manager, server, player.getName());
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    @Override
    public World getWorld() {
        Player player = this.getPlayer();
        return player != null ? player.getWorld() : null;
    }

    @Override
    public String getWorldName() {
        World world = this.getWorld();
        return world != null ? world.getName() : null;
    }

    @Override
    public boolean isOnline() {
        return this.server.getOfflinePlayer(this.playerName).isOnline();
    }

    @Override
    public Player getPlayer() {
        return this.server.getPlayerExact(this.playerName);
    }

    @Override
    public String getDisplayName() {
        return this.getPlayer().getDisplayName();
    }

    @Override
    public MessageFormat getDisplayNameFormat() {
        return this.displayNameFormat;
    }

    @Override
    public void setDisplayNameFormat(MessageFormat format) {
        this.displayNameFormat = format;
    }

    @Override
    public Channel getDefaultChannel() {
        return this.defaultChannel != null ? this.defaultChannel : manager.getDefaultChannel(this.isOnline() ? this.getPlayer().getWorld() : null);
    }

    @Override
    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    @Override
    public Iterable<Channel> getChannels() {
        return manager.getChannels(this);
    }

    @Override
    public void addChannel(Channel channel) {
        if (channel instanceof ManageableChannel) {
            ((ManageableChannel) channel).addSubscriber(this);
        }
    }

    @Override
    public void removeChannel(Channel channel) {
        if (channel instanceof ManageableChannel) {
            ((ManageableChannel) channel).removeSubscriber(this);
        }
    }

    @Override
    public Set<Speaker> getIgnoreList() {
        return this.ignoreList;
    }

    @Override
    public void addIgnore(Speaker speaker) {
        this.ignoreList.add(speaker);
    }

    @Override
    public void removeIgnore(Speaker speaker) {
        this.ignoreList.remove(speaker);
    }

    @Override
    public boolean isIgnore(Speaker speaker) {
        return this.ignoreList.contains(speaker);
    }

    @Override
    public boolean isMuted() {
        return this.muted;
    }

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
    }


    @Override
    public void sendMessage(String s) {
        if (this.isOnline()) {
            this.getPlayer().sendMessage(s);
        }
    }

    @Override
    public void sendMessage(String[] strings) {
        if (this.isOnline()) {
            this.getPlayer().sendMessage(strings);
        }
    }

    @Override
    public boolean isPermissionSet(String s) {
        return this.isOnline() ? this.getPlayer().isPermissionSet(s) : false;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.isOnline() ? this.getPlayer().isPermissionSet(permission) : false;
    }

    @Override
    public boolean hasPermission(String s) {
        return this.isOnline() ? this.getPlayer().hasPermission(s) : false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.isOnline() ? this.getPlayer().hasPermission(permission) : false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return this.getPlayer().addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.getPlayer().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return this.getPlayer().addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return this.getPlayer().addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        this.getPlayer().removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        if (this.isOnline()) {
            this.getPlayer().recalculatePermissions();
        }
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.getPlayer().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return this.server.getOfflinePlayer(this.getName()).isOp();
    }

    @Override
    public void setOp(boolean b) {
        this.server.getOfflinePlayer(this.getName()).setOp(b);
    }

    @Override
    public String toString() {
        return "SimpleSpeaker{name=" + this.getName() + ", online=" + this.isOnline() + "}";
    }
}
