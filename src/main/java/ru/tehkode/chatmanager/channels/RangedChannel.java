package ru.tehkode.chatmanager.channels;

import org.bukkit.Location;
import ru.tehkode.chatmanager.ChatManager;
import ru.tehkode.chatmanager.Message;
import ru.tehkode.chatmanager.Speaker;

public class RangedChannel extends AbstractChannel {

    protected int rangeSquared = 1000;

    public RangedChannel(ChatManager manager){
        this(manager, "ranged", 100);
    }

    public RangedChannel(ChatManager manager, int range) {
        this(manager, "ranged", range);
    }
    
    public RangedChannel(ChatManager manager, String name, int range) {
        super(manager, name);

        this.setRange(range);
    }

    public void setRange(int range) {
        this.rangeSquared = range * range;
    }

    public int getRange() {
        return (int)Math.sqrt(rangeSquared);
    }

    @Override
    public boolean isSubscriber(Speaker speaker) {
        return true;
    }

    @Override
    protected boolean applySender(Message message, Speaker receiver) {
        if (!receiver.isOnline()){
             return false;
        }

        Location target = receiver.getPlayer().getLocation();
        Location source = message.getSender().getPlayer().getLocation();

        return this.isSubscriber(receiver) && source.distanceSquared(target) <= rangeSquared;
    }

    @Override
    public String getSelector() {
        return "@";
    }
}
