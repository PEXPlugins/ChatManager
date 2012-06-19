package ru.tehkode.chatmanager.channels;

import org.bukkit.entity.Player;

import java.util.Set;

public interface Channel {
    
    public String getName();
    
    public String getTitle();

    public Set<Player> getSubscribers(Player sender);

}
