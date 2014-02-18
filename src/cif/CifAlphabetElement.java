package cif;

import base.AlphabetElement;
import base.Message;
import base.Peer;

import java.util.Set;

/**
 * Created by pascalpoizat on 15/02/2014.
 */
public class CifAlphabetElement implements AlphabetElement {

    private CifMessage message;
    private CifPeer initiator;
    private Set<Peer> receivers;

    public CifAlphabetElement(CifMessage message, CifPeer initiator, Set<Peer> receivers) {
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
}
