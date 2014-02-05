package base;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public abstract class Choreography {

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

    public boolean isRealizable(EquivalenceChecker equivalenceChecker, Composer composer) {
        return choreographySpecification.isRealizable(equivalenceChecker, composer);
    }

    public boolean isConform(EquivalenceChecker equivalenceChecker, Composer composer) {
        List<Behaviour> behaviourList = new ArrayList<Behaviour>();
        for (Peer peer : peers.values()) {
            behaviourList.add(peer.getBehaviour());
        }
        return equivalenceChecker.isEquivalent(choreographySpecification.getBehaviour(), composer.compose(behaviourList));
    }

    public void project() {
        peers = choreographySpecification.project();
    }

}
