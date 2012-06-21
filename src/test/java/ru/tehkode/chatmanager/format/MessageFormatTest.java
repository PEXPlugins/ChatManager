package ru.tehkode.chatmanager.format;

import org.junit.Before;
import org.junit.Test;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.channels.GlobalChannel;
import ru.tehkode.chatmanager.placeholders.BasicPlaceholders;
import ru.tehkode.chatmanager.placeholders.PlayerPlaceholder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageFormatTest {

    PlaceholderManager manager = new PlaceholderManager();

    Speaker mockSender;
    Message message;

    MessageFormat format;

    @Before
    public void setup() {
        manager.registerPlaceholder("player", new PlayerPlaceholder());
        manager.registerPlaceholder(new BasicPlaceholders());

        format = SimpleMessageFormat.compile("<%player> %message", manager);

        mockSender = mock(Speaker.class);
        message = mock(Message.class);

        when(mockSender.getName()).thenReturn("testPlayer");
        when(message.getSender()).thenReturn(mockSender);
        when(message.getChannel()).thenReturn(new GlobalChannel(null));
    }

    @Test
    public void testCompiling() {
        when(message.getText()).thenReturn("this is test &fmessage");

        System.out.println("Format: " + format);
        System.out.println("Result: " + format.format(message));

    }


    @Test
    public void testFormatting() {

    }
}
