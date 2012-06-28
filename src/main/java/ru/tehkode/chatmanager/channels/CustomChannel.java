package ru.tehkode.chatmanager.channels;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.MessageFormatFactory;
import ru.tehkode.chatmanager.format.SimpleMessageFormat;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomChannel extends AbstractChannel implements ManageableChannel {

    public final static MessageFormat DEFAULT_FORMAT = MessageFormatFactory.create("&7[#%channel]&f <%player> %message");

    protected String title;

    protected Speaker owner;

    protected Map<Speaker, Boolean> subscribers = new HashMap<Speaker, Boolean>();
    
    protected boolean isPrivate = false;

    public CustomChannel(ChatManager manager, String name) {
        super(manager, name);

        this.title = name;
    }

    public CustomChannel(ChatManager manager, String name, ConfigurationSection config) {
        this(manager, name);

        readConfig(config);
    }

    @Override
    public String getTitle() {
        return this.title != null ? this.title : super.getTitle();
    }

    @Override
    public Iterable<Speaker> getSubscribers() {
        return this.subscribers.keySet();
    }

    @Override
    public void addSubscriber(Speaker subscriber) {
        this.subscribers.put(subscriber, true);
    }

    @Override
    public void removeSubscriber(Speaker subscriber) {
        this.subscribers.remove(subscriber);
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public Iterable<Speaker> getMuted() {
        return Maps.filterValues(this.subscribers, new Predicate<Boolean>() {
            @Override
            public boolean apply(@Nullable Boolean aBoolean) {
                return !aBoolean;
            }
        }).keySet();
    }

    @Override
    public boolean isMuted(Speaker speaker) {
        return !subscribers.containsKey(speaker) || !subscribers.get(speaker);
    }

    @Override
    public void setMute(Speaker speaker, boolean mute) {
        if (this.subscribers.containsKey(speaker)) {
            this.subscribers.put(speaker, !mute);
        }
    }

    @Override
    public boolean isSubscriber(Speaker speaker) {
        return subscribers.containsKey(speaker);
    }

    @Override
    public Speaker getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(Speaker owner) {
        this.owner = owner;
    }

    @Override
    public boolean hasOwner() {
        return this.owner != null;
    }

    protected void readConfig(ConfigurationSection config) {
        this.title = config.getString("title");

        this.setPrivate(config.getBoolean("private", this.isPrivate));

        if (config.isString("owner")) {
            this.owner = manager.getSpeaker(config.getString("owner"));
        }

        if (config.isString("format")) {
            this.setMessageFormat(MessageFormatFactory.create(config.getString("format")));
        }

        if (config.isConfigurationSection("subscribers")) {
            ConfigurationSection subConfig = config.getConfigurationSection("subscribers");
            for (String subscriber : subConfig.getKeys(false)) {
                if (!subConfig.isBoolean(subscriber)) {
                    continue;
                }

                this.subscribers.put(this.manager.getSpeaker(subscriber), subConfig.getBoolean(subscriber, true));
            }
        }

        if (config.isList("subscribers")) {
            for (String subscriber : config.getStringList("subscribers")) {
                this.subscribers.put(this.manager.getSpeaker(subscriber), true);
            }
        }
    }

    public void saveConfig(ConfigurationSection config) {
        if(this.title != null) {
            config.set("title", title);
        }
        
        if (this.owner != null) {
            config.set("owner", this.owner.getName());
        }
        
        if (this.format != null) {
            config.set("format", this.format.toString());
        }
        
        if (this.subscribers != null && !this.subscribers.isEmpty()){
            if (this.subscribers.containsValue(false)) {  // save as map
                Map<String, Boolean> sub = new HashMap<String, Boolean>(this.subscribers.size());

                for (Map.Entry<Speaker, Boolean> entry : this.subscribers.entrySet()) {
                    sub.put(entry.getKey().getName(), entry.getValue());
                }

                config.set("subscribers", sub);
            } else {  // save as list
                List<String> sub = new LinkedList<String>();
                
                for (Speaker subscriber : this.subscribers.keySet()) {
                    sub.add(subscriber.getName());
                }

                config.set("subscribers", sub);
            }
        }
    }
}
