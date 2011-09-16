package ru.tehkode.chatmanager.filters;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author t3hk0d3
 */
public interface MessageFilter {

    public abstract String filter(String message);
}
