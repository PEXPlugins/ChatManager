package ru.tehkode.chatmanager.utils;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatUtils {
    public static Pattern chatColorPattern = Pattern.compile("&([a-z0-9])");
    private static Map<String, String> colorNames = new HashMap<String, String>();
    
    public static String colorize(String string) {
        return chatColorPattern.matcher(string).replaceAll("\u00A7$1");
    }

    public static String stripColor(String string) {
        return ChatColor.stripColor(string);
    }
    
    public static String colorName(String colorCode) {
        String name = colorNames.get(colorCode);
        return (name == null) ? colorCode : name;
    }
    
    
    static {
        for(ChatColor color : ChatColor.values()) {
            colorNames.put(""+color.getChar(), color.name().replace("_", "").toLowerCase());
        }
    }
    


}
