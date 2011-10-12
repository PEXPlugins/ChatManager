package ru.tehkode.chatmanager.bukkit;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import ru.tehkode.chatmanager.bukkit.utils.MultiverseConnector;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class PluginListener extends ServerListener {
    private ChatManager plugin;

    public PluginListener(ChatManager mgr) {
        this.plugin = mgr;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        this.checkForMultiverse(event.getPlugin());
    }

    public void checkForMultiverse(Plugin p) {
        if (p != null && p.getDescription().getName().equalsIgnoreCase("Multiverse-Core")) {
            this.plugin.listener.setupMultiverseConnector(new MultiverseConnector((MultiverseCore) p));
            ChatManager.logger.info("[ChatManager] Multiverse 2 integration enabled!");
        }
    }
}
