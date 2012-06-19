package ru.tehkode.chatmanager.channels;


import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Set;

public class AdminChannel extends GlobalChannel {

    public AdminChannel() {
        super("admin");
    }

    public AdminChannel(String name) {
        super(name);
    }

    @Override
    public Set<Player> getSubscribers(Player sender) {
        Set<Player> recv = super.getSubscribers(sender);

        // Filter recipients
        for (Player player : recv) {
            if (!player.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)){
                recv.remove(player);
            }
        }

        return recv;
    }
}
