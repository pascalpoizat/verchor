package fr.lip6.move.verchor.base;

import java.util.HashMap;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface ChoreographySpecification {

    public HashMap<PeerId, Peer> getPeers();
    public HashMap<MessageId, Message> getMessages();
    public Behaviour getBehaviour();

}
