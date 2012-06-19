package ru.tehkode.chatmanager.placeholders;

import org.bukkit.entity.Player;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.format.Placeholder;

public class PlayerPlaceholder implements Placeholder {


    @Override
    public String run(String name, String arg, Message message) {
        Player player = message.getSender();

        if(arg == null || "displayname".equals(arg)) {
            return player.getDisplayName();
        } else if ("name".equals(arg)) {
            return player.getName();
        } else if ("world".equals(arg)) {
            return player.getWorld().getName();
        } else if ("health".equals(arg)) {
            return (int)(((float)player.getHealth()/player.getMaxHealth())*100) + "%";
        }

        return null;
    }
}
