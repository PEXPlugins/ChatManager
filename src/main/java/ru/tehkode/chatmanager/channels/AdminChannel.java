package ru.tehkode.chatmanager.channels;


import org.bukkit.Server;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.MessageFormatFactory;

public final class AdminChannel extends AbstractChannel {

    public static MessageFormat DEFAULT_FORMAT = MessageFormatFactory.create("&6<%player>&f %message");

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
