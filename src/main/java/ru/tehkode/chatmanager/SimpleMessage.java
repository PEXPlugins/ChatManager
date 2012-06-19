package ru.tehkode.chatmanager;

import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.format.MessageFormat;

public class SimpleMessage implements Message {
    // Basic properties
    protected final Player sender;
    protected final String message;

    // Advanced properties
    protected MessageFormat format;
    protected Channel channel;

    public SimpleMessage(Player sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public MessageFormat getFormat() {
        return format;
    }

    @Override
    public void setFormat(MessageFormat format) {
        this.format = format;
    }
}
