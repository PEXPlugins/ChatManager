package ru.tehkode.chatmanager.channels;


import org.bukkit.Server;
import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;

import java.util.HashSet;
import java.util.Set;

public final class AdminChannel extends AbstractChannel {

    public AdminChannel(ChatManager manager) {
        super(manager, "admin");
    }

    @Override
    public boolean isSubscriber(Speaker speaker) {
        return speaker.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
    }

    @Override
    public String getSelector() {
        return "$";
    }
}
