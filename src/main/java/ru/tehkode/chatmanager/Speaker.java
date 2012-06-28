package ru.tehkode.chatmanager;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.format.MessageFormat;

import java.util.Set;

public interface Speaker extends CommandSender {

    public String getDisplayName();

    public MessageFormat getDisplayNameFormat();

    public void setDisplayNameFormat(MessageFormat format);

    public boolean isOnline();
    
    public World getWorld();
    
    public String getWorldName();

    public Player getPlayer();

    public Channel getDefaultChannel();

    public void setDefaultChannel(Channel defaultChannel);
    
    public Iterable<Channel> getChannels();

    public void addChannel(Channel channel);

    public void removeChannel(Channel channel);

    public boolean isIgnore(Speaker speaker);
    
    public Iterable<Speaker> getIgnoreList();

    public void addIgnore(Speaker speaker);

    public void removeIgnore(Speaker speaker);

    /**
     * Globally muted
     *
     * @return
     */
    public boolean isMuted();

    public void setMuted(boolean muted);

}
