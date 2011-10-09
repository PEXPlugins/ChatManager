package ru.tehkode.chatmanager.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.event.player.PlayerChatEvent;
import ru.tehkode.chatmanager.filters.MessageFilter;
import ru.tehkode.chatmanager.placeholders.Placeholder;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author t3hk0d3
 */
public abstract class ChatChannel {

	protected Pattern placeholderPattern = Pattern.compile("%([a-z0-9\\_\\-]+)(?:\\(([^\\)]+)\\))?");
	protected String messageFormat = "<%prefix%player%suffix> %message";
	protected Map<String, Placeholder> placeholders = new HashMap<String, Placeholder>();
	protected Set<MessageFilter> filters = new HashSet<MessageFilter>();

	public void processMessage(PlayerChatEvent event) {
		this.updateRecipients(event);
		this.replacePlaceholders(event);
		this.filterMessage(event);
	}

	public Set<MessageFilter> getFilters() {
		return filters;
	}

	public Map<String, Placeholder> getPlaceholders() {
		return placeholders;
	}

	public void addPlaceholder(Placeholder placeholder, String pattern) {
		this.placeholders.put(pattern, placeholder);
	}

	public void addPlaceholder(Placeholder placeholder) {
		for (String pattern : placeholder.getPatterns()) {
			this.addPlaceholder(placeholder, pattern);
		}
	}

	public void addFilter(MessageFilter filter) {
		this.filters.add(filter);
	}

	public String getMessageFormat(PlayerChatEvent event) {
		PermissionUser user = PermissionsEx.getPermissionManager().getUser(event.getPlayer());

		return user.getOption("message-format", event.getPlayer().getWorld().getName(), this.messageFormat);
	}

	public void filterMessage(PlayerChatEvent event) {
		String message = event.getMessage();
		for (MessageFilter filter : this.getFilters()) {
			message = filter.filter(message);
		}
		event.setMessage(message);
	}

	public void replacePlaceholders(PlayerChatEvent event) {
		String format = this.getMessageFormat(event).replace("%message", "%2$s").replace("%displayname", "%1$s");

		Matcher match = placeholderPattern.matcher(format);
		while (match.find()) {
			Placeholder placeholder = this.getPlaceholders().get(match.group(1));

			if (placeholder == null) { // placeholder not registered
				continue;
			}
			
			placeholder.start(event); // todo make proper initialization

			format = format.replace(match.group(0), placeholder.getValue(match.group(1), match.group(2), event).replace("%", "%%"));
		}
		
		event.setFormat(this.colorize(format));
	}

	protected String colorize(String string) {
		if (string == null) {
			return "";
		}

		return string.replaceAll("&([a-z0-9])", "\u00A7$1");
	}

	protected void updateRecipients(PlayerChatEvent event) {
		// do nothing
	}

	public boolean isApplicable(PlayerChatEvent message) {
		return true;
	}
}
