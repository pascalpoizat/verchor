package base;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class Choreography {

    private ChoreographySpecification choreographySpecification;
    private HashMap<PeerId, Peer> peers;

    public Choreography() {
        choreographySpecification = null;
        peers = new HashMap<PeerId, Peer>();
    }

    public Choreography(ChoreographySpecification choreographySpecification) {
        this.choreographySpecification = choreographySpecification;
        peers = new HashMap<PeerId, Peer>();
    }

    public Choreography(ChoreographySpecification choreographySpecification, HashMap<PeerId, Peer> peers) {
        this.choreographySpecification = choreographySpecification;
        this.peers = peers;
    }

    public ChoreographySpecification getSpecification() {
        return choreographySpecification;
    }

    public HashMap<PeerId, Peer> getPeers() {
        return peers;
    }

    public Peer getPeer(PeerId peerId) throws UnknownPeerId {
        if (peers.containsKey(peerId)) {
            return peers.get(peerId);
        } else {
            throw new UnknownPeerId(UnknownPeerId.MESSAGE + "(" + peerId + ")");
        }
    }

    public boolean isRealizable() {  // checks whether the choreography specification is realizable or not
        return choreographySpecification.isRealizable();
    }

    public boolean isConform() { // checks whether the set of peers conform to the choreography specification
        return choreographySpecification.conformsWith(peers);
    }

    public void project() { // computes the set of peers from the choreography specification (side-effect)
        peers = choreographySpecification.project();
    }

}
