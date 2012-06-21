package ru.tehkode.chatmanager.channels;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.channels.AbstractChannel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class GlobalChannel extends AbstractChannel {

    public GlobalChannel(ChatManager manager) {
        super(manager, "global");
    }

    @Override
    public boolean isSubscriber(Speaker speaker) {
        return speaker.hasPermission(Server.BROADCAST_CHANNEL_USERS);
    }

    @Override
    public String getSelector() {
        return "!";
    }
}
