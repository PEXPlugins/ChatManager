/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.filters;

import java.util.List;

/**
 *
 * @author t3hk0d3
 */
public class WordMessageFilter implements MessageFilter {

    protected List<String> words;
    protected String replacement;

    public WordMessageFilter(List<String> words, String replacement) {
        this.words = words;
        this.replacement = replacement;
        
        if(words == null){
            throw new IllegalArgumentException("Invalid list of words specified");
        }
    }

    @Override
    public String filter(String message) {
        String filteredMessage = message.toString();
        for (String badWord : words) {
            filteredMessage = filteredMessage.replace(badWord, this.replacement);
        }

        return filteredMessage;
    }
}
