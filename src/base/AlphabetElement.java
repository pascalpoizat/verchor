package base;

import java.util.Set;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface AlphabetElement {
    // an alphabet element encodes a choreography alphabet element, ie a triple m[x,P]
    // where m is the message, x is the initiating peer, and P are the other participants
    // important: x in P denotes that x is the initiating peer AND a receiving peer, else x not in P

    public Message getMessage(); // get m from m[x,P]

    public Peer getInitiatingPeer(); // get x from m[x,P]

    public Set<Peer> getParticipants(); // get P from m[x,P]

    @Override
    public boolean equals(Object o);

    @Override
    public int hashCode();
}
