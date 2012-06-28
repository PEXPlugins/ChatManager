package ru.tehkode.chatmanager;

import org.bukkit.Server;

public interface SpeakerFactory {

    public Speaker createSpeaker(Server server, String name);
}
