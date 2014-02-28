package cif;

import base.Behaviour;
import base.PeerId;
import base.Peer;

/**
 * Created by pascalpoizat on 18/02/2014.
 */
public class CifPeer implements Peer {
    // adapted
    private models.choreography.cif.generated.Peer peer;
    // own data
    private PeerId id;
    private Behaviour behaviour;

    public CifPeer(models.choreography.cif.generated.Peer peer) {
        this.peer = peer;
        this.id = new PeerId(peer.getPeerID());
        this.behaviour = null;
    }

    public PeerId getId() {
        return id;
    }

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifPeer cifPeer = (CifPeer) o;

        if (!id.equals(cifPeer.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
