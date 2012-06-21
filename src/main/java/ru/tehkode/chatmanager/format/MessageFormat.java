package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;

/**
 * Created by IntelliJ IDEA.
 * User: code
 * Date: 21.06.12
 * Time: 6:28
 * To change this template use File | Settings | File Templates.
 */
public interface MessageFormat {
    String format(Message message);
}
