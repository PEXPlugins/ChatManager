/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.filters;

import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author t3hk0d3
 */
public class ConfigMessageFilter implements MessageFilter {

    public final static String DEFAULT_REPLACEMENT = "%censored%";
    MessageFilter filter = null;

    public ConfigMessageFilter(ConfigurationNode config) {
        String type = config.getString("type");
        if (type == null) {
            throw new IllegalArgumentException("Specify message filter type - 'word' or 'regexp'.");
        }

        String replacement = config.getString("replacement", DEFAULT_REPLACEMENT);

        if (type.equalsIgnoreCase("word")) {
            this.filter = new WordMessageFilter(config.getStringList("words", null), replacement);
        } else if (type.equalsIgnoreCase("regexp")) {
            this.filter = new RegexpMessageFilter(config.getString("regexp"), replacement);
        } else {
            throw new IllegalArgumentException("Unknown filter specified - \"" + type + "\".");
        }
    }

    @Override
    public String filter(String message) {
        return (this.filter != null) ? this.filter.filter(message) : message;
    }
}
