package ru.tehkode.chatmanager.placeholders;


import ru.tehkode.chatmanager.Message;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXPlaceholders extends AbstractPlaceholders {

    private PermissionManager manager;

    public PEXPlaceholders(PermissionManager manager) {
        this.manager = manager;
    }

    protected PermissionUser user(Message message) {
        return PermissionsEx.getUser(message.getSender().getName());
    }

    protected PermissionGroup[] getGroups(Message message) {
        return user(message).getGroups(message.getSender().getWorldName());
    }

    protected void appendNotNull(StringBuffer buffer, String string) {
        if (string != null) {
            buffer.append(string);
        }
    }


    @PlaceholderMethod("option")
    public String option(String arg, Message message) {
        return user(message).getOption(arg, message.getSender().getWorldName(), null);
    }

    /*
    * @todo refactor shit out of prefix/suffix methods - !!!!CODE DUPLICATION!!!!
    * Possible solution - private callback interface
    */
    @PlaceholderMethod("prefix")
    public String prefix(String arg, Message message) {
        if ("all".equals(arg)) {
            StringBuffer buffer = new StringBuffer();

            appendNotNull(buffer, user(message).getOwnPrefix(message.getSender().getWorldName()));

            for (PermissionGroup group : getGroups(message)) {
                if (buffer.length() > 0) {
                    // @todo make this char configurable (via arg?)
                    buffer.append(' '); // append spacer char
                }

                appendNotNull(buffer, group.getPrefix(message.getSender().getWorldName()));
            }

            return buffer.toString();
        } else if ("own".equals(arg)) {
            return user(message).getOwnPrefix(message.getSender().getWorldName());
        }

        return user(message).getPrefix(message.getSender().getWorldName());
    }

    @PlaceholderMethod("suffix")
    public String suffix(String arg, Message message) {
        if ("all".equals(arg)) {
            StringBuffer buffer = new StringBuffer();

            appendNotNull(buffer, user(message).getOwnSuffix(message.getSender().getWorldName()));

            for (PermissionGroup group : getGroups(message)) {
                if (buffer.length() > 0) {
                    // @todo make this char configurable (via arg?)
                    buffer.append(' '); // append spacer char
                }

                appendNotNull(buffer, group.getSuffix(message.getSender().getWorldName()));
            }

            return buffer.toString();
        } else if ("own".equals(arg)) {
            return user(message).getOwnSuffix(message.getSender().getWorldName());
        }

        return user(message).getSuffix(message.getSender().getWorldName());
    }


    @PlaceholderMethod("group")
    public String group(String arg, Message message) {
        return user(message).getGroups(message.getSender().getWorldName())[0].getName();
    }

    @PlaceholderMethod("groups")
    public String groups(String arg, Message message) {
        StringBuffer buffer = new StringBuffer();

        for (PermissionGroup group : getGroups(message)) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }

            buffer.append(group.getName());
        }

        return buffer.toString();
    }
}

