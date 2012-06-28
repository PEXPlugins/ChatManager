package ru.tehkode.chatmanager;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.tehkode.chatmanager.bukkit.ChatManagerPlugin;
import ru.tehkode.chatmanager.channels.*;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.MessageFormatFactory;
import ru.tehkode.chatmanager.placeholders.PlaceholderManager;
import ru.tehkode.chatmanager.utils.ChatUtils;

import javax.annotation.Nullable;
import java.util.*;

public class ChatManager implements Listener {

    protected final Server server;

    protected final Map<String, Channel> channels = new HashMap<String, Channel>();

    protected final Map<World, Channel> defaultChannels = new HashMap<World, Channel>();

    protected final Map<String, MessageFormat> defaultFormats = new HashMap<String, MessageFormat>();

    protected final Map<String, Speaker> speakers = new HashMap<String, Speaker>();

    protected final PlaceholderManager placeholders = new PlaceholderManager();

    public ChatManager(Server server) {
        this.server = server;

        // default channels
        this.addChannel(new GlobalChannel(this));
        this.addChannel(new RangedChannel(this));

        this.addChannel(new AdminChannel(this));
    }

    public PlaceholderManager getPlaceholders() {
        return this.placeholders;
    }

    public void loadConfig(ConfigurationSection config) {
        this.setDefaultFormat(MessageFormatFactory.create(config.getString("message-format", Channel.DEFAULT_FORMAT.toString())));

        if (config.isConfigurationSection("message-formats")) {
            ConfigurationSection formatsSection = config.getConfigurationSection("message-formats");
            
            for (String channelType : formatsSection.getKeys(false)) {
                if (!formatsSection.isString(channelType)){
                    continue;
                }

                MessageFormat format = MessageFormatFactory.create(formatsSection.getString(channelType));

                this.setDefaultFormat(format, channelType);
            }
        }

        if (config.isConfigurationSection("channels")) {
            loadChannels(config.getConfigurationSection("channels"));
        }

        if (config.isConfigurationSection("speakers")) {
            loadSpeakers(config.getConfigurationSection("speakers"));
        }

        // Read default channel
        Channel defaultChannel = this.getChannel(config.getString("default-channel", "global"));

        if (defaultChannel == null) {
            defaultChannel = this.getChannel("global");
            // @todo print warning into console
        }

        setDefaultChannel(defaultChannel);

        // Read per world default channels
        if (config.isConfigurationSection("default-channels")) {
            ConfigurationSection channels = config.getConfigurationSection("default-channels");
            
            for (String worldName : channels.getKeys(false)) {
                if (!channels.isString(worldName)) {
                    continue;
                }
                
                Channel channel = this.getChannel(channels.getString(worldName));
                if (channel == null){
                    // @todo print warning
                    continue;
                }


            }
        }
    }

    public void loadChannels(ConfigurationSection config) {
        for (String name : config.getKeys(false)) {
            if (!config.isConfigurationSection(name)) {
                continue;
            }

            this.addChannel(new CustomChannel(this, name, config.getConfigurationSection(name)));
        }
    }

    public void loadSpeakers(ConfigurationSection config) {
        for (String name : config.getKeys(false)) {
            if (!config.isConfigurationSection(name)) {
                return;
            }

            Speaker speaker = this.getSpeaker(name);
            ConfigurationSection settings = config.getConfigurationSection(name);

            // default channel
            if (settings.isString("defaultChannel")) {
                speaker.setDefaultChannel(this.getChannel(settings.getString("defaultChannel")));
            }

            // load muted state
            speaker.setMuted(settings.getBoolean("muted", false));

            // load ignore list
            if (settings.isList("ignore")) {
                List<String> ignoreList = settings.getStringList("ignore");

                for (String ignored : ignoreList) {
                    speaker.addIgnore(this.getSpeaker(ignored));
                }
            }
        }
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
        return defaultChannels.get(null);
    }

    public Channel getDefaultChannel(World world) {
        return defaultChannels.containsKey(world) ? defaultChannels.get(world) : getDefaultChannel();
    }
    
    public void setDefaultChannel(World world, Channel channel) {
        this.defaultChannels.put(world, channel);
    }
    
    public void setDefaultChannel(Channel channel) {
        this.defaultChannels.put(null, channel);
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
        return defaultFormats.get(null);
    }

    public MessageFormat getDefaultFormat(String channelType) {
        return defaultFormats.containsKey(channelType) ? defaultFormats.get(channelType.toLowerCase()) : getDefaultFormat();
    }
    
    public MessageFormat getDefaultFormat(Class<? extends Channel> channelClass) {
        String type = channelClassToString(channelClass);
        return defaultFormats.containsKey(type) ? defaultFormats.get(type) : getDefaultFormat();
    }

    public void setDefaultFormat(MessageFormat defaultFormat) {
        this.defaultFormats.put(null, defaultFormat);
    }
    
    public void setDefaultFormat(MessageFormat format, Class<? extends Channel> channelClass) {
        this.defaultFormats.put(channelClassToString(channelClass), format);
    }

    public void setDefaultFormat(MessageFormat defaultFormat, String type) {
        this.defaultFormats.put(type, defaultFormat);
    }
    
    protected String channelClassToString(Class<? extends Channel> channelClass) {
        String type = channelClass.getSimpleName();

        int suffix = type.lastIndexOf("Channel");
        if(suffix > 0) { // cutoff "Channel"
            type = type.substring(0, suffix);
        }

        return type.toLowerCase();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {


    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(PlayerChatEvent event) {
        Speaker speaker = this.getSpeaker(event.getPlayer());
        Message message = new SimpleMessage(speaker, event.getMessage());

        ChatManagerPlugin.debug("Incoming message - ", message);

        Channel channel = selectChannel(message);
        message.setChannel(channel);

        ChatManagerPlugin.debug("Selected channel - ", channel);

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

        ChatManagerPlugin.debug("Recipients - ", recipients);

        // Format message
        event.setFormat(ChatUtils.colorize(channel.getMessageFormat().format(message, this.placeholders)));

        ChatManagerPlugin.debug("Message formatted - ", event.getFormat());

        // Put message back into event
        event.setMessage(message.getText());

        ChatManagerPlugin.debug("Message text - ", event.getMessage());

        ChatManagerPlugin.debug("Result - " + event.isCancelled());
        ChatManagerPlugin.debug("Recipients - " + event.getRecipients());
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
