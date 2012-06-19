package ru.tehkode.chatmanager.channels;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

public class RangedChannel extends GlobalChannel {

    protected int range = 100;

    public RangedChannel(int range) {
        this("ranged", range);
    }
    
    public RangedChannel(String name, int range) {
        super(name);

        this.range = range;
    }

    @Override
    public Set<Player> getSubscribers(Player sender) {
        World senderWorld = sender.getWorld();

        // squared range for faster distance computation
        double maxDistance = this.range * this.range;
        
        Set<Player> recipients = super.getSubscribers(sender);

        for (Player recipient : recipients) {
            if (senderWorld != recipient.getWorld() || sender.getLocation().distanceSquared(recipient.getLocation()) > maxDistance) {
                recipients.remove(recipient);
            }
        }

        return recipients;
    }
}
