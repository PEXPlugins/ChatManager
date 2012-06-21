package ru.tehkode.chatmanager;

import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.format.MessageFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimpleMessage implements Message {
    // Basic properties
    protected final Speaker sender;
    protected final Calendar date;

    protected String message;

    // Advanced properties
    protected MessageFormat format;
    protected Channel channel;

    public SimpleMessage(final Speaker sender, final String message) {
        this.sender = sender;
        this.message = message;
        this.date = GregorianCalendar.getInstance();
    }

    @Override
    public String getText() {
        return this.message;
    }

    @Override
    public Calendar getDate() {
        return this.date;
    }

    public void setText(String message) {
        this.message = message;
    }


    @Override
    public Speaker getSender() {
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
