package base;

import java.util.List;
import java.util.HashMap;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public abstract class ChoreographySpecification {

    private List<PeerId> peers;
    private HashMap<MessageId, AlphabetElement> messages;
    private Behaviour behaviour;
    private boolean verbose;


    public ChoreographySpecification() {
        verbose = false;
    }

    public List<PeerId> getPeers() {
        return peers;
    }

    public HashMap<MessageId, AlphabetElement> getMessages() {
        return messages;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    protected abstract boolean isRealizable(); // checks whether the choreography specification is realizable or not

    protected abstract boolean isSynchronizable(); // checkes whether the choreography specification is synchronizable or not

    protected abstract boolean conformsWith(HashMap<PeerId, Peer> peers); // checks whether a set of peers is conform to the choreography specification

    protected abstract HashMap<PeerId, Peer> project(); // computes a set of peers by projecting the choreography specification

    // methods below should probably move to Choreography

    public void setVerbose(boolean verbose) {



        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void message(String msg) {
        if (this.verbose) {
            System.out.println("" + msg);
        }
    }

    public void error(String msg) {
        System.out.println("ERROR: " + msg);
    }

    public void warning(String msg) {
        System.out.println("WARNING: " + msg);
    }

    public abstract void about();

}
