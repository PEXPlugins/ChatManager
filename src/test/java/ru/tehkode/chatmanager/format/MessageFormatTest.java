package ru.tehkode.chatmanager.format;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;
import ru.tehkode.chatmanager.channels.GlobalChannel;
import ru.tehkode.chatmanager.placeholders.BasicPlaceholders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class MessageFormatTest {

    PlaceholderManager manager = new PlaceholderManager();

    Speaker mockSender;
    Message message;

    @Before
    public void setup() {
        manager.registerPlaceholder(new BasicPlaceholders());

        mockSender = mock(Speaker.class);
        message = mock(Message.class);

        when(mockSender.getName()).thenReturn("testPlayer");
        when(message.getSender()).thenReturn(mockSender);
        when(message.getChannel()).thenReturn(new GlobalChannel(null));
    }

    @Test
    public void testCompiling() {
        MessageFormat format = SimpleMessageFormat.compile("<%player> %message");

        Assert.assertEquals("<%player> %message", format.toString());

    }


    @Test
    public void testFormatting() {
        MessageFormat format = SimpleMessageFormat.compile("<%player> %message");

        when(message.getText()).thenReturn("this is test &fmessage");

        Assert.assertEquals("<%1$s> %2$s", format.format(message, manager));

    }
}
