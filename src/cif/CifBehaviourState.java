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
 * Copyright (C) 2014 Pascal Poizat (@pascalpoizat)
 * emails: pascal.poizat@lip6.fr
 */

package cif;

import base.*;
import models.choreography.cif.generated.BaseState;
import models.choreography.cif.generated.InteractionState;
import models.choreography.cif.generated.OneSuccState;
import models.choreography.cif.generated.SeveralSuccState;

import java.util.ArrayList;
import java.util.List;

public class CifBehaviourState implements State {

    // adapted
    private BaseState state;
    // own data
    private StateId id;
    private List<StateId> successorIds;
    private List<Transition> incomingTransitions;
    private List<Transition> outgoingTransitions;

    public CifBehaviourState(BaseState state) {
        this.state = state;
        this.id = new StateId(state.getStateID());
        initializeSuccessorIds();
        this.incomingTransitions = new ArrayList<>();
        this.outgoingTransitions = new ArrayList<>();
    }

    @Override
    public List<Transition> getIncomingTransitions() {
        return incomingTransitions;
    }

    @Override
    public List<Transition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    public StateId getId() {
        return id;
    }

    public List<StateId> getSuccessorIds() {
        return successorIds;
    }

    private void initializeSuccessorIds() {
        successorIds = new ArrayList<>();
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

    public MessageId getMessageId() {
        // method used to get the message id (for interaction states)
        if(state instanceof InteractionState) {
            return new MessageId(((InteractionState)state).getMsgID());
        }
        else {
            return null;
        }
    }

    public void addInTransition(CifBehaviourTransition transition) {
        incomingTransitions.add(transition);
    }

    public void addOutTransition(CifBehaviourTransition transition) {
        outgoingTransitions.add(transition);
    }
}
