package ru.tehkode.chatmanager;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.PlaceholderManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatManager implements Listener {

    protected Server server;

    protected Channel defaultChannel;

    protected MessageFormat defaultFormat;

    protected Map<String, Channel> channels = new HashMap<String, Channel>();

    protected Map<String, Speaker> speakers = new HashMap<String, Speaker>();

    protected PlaceholderManager placeholders = new PlaceholderManager();

    public ChatManager(Server server) {
        this.server = server;
    }

    public PlaceholderManager getPlaceholders() {
        return this.placeholders;
    }

    public Speaker getSpeaker(OfflinePlayer player) {
        if (!speakers.containsKey(player.getName())) {
            speakers.put(player.getName(), new SimpleSpeaker(this, this.server, player));
        }

        return speakers.get(player.getName());
    }

    public Speaker getSpeaker(String name) {
        if (!speakers.containsKey(name)) {
            Speaker speaker = new SimpleSpeaker(this, this.server, this.server.getOfflinePlayer(name));
            speakers.put(speaker.getName(), speaker);
            return speaker;
        }

        return speakers.get(name);
    }

    public Iterable<Speaker> getOnlineSpeakers() {
        Set<Speaker> speakers = new HashSet<Speaker>(this.server.getMaxPlayers());
        for (Player player : this.server.getOnlinePlayers()) {
            speakers.add(this.getSpeaker(player));
        }

        return speakers;
    }

    public Iterable<Speaker> getSpeakers() {
        return this.speakers.values();
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public Channel getChannel(String name) {
        return channels.get(name);
    }

    public Iterable<Channel> getChannels() {
        return this.channels.values();
    }

    public Iterable<Channel> getChannels(final Speaker speaker) {
        return Collections2.filter(this.channels.values(), new Predicate<Channel>() {
            @Override
            public boolean apply(@Nullable Channel channel) {
                return channel.isSubscriber(speaker);
            }
        });
    }

    public void addChannel(Channel channel) {
        this.channels.put(channel.getName(), channel);
    }

    public void removeChannel(Channel channel) {
        this.channels.remove(channel);
    }

    public MessageFormat getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(MessageFormat defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Speaker speaker = this.getSpeaker(event.getPlayer());

        Message message = new SimpleMessage(speaker, event.getMessage());
        Channel channel = selectChannel(message);
        message.setChannel(channel);

        // Check if player muted
        if (speaker.isMuted() || channel.isMuted(speaker)) {
            // @todo - make this message customized
            speaker.sendMessage(ChatColor.GRAY + "You are muted");
            event.setCancelled(true);
            return;
        }

        // Find recipients
        Set<Player> recipients = event.getRecipients();
        recipients.clear();
        
        for (Speaker receiver : channel.getSubscribers(message)) {
            if (!receiver.isOnline() || receiver.isIgnore(speaker)) {
                continue;
            }

            recipients.add(receiver.getPlayer());
        }

        // Format message
        event.setFormat(channel.getMessageFormat().format(message));

        // Put message back into event
        event.setMessage(message.getText());
    }

    protected Channel selectChannel(Message message) {
        String text = message.getText();
        for (Channel channel : this.channels.values()) {
            String selector = channel.getSelector();

            if (text.startsWith(selector)) {
                message.setText(text.substring(selector.length()));
                return channel;
            }
        }

        return message.getSender().getDefaultChannel();
    }

}
