package ru.tehkode.chatmanager.adapters;

import org.bukkit.Server;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.SimpleSpeaker;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.format.MessageFormat;
import ru.tehkode.chatmanager.format.MessageFormatFactory;
import ru.tehkode.chatmanager.placeholders.PlaceholderCollection;
import ru.tehkode.chatmanager.placeholders.PEXPlaceholders;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public final class PermissionsExAdapter implements Adapter {


    private ChatManager chatManager;

    @Override
    public void initialize(ChatManager manager) {
        this.chatManager = manager;
    }

    @Override
    public Speaker createSpeaker(String name, Server server) {
        return new PEXSpeaker(this.chatManager, server, name);
    }

    @Override
    public PlaceholderCollection getPlaceholders() {
        return new PEXPlaceholders(PermissionsEx.getPermissionManager());
    }

    @Override
    public boolean isActive() {
        return PermissionsEx.isAvailable();
    }

    protected class PEXSpeaker extends SimpleSpeaker {

        public PEXSpeaker(ChatManager manager, Server server, String name) {
            super(manager, server, name);
        }

        @Override
        public MessageFormat getDisplayNameFormat() {
            String format = PermissionsEx.getUser(this.getName()).getOption("display-name-format", this.getWorldName());
            return format != null ? MessageFormatFactory.create(format) : super.getDisplayNameFormat();
        }
    }


}
