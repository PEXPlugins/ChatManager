package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.placeholders.PlaceholderCollection;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMessageFormat implements MessageFormat {
    protected static Pattern placeholderPattern = Pattern.compile("\\%([a-zA-Z_0-9]+)(?:\\[([^\\]]+)\\])?");
    
    protected final Object[] compiledFormat;

    private transient final StringBuilder builder = new StringBuilder(128);

    protected SimpleMessageFormat(Object[] compliedFormat) {
        this.compiledFormat = compliedFormat;
    }
    
    @Override
    public String format(Message message, PlaceholderCollection placeholders) {
        builder.setLength(0); // reset builder

        for(Object piece : this.compiledFormat) {
            if (piece instanceof PlaceholderBinding) {
                PlaceholderBinding binding = (PlaceholderBinding)piece;
                piece = binding.invoke(message, placeholders);
            }

            builder.append(piece.toString());
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Object obj : this.compiledFormat) {
            builder.append(obj);
        }

        return builder.toString();
    }

    static SimpleMessageFormat compile(String format) {

        List<Object> stack = new LinkedList<Object>();

        Matcher matcher = placeholderPattern.matcher(format);

        int last = 0;
        while(matcher.find()) {
            int current  = format.indexOf(matcher.group(0), last);

            if (current - last > 0) {
                stack.add(format.substring(last, current));
            }

            stack.add(new PlaceholderBinding(matcher.group(1), matcher.group(2)));

            last = current + matcher.group(0).length();
        }
        
        if (last < format.length()) {
            stack.add(format.substring(last));
        }

        return new SimpleMessageFormat(stack.toArray());
    }
    
    protected final static class PlaceholderBinding {

        private final String name;
        private final String arg;

        public PlaceholderBinding(final String name, final String arg) {
            this.name = name;
            this.arg = arg; 
        } 
        
        public String invoke(Message message, PlaceholderCollection placeholder) {
            String result = placeholder.run(name, arg, message);
            return result != null ? result : "";
        }

        @Override
        public String toString() {
            return "%" + this.name + (arg != null ? "[" + arg + "]" : "");
        }
    }
    
}
