package cif;

import base.*;
import models.base.IllegalModelException;
import models.choreography.cif.CifModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviour implements Behaviour {

    private HashMap<StateId, CifBehaviourState> states;
    private CifBehaviourState initialState; // should be in states
    private HashMap<StateId, CifBehaviourState> finalStates; // all should be in states

    public CifBehaviour(CifModel model) {
        // TODO
    }

    @Override
    public Set<Message> getAlphabet() {
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
        return (initialState.equals(stateId));
    }

    @Override
    public boolean isFinal(StateId stateId) {
        return finalStates.containsKey(stateId);
    }

    @Override
    public List<Transition> outgoingTransitions(StateId stateId) {
        State state = states.get(stateId);
        if (state == null) {
            return null;
        }
        List<Transition> rtr = new ArrayList<Transition>();

        return rtr;
     }
}
