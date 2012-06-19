package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;

public interface Placeholder {
        
    public String run(String name, String arg, Message message);

}
