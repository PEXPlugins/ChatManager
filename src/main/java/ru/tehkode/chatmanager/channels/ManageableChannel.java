package ru.tehkode.chatmanager.channels;

import ru.tehkode.chatmanager.Speaker;

public interface ManageableChannel extends Channel{

    public Iterable<Speaker> getSubscribers();

    public void addSubscriber(Speaker subscriber);

    public void removeSubscriber(Speaker player);
    


    public Speaker getOwner();

    public void setOwner(Speaker owner);

    public boolean hasOwner();

}
