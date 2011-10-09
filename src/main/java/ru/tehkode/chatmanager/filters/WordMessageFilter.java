/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tehkode.chatmanager.filters;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author t3hk0d3
 */
public class WordMessageFilter extends RegexpMessageFilter {

    public WordMessageFilter(List<String> words, String replacement) {
		super(prepareWordList(words), replacement);
    }

    @Override
    public String filter(String message) {
        return this.pattern.matcher(message).replaceAll(replacement);
    }
	
	protected static String prepareWordList(List<String> words){
		if(words == null){
            throw new IllegalArgumentException("Invalid list of words specified");
        }
		
		String regexp = "";
		for(String word : words){
			regexp += "|" + Pattern.quote(word);
		}
		return "(" + regexp.substring(1) + ")";
	}
}
