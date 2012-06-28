package ru.tehkode.chatmanager.placeholders;

import ru.tehkode.chatmanager.Message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractPlaceholders implements PlaceholderCollection {

    protected Map<String, MethodPlaceholder> placeholders = new HashMap<String, MethodPlaceholder>();

    protected AbstractPlaceholders() {
        this.readPlaceholders();
    }

    private void readPlaceholders() {
        for (Method method : this.getClass().getMethods()) {
            if (!method.isAnnotationPresent(PlaceholderMethod.class)) {
                continue;
            }

            PlaceholderMethod ph = method.getAnnotation(PlaceholderMethod.class);

            placeholders.put(ph.value().toLowerCase(), new MethodPlaceholder(method));
        }
    }

    public String run(String name, String arg, Message message) {
        MethodPlaceholder placeholder = placeholders.get(name);
        return placeholder != null ? placeholder.run(name, arg, message) : null;
    }

    @Override
    public Placeholder getPlaceholder(String name) {
        return this.placeholders.get(name);
    }

    public Set<String> getPlaceholders() {
        return this.placeholders.keySet();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface PlaceholderMethod {
        String value();
    }

    private class MethodPlaceholder implements Placeholder {

        protected Method method;
        
        public MethodPlaceholder(Method method) {
            this.method = method;
        }

        @Override
        public String run(String name, String arg, Message message) {
            try {
                // Should be fast because of JIT
                Object result = method.invoke(AbstractPlaceholders.this, arg, message);

                if (result != null) {
                    return result.toString();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
