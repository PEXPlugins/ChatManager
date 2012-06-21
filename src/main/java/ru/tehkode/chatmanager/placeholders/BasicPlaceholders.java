package ru.tehkode.chatmanager.placeholders;

import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.channels.Channel;
import ru.tehkode.chatmanager.format.AbstractPlaceholders;
import ru.tehkode.chatmanager.utils.ChatUtils;

import java.util.regex.Matcher;

public class BasicPlaceholders extends AbstractPlaceholders {


    @PlaceholderMethod("message")
    public String message(String arg, Message message) {
        // Sanitize message
        String text = ChatUtils.stripColor(message.getText());

        Speaker sender = message.getSender();
        Channel channel = message.getChannel();
        // Colorize
        if (sender.hasPermission("chatmanager.chat.color") ||
                (channel != null && sender.hasPermission("chatmanager.chat." + channel.getName() + ".color"))) {

            // Common colorize
            text = ChatUtils.colorize(text);

        } else { // @TODO: make configuration toggle

            // Strict colorize
            Matcher matcher = ChatUtils.chatColorPattern.matcher(text);
            while (matcher.find()) {
                String color = matcher.group(1);
                if (sender.hasPermission("chatmanager.chat.color." + ChatUtils.colorName(color)) ||
                        (channel != null && sender.hasPermission("chatmanager.chat." + channel.getName() + ".color." + ChatUtils.colorName(color)))) {

                    text = text.replace(matcher.group(0), "\u00A7" + color);
                }
            }
        }

        message.setText(text);


        return "%2$s";
    }


    @PlaceholderMethod("channel")
    public String channel(String arg, Message message) {

        if (arg == null || "title".equals(arg)) {
            return message.getChannel().getTitle();
        } else if ("name".equals(arg)) {
            return message.getChannel().getName();
        }

        // add channel related stuff here

        return null;
    }
}
