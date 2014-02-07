package base;

import models.base.IllegalModelException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface Behaviour {

    public HashMap<MessageId, Message> getAlphabet();

    public State getInitialState() throws IllegalModelException;

    public HashMap<StateId, ? extends State> getFinalStates();

    public boolean isInitial(StateId stateId);

    public boolean isFinal(StateId stateId);

    public List<Transition> outgoingTransitions(StateId stateId);

}
