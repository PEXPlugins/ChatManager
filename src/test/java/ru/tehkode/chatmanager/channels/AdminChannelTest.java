package ru.tehkode.chatmanager.channels;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class AdminChannelTest {
    
    protected Player sender;
    
    protected Player justPlayer;

    protected Player administratorPlayer;

    @Before
    public void setup() {
        Server server = mock(Server.class);
                                               
        sender = mock(Player.class);
        when(sender.getServer()).thenReturn(server);
        
        justPlayer = mock(Player.class);
        administratorPlayer = mock(Player.class);
        
        when(administratorPlayer.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE)).thenReturn(true);
        
        when(server.getOnlinePlayers()).thenReturn(new Player[] {sender, justPlayer, administratorPlayer});

    }

    @Test
    public void testGetSubscribers() throws Exception {
        AdminChannel channel = new AdminChannel();

        Set<Player> result = channel.getSubscribers(sender);

        assertTrue("Should contain admin", result.contains(administratorPlayer));

        assertFalse("Should NOT contain normal player", result.contains(justPlayer));

    }
}
