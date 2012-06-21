package ru.tehkode.chatmanager.channels;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.SimpleMessageFormat;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractChannel implements Channel {

    protected final ChatManager manager;
    protected final String name;

    protected SimpleMessageFormat format;
    
    private Set<Speaker> muted = new HashSet<Speaker>();

    public AbstractChannel(ChatManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        // This should be overriden
        return this.name;
    }

    @Override
    public final Iterable<Speaker> getSubscribers(final Message message) {
        return Iterables.filter(this.manager.getOnlineSpeakers(), new Predicate<Speaker>() {
            @Override
            public boolean apply(@Nullable Speaker speaker) {
                return applySender(message, speaker);
            }
        });
    }

    @Override
    public Iterable<Speaker> getMuted() {
        return this.muted;
    }

    @Override
    public boolean isMuted(Speaker speaker) {
        return this.muted.contains(speaker);
    }

    @Override
    public void setMute(Speaker speaker, boolean mute) {
        if (mute) {
            this.muted.add(speaker);
        } else {
            this.muted.remove(speaker);
        }
    }

    @Override
    public MessageFormat getMessageFormat() {
        return this.format != null ? this.format : manager.getDefaultFormat();
    }

    @Override
    public void setMessageFormat(SimpleMessageFormat format) {
        this.format = format;
    }


    @Override
    public String getSelector() {
        return "#" + this.name;
    }

    // Override method
    protected boolean applySender(Message message, Speaker receiver) {
        return this.isSubscriber(receiver);
    }
}
