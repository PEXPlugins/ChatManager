package ru.tehkode.chatmanager.placeholders;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import ru.tehkode.chatmanager.Message;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class PlayerPlaceholderTest {

    PlayerPlaceholder placeholder;
    Player player;
    Message message;

    @Before
    public void setup() {
        placeholder = new PlayerPlaceholder();

        player = mock(Player.class);
        World world = mock(World.class);

        // Mock player
        when(player.getName()).thenReturn("testPlayer");
        when(player.getDisplayName()).thenReturn("testDisplayPlayer");
        when(player.getWorld()).thenReturn(world);
        when(player.getHealth()).thenReturn(25);     // Should give 50%
        when(player.getMaxHealth()).thenReturn(50);

        // Mock world
        when(world.getName()).thenReturn("TestWorld");

        // Mock message
        message = mock(Message.class);
        when(message.getSender()).thenReturn(player);
    }

    @Test
    public void testDisplayName() throws Exception {
        // Default call (%player)
        assertEquals("testDisplayPlayer", placeholder.run("player", null, message));
        // %player[displayname]
        assertEquals("testDisplayPlayer", placeholder.run("player", "displayname", message));
        verify(player, atLeast(2)).getDisplayName();
    }

    @Test
    public void testPlayerName() throws Exception {
        // %player[name]
        assertEquals("testPlayer", placeholder.run("player", "name", message));
        verify(player).getName();
    }

    @Test
    public void testWorld() {
        // %player[world]
        assertEquals("TestWorld", placeholder.run("player", "world", message));
        verify(player).getWorld();
    }

    @Test
    public void testHealth() {
        // %player[health]
        assertEquals("50%", placeholder.run("player", "health", message));
        verify(player).getHealth();
        verify(player).getMaxHealth();
    }
}
