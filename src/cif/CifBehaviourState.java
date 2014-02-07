package cif;

import models.choreography.cif.generated.BaseState;
import base.State;

/**
 * Created by pascalpoizat on 06/02/2014.
 */
public class CifBehaviourState implements State {

    private BaseState state;

    public CifBehaviourState() {
        state = null;
    }

    public CifBehaviourState(BaseState state) {
        this.state = state;
    }
}
