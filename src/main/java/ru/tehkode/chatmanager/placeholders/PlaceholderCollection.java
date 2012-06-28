package ru.tehkode.chatmanager.placeholders;

import java.util.Set;

public interface PlaceholderCollection extends Placeholder {

    public Set<String> getPlaceholders();

    public Placeholder getPlaceholder(String name);
}
