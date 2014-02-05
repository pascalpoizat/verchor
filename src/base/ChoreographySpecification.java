package base;

import java.util.List;
import java.util.HashMap;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public abstract class ChoreographySpecification {

    private List<PeerId> peers;
    private HashMap<MessageId, Message> messages;
    private Behaviour behaviour;

    public List<PeerId> getPeers() {
        return peers;
    }

    public HashMap<MessageId, Message> getMessages() {
        return messages;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public abstract boolean isRealizable(); // checks whether the choreography specification is realizable or not

    public abstract boolean conformsWith(HashMap<PeerId, Peer> peers); // checks whether a set of peers is conform to the choreography specification

    public abstract HashMap<PeerId, Peer> project(); // computes a set of peers by projecting the choreography specification

}
