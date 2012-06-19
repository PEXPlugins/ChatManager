package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageFormat {
    protected static Pattern placeholderPattern = Pattern.compile("\\%([a-zA-Z_0-9]+)(?:\\[([^\\]]+)\\])?");
    
    protected final Object[] compiledFormat;

    protected MessageFormat(Object[] compliedFormat) {
        this.compiledFormat = compliedFormat;
    }
    
    public String format(Message message) {
        StringBuilder builder = new StringBuilder();
        
        for(Object piece : compiledFormat) {
            if (piece instanceof PlaceholderBinding) {
                piece = ((PlaceholderBinding)piece).call(message);
            }

            builder.append(piece.toString());
        }

        return builder.toString();
    }
    
    public static MessageFormat compile(String format, PlaceholderCollection placeholders) {

        List<Object> stack = new LinkedList<Object>();

        Matcher matcher = placeholderPattern.matcher(format);

        int last = 0;
        while(matcher.find()) {
            int current  = format.indexOf(matcher.group(0), last);

            if (current - last > 0) {
                stack.add(format.substring(last, current));
            }

            stack.add(new PlaceholderBinding(placeholders, matcher.group(1), matcher.group(2)));

            last = current + matcher.group(0).length();
        }
        
        if (last < format.length()) {
            stack.add(format.substring(last));
        }

        System.out.println("Format " + format + " -> " + stack);

        return new MessageFormat(stack.toArray());
    }
    
    protected static class PlaceholderBinding {
        
        protected final Placeholder placeholder;
        protected final String name;
        protected final String arg;

        public PlaceholderBinding(Placeholder placeholder, final String name, final String arg) {
            this.placeholder = placeholder;
            this.name = name;
            this.arg = arg; 
        } 
        
        public String call(Message message) {
            String result = placeholder.run(name, arg, message);
            return result != null ? result : "";
        }

        @Override
        public String toString() {
            return "%" + this.name + (arg != null ? "[" + arg + "]" : "");
        }
    }
    
}
