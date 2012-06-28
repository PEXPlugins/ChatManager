package ru.tehkode.chatmanager;


import org.bukkit.Server;

public class SimpleSpeakerFactory implements SpeakerFactory {

    private ChatManager chatManager;

    public SimpleSpeakerFactory(ChatManager manager) {
        this.chatManager = manager;
    }

    @Override
    public Speaker createSpeaker(Server server, String name) {
        return new SimpleSpeaker(chatManager, server, name);
    }
}
