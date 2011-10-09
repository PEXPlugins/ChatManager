/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.placeholders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.player.PlayerChatEvent;

/**
 *
 * @author t3hk0d3
 */
public abstract class PlaceholderCollection implements Placeholder {

	// this can be made adjustable
	protected Map<String, Placeholder> placeholders = new HashMap<String, Placeholder>();

	public PlaceholderCollection() {
		this.registerPlaceholders();
	}
	
	public Map<String, Placeholder> getPlaceholders(){
		return this.placeholders;
	}
	
	private void registerPlaceholders() {
		for (Method method : this.getClass().getMethods()) {
			if (!method.isAnnotationPresent(PlaceholderItem.class)) {
				continue;
			}

			PlaceholderItem annotation = method.getAnnotation(PlaceholderItem.class);

			if (annotation.value().isEmpty()) {
				continue;
			}

			this.placeholders.put(annotation.value(), this.createPlaceholder(annotation.value(), method));
		}
	}

	@Override
	public String[] getPatterns() {
		return this.placeholders.keySet().toArray(new String[0]);
	}

	@Override
	public String getValue(String pattern, String value, PlayerChatEvent event) {
		Placeholder placeholder = this.placeholders.get(pattern);		
		
		if(placeholder == null){
			return "";
		}
		
		return placeholder.getValue(pattern, value, event);
	}

	@Override
	public void start(PlayerChatEvent event) {
		// do nothing
	}

	protected Placeholder createPlaceholder(final String pattern, final Method method) {
		Class<?>[] params = method.getParameterTypes();

		final PlaceholderCollection collection = this;

		return new Placeholder() {

			@Override
			public void start(PlayerChatEvent event) {
				collection.start(event);
			}			
			
			@Override
			public String[] getPatterns() {
				return new String[] { pattern };
			}			
			
			@Override
			public String getValue(String pattern, String arg, PlayerChatEvent message) {
				try {
					return (String) method.invoke(collection, pattern, arg, message);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				// return empty string
				return "";
			}
		};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PlaceholderItem {

		String value();
	}
}
