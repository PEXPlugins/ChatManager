package ru.tehkode.chatmanager.format;

import ru.tehkode.chatmanager.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleMessageFormat implements MessageFormat {
    protected static Pattern placeholderPattern = Pattern.compile("\\%([a-zA-Z_0-9]+)(?:\\[([^\\]]+)\\])?");
    
    protected final Object[] compiledFormat;

    protected SimpleMessageFormat(Object[] compliedFormat) {
        this.compiledFormat = compliedFormat;
    }
    
    @Override
    public String format(Message message, PlaceholderCollection placeholders) {
        StringBuilder builder = new StringBuilder();
        
        for(Object piece : compiledFormat) {
            if (piece instanceof PlaceholderBinding) {
                piece = ((PlaceholderBinding)piece).call(message, placeholders);
            }

            builder.append(piece.toString());
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Object obj : this.compiledFormat) {
            if (obj instanceof PlaceholderBinding) {
                obj = ((PlaceholderBinding)obj).toString();
            }

            builder.append(obj.toString());
        }

        return builder.toString();
    }

    public static SimpleMessageFormat compile(String format) {

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
    
    protected static class PlaceholderBinding {

        protected final String name;
        protected final String arg;

        public PlaceholderBinding(final String name, final String arg) {
            this.name = name;
            this.arg = arg; 
        } 
        
        public String call(Message message, PlaceholderCollection placeholder) {
            String result = placeholder.run(name, arg, message);
            return result != null ? result : "";
        }

        @Override
        public String toString() {
            return "%" + this.name + (arg != null ? "[" + arg + "]" : "");
        }
    }
    
}
