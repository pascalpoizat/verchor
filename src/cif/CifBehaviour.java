package cif;

import base.*;
import models.base.IllegalModelException;
import models.choreography.cif.CifModel;
import models.choreography.cif.generated.BaseState;
import models.choreography.cif.generated.FinalState;
import models.choreography.cif.generated.StateMachine;

import java.util.*;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviour implements Behaviour {

    // own data
    private HashMap<StateId, CifBehaviourState> states;
    private CifBehaviourState initialState; // should be in states
    private HashMap<StateId, CifBehaviourState> finalStates; // all should be in states
    // wrapped data
    private StateMachine stateMachine;

    public CifBehaviour(CifModel model) {
        CifBehaviourState state;
        stateMachine = null;
        //
        try {
            // try to get the model state machine
            stateMachine = model.getStateMachine();
            // buid adapters for the states
            buildStateAdapters();
            // set initial state
            initialState = states.get(new StateId(stateMachine.getInitial().getStateID()));
            // set final states
            finalStates = new HashMap<StateId, CifBehaviourState>();
            for (FinalState finalState : stateMachine.getFinal()) {
                state = states.get(new StateId(finalState.getStateID()));
                finalStates.put(state.getId(), state);
            }
            // build the transition structure
            buildTransitionStructure();
        } catch (IllegalModelException e) {
        }
    }

    private void buildTransitionStructure() {
        CifBehaviourState source;
        CifBehaviourState target;
        CifBehaviourTransition transition;
        // we suppose that the CIF model is fully connected (all states are reachable from the initial state)
        List<StateId> visitedStates = new ArrayList<StateId>();
        Queue<StateId> statesStillToVisit = new LinkedList<StateId>();
        StateId stateId;
        statesStillToVisit.add(initialState.getId());
        while (!statesStillToVisit.isEmpty()) {
            stateId = statesStillToVisit.poll();
            source = states.get(stateId);
            visitedStates.add(stateId);
            for (StateId targetId : source.getSuccessorIds()) {
                target = states.get(targetId);
                transition = new CifBehaviourTransition(source, target);
                source.addOutTransition(transition);
                target.addInTransition(transition);
                if (!visitedStates.contains(targetId) && !statesStillToVisit.contains(targetId)) {
                    statesStillToVisit.add(targetId);
                }
            }
        }
    }

    private void buildStateAdapters() {
        CifBehaviourState state;
        states = new HashMap<StateId, CifBehaviourState>();
        // for the time being, CIF generated classes generate separately initial, final, and all other states
        // we then have to treat the 3 separately
        // 1- initial state
        state = new CifBehaviourState(stateMachine.getInitial());
        states.put(state.getId(), state);
        // 2- final states
        for (BaseState s : stateMachine.getFinal()) {
            state = new CifBehaviourState(s);
            states.put(state.getId(), state);
        }
        // 3- all other states
        for (BaseState s : stateMachine.getInteractionOrInternalActionOrSubsetJoin()) {
            state = new CifBehaviourState(s);
            states.put(state.getId(), state);
        }
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
