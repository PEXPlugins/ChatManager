package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.placeholders.PlaceholderCollection;

public interface MessageFormat {

    String format(Message message, PlaceholderCollection placeholders);

    String toString();
}
