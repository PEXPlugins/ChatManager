package ru.tehkode.chatmanager.placeholders;

import ru.tehkode.chatmanager.Message;

public interface Placeholder {
        
    public String run(String name, String arg, Message message);

}
