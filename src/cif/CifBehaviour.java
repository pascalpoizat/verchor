package cif;

import base.*;
import models.base.IllegalModelException;
import models.choreography.cif.CifModel;
import models.choreography.cif.generated.StateMachine;
import models.choreography.cif.generated.BaseState;
import models.choreography.cif.generated.FinalState;
import base.Message;

import java.util.*;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviour implements Behaviour {

    // own data
    private HashMap<StateId, CifBehaviourState> states;
    private CifBehaviourState initialState; // should be in states
    private HashMap<StateId, CifBehaviourState> finalStates; // all should be in states
    private Set<AlphabetElement> alphabet;
    // wrapped data
    private CifModel model;
    private StateMachine stateMachine;

    public CifBehaviour(CifModel model, HashMap<MessageId, Message> messages, HashMap<PeerId, Peer> peers) {
        try {
            // try to get the model and state machine
            this.model = model;
            stateMachine = model.getStateMachine();
            // compute the state machine (states, initial state, final states)
            buildStateMachine();
            // compute the alphabet
            buildAlphabet(messages, peers);
        } catch (IllegalModelException e) {
            stateMachine = null;
            states = new HashMap<StateId, CifBehaviourState>();
            initialState = null;
            finalStates = new HashMap<StateId, CifBehaviourState>();
            alphabet = new HashSet<AlphabetElement>();
        }
    }

    private void buildAlphabet(HashMap<MessageId, Message> messages, HashMap<PeerId, Peer> peers) throws IllegalModelException {
        // in CIF, the set of all m[p,P] in the interactions
        // where:
        // m is a message (in the CIF model messages),
        // p the initiating peer (in the CIF model peers),
        // P are the receiving peers (in the CIF model peers)
        CifMessage cifMessage;
        CifPeer peer;
        Set<Peer> receivers;
        CifPeer receiver;
        CifAlphabetElement cifAlphabetElement;
        alphabet = new HashSet<AlphabetElement>();
        for (Object o : model.getAlphabet().getMessageOrAction()) {
            if (o instanceof models.choreography.cif.generated.Message) {
                models.choreography.cif.generated.Message m = (models.choreography.cif.generated.Message) o;
                cifMessage = (CifMessage) messages.get(new MessageId(m.getMsgID()));
                peer = (CifPeer) peers.get(new PeerId(m.getSender()));
                receivers = new HashSet<Peer>();
                // for the time being, only 1 receiver in CIF
                receiver = (CifPeer) peers.get(new PeerId(m.getReceiver()));
                receivers.add(receiver);
                cifAlphabetElement = new CifAlphabetElement(cifMessage, peer, receivers);
                alphabet.add(cifAlphabetElement);
            }
        }
    }

    private void buildStateMachine() {
        CifBehaviourState state;
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
    public Set<AlphabetElement> getAlphabet() {
        return alphabet;
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
        return (initialState.getId().equals(stateId));
    }

    @Override
    public boolean isFinal(StateId stateId) {
        return finalStates.containsKey(stateId);
    }

    @Override
    public List<Transition> outgoingTransitions(StateId stateId) {
        CifBehaviourState state = states.get(stateId);
        if (state == null) {
            return null;
        }
        return state.getOutgoingTransitions();
    }
}
