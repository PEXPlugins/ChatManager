package ru.tehkode.chatmanager.format;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.channels.GlobalChannel;
import ru.tehkode.chatmanager.placeholders.BasicPlaceholders;
import ru.tehkode.chatmanager.placeholders.PlayerPlaceholder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageFormatTest {

    PlaceholderManager manager = new PlaceholderManager();

    Player mockSender;
    Message message;

    @Before
    public void setup() {
        manager.registerPlaceholder("player", new PlayerPlaceholder());
        manager.registerPlaceholder(new BasicPlaceholders());

        mockSender = mock(Player.class);
        message = mock(Message.class);

        when(mockSender.getName()).thenReturn("testPlayer");
        when(message.getSender()).thenReturn(mockSender);
        when(message.getChannel()).thenReturn(new GlobalChannel());
    }

    @Test
    public void testCompiling() {
        MessageFormat format = MessageFormat.compile("<%player> %message", this.manager);

        when(message.getMessage()).thenReturn("this is test &fmessage");
        
        format.format(message);

    }

}
