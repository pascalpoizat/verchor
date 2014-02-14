package cif;

import base.StateId;
import models.choreography.cif.generated.BaseState;
import base.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviourState implements State {

    private BaseState state;
    private StateId id;
    private List<CifBehaviourTransition> incomingTransitions;
    private List<CifBehaviourTransition> outgoingTransitions;

    public CifBehaviourState(BaseState state) {
        this.state = state;
        this.id = new StateId(state.getStateID());
        this.incomingTransitions = new ArrayList<CifBehaviourTransition>();
        this.outgoingTransitions = new ArrayList<CifBehaviourTransition>();
    }

    public StateId getId() {
        return id;
    }
}
