/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.placeholders;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.permissions.PermissionUser;

/**
 *
 * @author t3hk0d3
 */
public abstract class PlaceholderCollection {

    protected Map<String, PlaceholderExecutor> placeholders = new HashMap<String, PlaceholderExecutor>();

    public PlaceholderCollection() {
        this.registerPlaceholders();
    }

    public final String replace(String string, PlayerChatEvent event, PermissionUser user) {
        for (Map.Entry<String, PlaceholderExecutor> entry : placeholders.entrySet()) {
            if (!string.contains(entry.getKey())) {
                continue;
            }
            String value = "";

            try {
                value = entry.getValue().replace(entry.getKey(), event, user);
            } catch (Throwable e) {
                Logger.getLogger("Minecraft").warning("[ChatManager] Error during replacing placeholder \"%" + entry.getKey() + "\": " + e.getMessage());
            }

            string = string.replace(entry.getKey(), value);
        }


        return string;
    }

    private void registerPlaceholders() {
        for (Method method : this.getClass().getMethods()) {
            if (!method.isAnnotationPresent(Placeholder.class)) {
                continue;
            }

            Placeholder placeholder = method.getAnnotation(Placeholder.class);
            
            if (placeholder.value().isEmpty()) {
                continue;
            }

            this.placeholders.put(placeholder.value(), this.createExecutor(placeholder.value(), method));
        }
    }

    protected PlaceholderExecutor createExecutor(final String pattern, final Method method) {
        Class<?>[] params = method.getParameterTypes();

        final PlaceholderCollection collection = this;

        return new PlaceholderExecutor() {

            @Override
            public String replace(String pattern, PlayerChatEvent event, PermissionUser user) throws Throwable {
                return (String) method.invoke(collection, pattern, event, user);
            }
        };
    }
}
