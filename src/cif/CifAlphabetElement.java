package cif;

import base.AlphabetElement;
import base.Message;
import base.Peer;
import models.base.IllegalModelException;

import java.util.Set;

/**
 * Created by pascalpoizat on 15/02/2014.
 */
public class CifAlphabetElement implements AlphabetElement {

    private Message message;
    private Peer initiator;
    private Set<Peer> receivers; // NEXT RELEASE : support more than 1 receiver (impacts on several methods)

    public CifAlphabetElement(Message message, Peer initiator, Set<Peer> receivers) throws IllegalModelException {
        if (receivers.size() != 1) {
            throw new IllegalModelException("Illegal alphabet element has not exactly one receiver.");
        }
        this.message = message;
        this.initiator = initiator;
        this.receivers = receivers;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public Peer getInitiatingPeer() {
        return initiator;
    }

    @Override
    public Set<Peer> getParticipants() {
        // does not include the initiator
        // always 1 participant
        return receivers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CifAlphabetElement that = (CifAlphabetElement) o;
        if (!initiator.equals(that.initiator)) return false;
        if (!message.equals(that.message)) return false;
        if (!receivers.equals(that.receivers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + initiator.hashCode();
        result = 31 * result + receivers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s_%s_%s", initiator.toString(), receivers.iterator().next().toString(), message.toString());
    }

    @Override
    public int compareTo(AlphabetElement o) {
        return this.toString().compareTo(o.toString());
    }
}
