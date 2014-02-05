package fr.lip6.move.verchor.base;

import java.util.HashMap;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface Choreography {

    public ChoreographySpecification getSpecification();
    public HashMap<PeerId, Peer> getPeers();
    public Peer getPeer(PeerId peerId);

}
