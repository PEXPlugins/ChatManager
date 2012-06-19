package ru.tehkode.chatmanager.channels;

import org.bukkit.entity.Player;

public interface ManageableChannel extends Channel{

    public void addSubscriber(Player subscriber);

    public void removeSubscriber(Player player);
}
