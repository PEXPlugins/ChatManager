package ru.tehkode.chatmanager.channels;

import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.channels.AbstractChannel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlobalChannel extends AbstractChannel {

    public GlobalChannel() {
        super("global");
    }

    public GlobalChannel(String name) {
        super(name);
    }

    @Override
    public Set<Player> getSubscribers(Player sender) {
        return new HashSet<Player>(Arrays.asList(sender.getServer().getOnlinePlayers()));
    }

}
