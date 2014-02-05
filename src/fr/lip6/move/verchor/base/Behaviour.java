package fr.lip6.move.verchor.base;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface Behaviour {

    public State getInitialState();
    public HashMap<StateId, State> getFinalStates();
    public boolean isInitial(StateId stateId);
    public boolean isFinal(StateId stateId);
    public List<Transition> outgoingTransitions(StateId stateId);

}
