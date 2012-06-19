package ru.tehkode.chatmanager.placeholders;

import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.format.AbstractPlaceholders;
import ru.tehkode.chatmanager.utils.ChatUtils;

import java.util.regex.Matcher;

public class BasicPlaceholders extends AbstractPlaceholders {


    @PlaceholderMethod("message")
    public String message(String arg, Message message) {
        Player player = message.getSender();
        String text = message.getMessage();

        // Santize message
        text = ChatUtils.stripColor(text);

        // Colorize
        if (message.getSender().hasPermission("chatmanager.chat.color") ||
                message.getSender().hasPermission("chatmanager.chat." + message.getChannel().getName() + ".color")) {
            // Common colorize
            text = ChatUtils.colorize(text);

        } else { // @TODO: make configuration toggle
            // Strict colorize
            Matcher matcher = ChatUtils.chatColorPattern.matcher(text);
            while (matcher.find()) {
                String color = matcher.group(1);
                if (player.hasPermission("chatmanager.chat.color." + ChatUtils.colorName(color)) ||
                        player.hasPermission("chatmanager.chat." + message.getChannel().getName() + ".color." + ChatUtils.colorName(color))) {

                    text = text.replace(matcher.group(0), "\u00A7" + color);
                }
            }
        }

        return text;
    }
    

    @PlaceholderMethod("channel")
    public String channel(String arg, Message message){

        if(arg == null || "title".equals(arg)) {
            return message.getChannel().getTitle();
        } else if ("name".equals(arg)) {
            return message.getChannel().getName();
        }

        // add channel related stuff here

        return null;
    }
}
