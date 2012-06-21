package ru.tehkode.chatmanager.channels;

import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.SimpleMessageFormat;

public interface Channel {
    
    public String getName();
    
    public String getTitle();

    public boolean isSubscriber(Speaker speaker);

    public Iterable<Speaker> getSubscribers(Message message);

    public MessageFormat getMessageFormat();

    public void setMessageFormat(SimpleMessageFormat format);

    public String getSelector();

    public boolean isMuted(Speaker speaker);

    public void setMute(Speaker speaker, boolean mute);

    public Iterable<Speaker> getMuted();

}
