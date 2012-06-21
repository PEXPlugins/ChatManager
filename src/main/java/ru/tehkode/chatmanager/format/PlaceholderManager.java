package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlaceholderManager implements PlaceholderCollection {

    protected Map<String, Placeholder> placeholders = new HashMap<String, Placeholder>();

    @Override
    public String run(String name, String arg, Message message) {
        if (placeholders.containsKey(name.toLowerCase())) {
            return placeholders.get(name.toLowerCase()).run(name, arg, message);
        }

        return null;
    }

    @Override
    public Set<String> getPlaceholders() {
        return this.placeholders.keySet();
    }

    public Placeholder getPlaceholder(String name) {
        return this.placeholders.get(name.toLowerCase());
    }

    public void registerPlaceholder(String name, Placeholder placeholder) {
        if (placeholder instanceof PlaceholderCollection) {
            registerPlaceholder((PlaceholderCollection)placeholder);
            return;
        }
        
        this.placeholders.put(name.toLowerCase(), placeholder);
    }

    public void registerPlaceholder(PlaceholderCollection collection) {
        for (String name : collection.getPlaceholders()) {
            this.registerPlaceholder(name, collection.getPlaceholder(name));
        }
    }

}
