package ru.tehkode.chatmanager.channels;

public abstract class AbstractChannel implements Channel {

    protected String name;

    public AbstractChannel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        // This should be overriden
        return this.name; // .capitalize();
    }

}
