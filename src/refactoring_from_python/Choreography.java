/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * verchor
 * Copyright (C) 2012-2014  Alexandre Dumont, Gwen Salaun, Matthias Gudemann for the Python version
 * Copyright (C) 2014 Pascal Poizat (@pascalpoizat) for the Python->Java refactoring
 * emails: pascal.poizat@lip6.fr
 */

package refactoring_from_python;

import models.base.AbstractModel;
import models.base.IllegalModelException;
import models.base.IllegalResourceException;
import models.choreography.cif.generated.*;
import refactoring_from_python.statemachine.*;
import refactoring_from_python.statemachine.AllJoinState;
import refactoring_from_python.statemachine.AllSelectState;
import refactoring_from_python.statemachine.ChoiceState;
import refactoring_from_python.statemachine.FinalState;
import refactoring_from_python.statemachine.InitialState;
import refactoring_from_python.statemachine.InteractionState;
import refactoring_from_python.statemachine.SimpleJoinState;
import refactoring_from_python.statemachine.SubsetJoinState;
import refactoring_from_python.statemachine.SubsetSelectState;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Choreography extends AbstractModel {

    // data from the refactoring
    private String name;
    private Set<State> states;
    private State initialState;
    private List<String> peers;     // list without doubles TODO check if a set would be ok
    private List<AlphabetElement> alphabet;  // list without doubles TODO check if a set would be ok

    // wrapped data
    private models.choreography.cif.generated.Choreography raw;
    private Map<String, Message> raw_messages;
    private Map<String, Peer> raw_peers;

    public Choreography() {
        this("");
    }

    public Choreography(String name) {
        super();
        this.name = name;
        this.states = new HashSet<>();
        this.initialState = null;
        this.peers = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.raw = null;
        this.raw_messages = null;
        this.raw_peers = null;
    }


    public String getName() {
        return name;
    }

    public Set<State> getStates() {
        return states;
    }

    public State getInitialState() {
        return initialState;
    }

    /**
     * returns the peers of the choreography
     * derived attribute (from states' getPeers())
     *
     * @return
     */
    public List<String> getPeers() {
        return peers;
    }

    /**
     * returns the alphabet of the choreography
     * derived attribute (from states' getAlphabet())
     *
     * @return
     */
    public List<AlphabetElement> getAlphabet() {
        return alphabet;
    }

    /**
     * pure refactoring of the python method - due to the difference between the CIF JAXB-generated API and the python CIF API
     * hence, we have 3 related classes:
     * - models.choreography.cif.generated.Choreography (JAXB-generated API)
     * - models.choreography.cif.CifModel (FMT API, with its reader from .cif file: CifCifReader)
     * - this class, refactoring_from_python.Choreography (Python-like API, with its reader from .cif file: CifChoreographyReader)
     * TODO this method will be useless in the future, use the FMT API
     * 1- AbstractModel c = new Choreography();
     * 2- c.setResource(new File
     *
     * @param filename
     */
    public void buildChoreoFromFile(String filename) throws IllegalResourceException, IOException, IllegalModelException {
        cleanUp();
        setResource(new File(filename));
        modelFromFile(new CifChoreographyReader());
    }

    public void buildChoreoFromRawModel(models.choreography.cif.generated.Choreography rawCifModel) throws IllegalModelException {
        raw = rawCifModel;
        setupRawPeers();
        setupRawMessages();
        //
        name = rawCifModel.getChoreoID();
        StateMachine stateMachine = rawCifModel.getStateMachine();
        // variables used in step 1 to keep information used in step 2
        Map<String, State> stateMap = new HashMap<>();          // state id -> State
        Map<String, List<String>> successors = new HashMap<>(); // state id -> state ids of the successors
        // step 1 : state creation
        buildStateFromRawModel(stateMachine.getInitial(), stateMap, successors);
        for (BaseState finalState : stateMachine.getFinal()) {
            buildStateFromRawModel(finalState, stateMap, successors);
        }
        for (BaseState intermediaryState : stateMachine.getInteractionOrInternalActionOrSubsetJoin()) {
            buildStateFromRawModel(intermediaryState, stateMap, successors);
        }
        // step 2 : relation with the successors
        for (State state : states) {
            for (String successorId : successors.get(state.getId())) {
                state.addSuccessor(stateMap.get(successorId));
            }
        }
        // step 3 : set up derived attributes
        computeAlphabet();
    }

    @Override
    public void cleanUp() {
        this.name = "";
        this.states = new HashSet<>();
        this.initialState = null;
        this.peers = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.raw = null;
        this.raw_messages = null;
        this.raw_peers = null;
        super.cleanUp();
    }

    /**
     * sets up the declared peers from the raw model
     */
    private void setupRawPeers() {
        raw_peers = new HashMap<>();
        raw.getParticipants().getPeer().stream()
                .forEach(peer -> {
                    raw_peers.put(peer.getPeerID(), peer);
                });
    }

    /**
     * sets up the declared messages from the raw model
     */
    private void setupRawMessages() {
        raw_messages = new HashMap<>();
        raw.getAlphabet().getMessageOrAction().stream()
                .filter(message -> message instanceof Message)
                .forEach(message -> {
                    raw_messages.put(((Message) message).getMsgID(), (Message) message);
                });
    }

    /**
     * add a state to the choreography
     * TODO bad pattern
     *
     * @param state the state to add
     * @return true if the state was not already present
     */
    private boolean addState(State state) throws IllegalModelException {
        boolean isAdded = states.add(state);
        if (isAdded) { // the state was not already present and has been added
            state.setChoreography(this);
            if (state instanceof InitialState) { // if this is an initial state, try to set it
                if (initialState == null) { // ok if there is no initial state yet
                    initialState = state;
                } else { // problem if there is already an initial state
                    throw new IllegalModelException(String.format("Too many initial states: %s, %s.", getInitialState(), state));
                }
            }
        }
        return isAdded;
    }

    /**
     * computes the derived attribute alphabet
     * TODO could use the stream API, trick used to ensure lists without doubles, simplify if sets can be used
     */
    private void computeAlphabet() {
        List<AlphabetElement> newAlphabet = new ArrayList<>();
        for (State state : states) {
            newAlphabet.addAll(state.getAlphabet());
        }
        alphabet.clear();
        alphabet = refactoring_from_python.verification.helpers.Collections.removeDoubles(newAlphabet, newAlphabet);
    }

    /**
     * TODO bad pattern, due to the design of CIF + CIF-Python, change it
     * precondition setup() has been called (raw_peers and raw_messages have been computed)
     *
     * @param rawState
     * @param stateMap
     * @param successors
     */
    private void buildStateFromRawModel(BaseState rawState, Map<String, State> stateMap, Map<String, List<String>> successors) throws IllegalModelException {
        State state;
        String id = rawState.getStateID();
        // compute state
        if (rawState instanceof models.choreography.cif.generated.FinalState) {
            state = new FinalState(id);
        } else if (rawState instanceof models.choreography.cif.generated.InitialState) {
            state = new InitialState(id);
        } else if (rawState instanceof models.choreography.cif.generated.InteractionState) {
            models.choreography.cif.generated.InteractionState interactionState = (models.choreography.cif.generated.InteractionState) rawState;
            String msgID = interactionState.getMsgID();
            if (raw_messages.containsKey(msgID)) {
                Message raw_message = raw_messages.get(msgID);
                List<String> message_peers = new ArrayList<>();
                message_peers.add(raw_message.getSender());
                message_peers.add(raw_message.getReceiver());
                if (!message_peers.stream().allMatch(peer -> raw_peers.containsKey(peer))) {
                    throw new IllegalModelException(String.format("Undeclared peer in message %s.", msgID));
                }
                String message_initiator = raw_message.getSender();
                List<MessageFlow> message_flows = new ArrayList<>();
                message_flows.add(new MessageFlow(raw_message.getMessageContent()));
                state = new InteractionState(id, message_peers, message_initiator, message_flows);
            } else {
                throw new IllegalModelException(String.format("Undeclared message %s in interaction state %s", msgID, id));
            }
        } else if (rawState instanceof models.choreography.cif.generated.AllJoinState) {
            state = new AllJoinState(id);
        } else if (rawState instanceof models.choreography.cif.generated.SimpleJoinState) {
            state = new SimpleJoinState(id);
        } else if (rawState instanceof models.choreography.cif.generated.SubsetJoinState) {
            state = new SubsetJoinState(id);
        } else if (rawState instanceof models.choreography.cif.generated.AllSelectState) {
            state = new AllSelectState(id);
        } else if (rawState instanceof models.choreography.cif.generated.ChoiceState) {
            state = new ChoiceState(id);
        } else if (rawState instanceof models.choreography.cif.generated.SubsetSelectState) {
            state = new SubsetSelectState(id);
        } else {
            throw new IllegalModelException(String.format("Element %s is not supported yet.", rawState.getClass().toString()));
        }
        // set up successors
        if (rawState instanceof models.choreography.cif.generated.FinalState) {
            successors.put(id, new ArrayList<>());
        } else if (rawState instanceof OneSuccState) {
            successors.put(id, ((OneSuccState) rawState).getSuccessors());
        } else if (rawState instanceof SeveralSuccState) {
            successors.put(id, ((SeveralSuccState) rawState).getSuccessors());
        }
        // set up state
        stateMap.put(id, state);
        boolean addResult = addState(state);
        if (!addResult) {
            throw new IllegalModelException(String.format("Duplicate state id %s.", id));
        }
    }

}
