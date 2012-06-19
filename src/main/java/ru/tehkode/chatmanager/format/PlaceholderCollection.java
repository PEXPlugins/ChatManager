package ru.tehkode.chatmanager.format;

import java.util.Set;

public interface PlaceholderCollection extends Placeholder {

    public Set<String> getPlaceholders();

    public Placeholder getPlaceholder(String name);
}
