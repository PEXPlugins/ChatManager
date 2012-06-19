package ru.tehkode.chatmanager.placeholders;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.channels.GlobalChannel;
import ru.tehkode.chatmanager.utils.ChatUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BasicPlaceholdersTest {

    Player player;
    Message message;

    BasicPlaceholders placeholders = new BasicPlaceholders();

    @Before
    public void setUp() throws Exception {
        player = mock(Player.class);
        message = mock(Message.class);

        when(player.getName()).thenReturn("testPlayer");

        when(message.getSender()).thenReturn(player);
        when(message.getChannel()).thenReturn(new GlobalChannel());
    }

    @Test
    public void testMessage() throws Exception {
        when(message.getMessage()).thenReturn("this is \u00A7fcolorized \u00A7kmessage text");
        // Basic (color striped)
        assertEquals("this is colorized message text", placeholders.run("message", null, message));
        verify(message).getMessage();

        reset(player);
    }

    @Test
    public void testMessageColorized() {
        // Colorized
        when(message.getMessage()).thenReturn("this is &f colorized &kmessage text");
        when(player.hasPermission("chatmanager.chat.color")).thenReturn(true);
        assertEquals("this is \u00A7f colorized \u00A7kmessage text", placeholders.run("message", null, message));

        reset(player);
    }

    @Test
    public void testMessageColorizedPerChannel() {
        // Colorized per-channel
        when(message.getMessage()).thenReturn("this is &f colorized &kmessage text");
        when(player.hasPermission("chatmanager.chat.global.color")).thenReturn(true);
        assertEquals("this is \u00A7f colorized \u00A7kmessage text", placeholders.run("message", null, message));

        reset(player);
    }

    @Test
    public void testMessageStrictColorized() {
        // Colorized strict
        when(message.getMessage()).thenReturn("this is &f colorized &kmessage text");
        when(player.hasPermission("chatmanager.chat.color." + ChatUtils.colorName("f"))).thenReturn(true);

        assertEquals("this is \u00A7f colorized &kmessage text", placeholders.run("message", null, message));

        reset(player);
    }

    @Test
    public void testMessageStrictColorizedPerChannel() {
        // Colorized per-channel strict
        when(message.getMessage()).thenReturn("this is &f colorized &kmessage text");
        when(player.hasPermission("chatmanager.chat.global.color." + ChatUtils.colorName("f"))).thenReturn(true);
        assertEquals("this is \u00A7f colorized &kmessage text", placeholders.run("message", null, message));

        reset(player);
    }
}
