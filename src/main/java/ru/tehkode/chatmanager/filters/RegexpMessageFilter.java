/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.filters;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author t3hk0d3
 */

public class RegexpMessageFilter implements MessageFilter {

    protected Pattern pattern = null;
    protected String replacement = "";

    public RegexpMessageFilter(String pattern, String replacement) {
        this.replacement = replacement;

        if (pattern == null) {
            throw new IllegalArgumentException("Invalid regular expression specified");
        }

        try {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regular expression specified: " + e.getMessage());
        }
    }

    @Override
    public String filter(String message) {
        return this.pattern.matcher(message).replaceAll(this.replacement);
    }
}
