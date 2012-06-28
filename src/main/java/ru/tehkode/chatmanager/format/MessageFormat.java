package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;

public interface MessageFormat {

    String format(Message message, PlaceholderCollection placeholders);

    String toString();
}
