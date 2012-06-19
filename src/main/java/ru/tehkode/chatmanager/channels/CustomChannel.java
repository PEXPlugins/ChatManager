package ru.tehkode.chatmanager.channels;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class CustomChannel extends AbstractChannel implements ManageableChannel {

    public CustomChannel(ConfigurationSection section) {
        super("boo!");
    }

    @Override
    public void addSubscriber(Player subscriber) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeSubscriber(Player player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<Player> getSubscribers(Player sender) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
