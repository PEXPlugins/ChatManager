package ru.tehkode.chatmanager;

import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.format.MessageFormat;

import java.util.Calendar;

public interface Message {

    String getText();

    void setText(String message);

    Speaker getSender();

    Channel getChannel();

    void setChannel(Channel channel);

    MessageFormat getFormat();

    void setFormat(MessageFormat format);

    Calendar getDate();
}
