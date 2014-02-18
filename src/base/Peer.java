package base;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface Peer {

    public PeerId getId();

    public void setBehaviour(Behaviour behaviour);

    public Behaviour getBehaviour();

    @Override
    public boolean equals(Object o);

    @Override
    public int hashCode();

}
