package fr.lip6.move.verchor.base;

import java.util.HashMap;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public abstract class ChoreographySpecification {

    private HashMap<PeerId, Peer> peers;
    private HashMap<MessageId, Message> messages;
    private Behaviour behaviour;

    public HashMap<PeerId, Peer> getPeers() {
        return peers;
    }

    public HashMap<MessageId, Message> getMessages() {
        return messages;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public abstract boolean isRealizable(EquivalenceChecker equivalenceChecker, Composer composer);

    public abstract HashMap<PeerId, Peer> project();

}
