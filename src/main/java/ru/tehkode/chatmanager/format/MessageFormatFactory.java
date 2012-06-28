package ru.tehkode.chatmanager.format;

import java.util.HashMap;
import java.util.Map;

public class MessageFormatFactory {
    
    protected static Map<String, MessageFormat> formatCache = new HashMap<String, MessageFormat>();
    
    public static MessageFormat create(String format) {
        if (!formatCache.containsKey(format)) {
            // @todo make more flexible format choosing
            formatCache.put(format, SimpleMessageFormat.compile(format));
        }

        return formatCache.get(format);
    }
}
