package ru.tehkode.chatmanager.placeholders;

import ru.tehkode.chatmanager.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlaceholderManager implements PlaceholderCollection {
    
    public static long runTime = 0;
    public static long runCalls = 0;

    protected Map<String, Placeholder> placeholders = new HashMap<String, Placeholder>();

    public PlaceholderManager() {
        this.registerPlaceholder(new BasicPlaceholders());
    }

    public PlaceholderManager(Map<String, Placeholder> placeholders) {
        this();

        this.placeholders.putAll(placeholders);
    }

    @Override
    public String run(String name, String arg, Message message) {
        long start = System.currentTimeMillis();
        Placeholder placeholder = this.getPlaceholder(name);

        String result = placeholder != null ? placeholder.run(name, arg, message) : null;
        
        runTime += (System.currentTimeMillis() - start);
        runCalls++;
        

        return result;
    }

    @Override
    public Set<String> getPlaceholders() {
        return this.placeholders.keySet();
    }

    public Placeholder getPlaceholder(String name) {
        return this.placeholders.get(name);
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
