package ru.tehkode.chatmanager.adapters;

import org.bukkit.Server;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.placeholders.PlaceholderCollection;

public interface Adapter {

    public void initialize(ChatManager manager);
    
    public Speaker createSpeaker(String name, Server server);

    public PlaceholderCollection getPlaceholders();

    public boolean isActive();
}
