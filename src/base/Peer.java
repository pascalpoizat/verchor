package base;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class Peer {

    private Behaviour behaviour;

    public Peer() {
        this.behaviour = null;
    }

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }
}
