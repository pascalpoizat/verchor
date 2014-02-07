package cif;

import base.*;
import models.base.IllegalModelException;
import models.choreography.cif.CifModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviour implements Behaviour {

    private HashMap<StateId, CifBehaviourState> states;
    private CifBehaviourState initialState; // should be in states
    private HashMap<StateId, CifBehaviourState> finalStates; // all should be in states

    public CifBehaviour(CifModel model) {

    }

    @Override
    public HashMap<MessageId, Message> getAlphabet() {
        // TODO
        return null;
    }

    @Override
    public State getInitialState() throws IllegalModelException {
        return initialState;
    }

    @Override
    public HashMap<StateId, CifBehaviourState> getFinalStates() {
        return finalStates;
    }

    @Override
    public boolean isInitial(StateId stateId) {
        return false;
    }

    @Override
    public boolean isFinal(StateId stateId) {
        // TODO
        return false;
    }

    @Override
    public List<Transition> outgoingTransitions(StateId stateId) {
        // TODO
        return null;
    }
}
