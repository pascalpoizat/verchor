package cif;

import base.StateId;
import models.choreography.cif.generated.BaseState;
import base.State;
import models.choreography.cif.generated.OneSuccState;
import models.choreography.cif.generated.SeveralSuccState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviourState implements State {

    private BaseState state;
    private StateId id;
    private List<StateId> successorIds;
    private List<CifBehaviourTransition> incomingTransitions;
    private List<CifBehaviourTransition> outgoingTransitions;

    public CifBehaviourState(BaseState state) {
        this.state = state;
        this.id = new StateId(state.getStateID());
        initializeSuccessorIds();
        this.incomingTransitions = new ArrayList<CifBehaviourTransition>();
        this.outgoingTransitions = new ArrayList<CifBehaviourTransition>();
    }

    public StateId getId() {
        return id;
    }

    public List<StateId> getSuccessorIds() {
        return successorIds;
    }

    private void initializeSuccessorIds() {
        successorIds = new ArrayList<StateId>();
        if (getStateClass().equals(OneSuccState.class)) {
            OneSuccState source = (OneSuccState) state;
            successorIds.add(new StateId(source.getSuccessors().get(0)));
        } else if (getStateClass().equals(SeveralSuccState.class)) {
            SeveralSuccState source = (SeveralSuccState) state;
            for (String targetId : source.getSuccessors()) {
                successorIds.add(new StateId(targetId));
            }
        }
    }

    private Class getStateClass() {
        // method used to get the kind of state (with some abstraction though) the object is wrapping
        if (state instanceof OneSuccState) {
            return OneSuccState.class;
        } else if (state instanceof SeveralSuccState) {
            return SeveralSuccState.class;
        } else {
            return state.getClass();
        }
    }

    public void addInTransition(CifBehaviourTransition transition) {
        incomingTransitions.add(transition);
    }

    public void addOutTransition(CifBehaviourTransition transition) {
        outgoingTransitions.add(transition);
    }
}
