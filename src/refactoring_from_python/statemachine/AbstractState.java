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

package refactoring_from_python.statemachine;

import refactoring_from_python.AlphabetElement;
import refactoring_from_python.Choreography;
import refactoring_from_python.verification.helpers.Couple;
import refactoring_from_python.MessageFlow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractState implements State {
    private String id;
    private Choreography choreography;  // link to the choreography
    private boolean checked;            // to mark visited states TODO should not be an attribute (use Visitor ?)
    private String space;               // offset to print tree TODO should not be an attribute (use Visitor ?)
    private Set<State> predecessors;   // predecessors in state machine
    private List<State> successors;     // successors in state machine // list without doubles TODO check if a set would be ok

    public AbstractState(String id) {
        this(id, new HashSet<>());
    }

    public AbstractState(String id, Set<State> successors) {
        this.id = id;
        this.checked = false;
        this.space = " ";
        this.predecessors = new HashSet<>();
        this.successors = new ArrayList<>();
        successors.forEach(this::addSuccessor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return !(id != null ? !id.equals(state.getId()) : state.getId() != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String getId() {
        return id; // TODO replace bad characters (eg " ") and bad ids (eg "exit") wrt LNT/SVL
    }

    @Override
    public Choreography getChoreography() {
        return choreography;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public String getSpace() {
        return space;
    }

    @Override
    public Set<State> getPredecessors() {
        return predecessors;
    }

    @Override
    public List<State> getSuccessors() {
        return successors;
    }

    @Override
    public List<String> getPeers() {
        return new ArrayList<>();
    }

    @Override
    public List<AlphabetElement> getAlphabet() {
        return new ArrayList<>();
    }

    @Override
    public Set<State> getSyncSet() {
        return new HashSet<>();
    }

    @Override
    public void setChoreography(Choreography choreography) {
        this.choreography = choreography;
    }

    @Override
    public boolean addPredecessor(State predecessor) {
        boolean isAdded = false;
        if (!getPredecessors().contains(predecessor)) {
            predecessors.add(predecessor);
            isAdded = true;
            predecessor.addSuccessor(this);
        }
        return isAdded;
    }

    @Override
    public boolean addSuccessor(State successor) {
        boolean isAdded = false;
        if (!getSuccessors().contains(successor)) {
            successors.add(successor);
            isAdded = true;
            successor.addPredecessor(this);
        }
        return isAdded;
    }

    @Override
    public Set<Couple<State, State>> getEdges(Set<State> visited) {
        Set<Couple<State, State>> edgeSet = new HashSet<>();
        visited.add(this);
        for (State s : successors) {
            edgeSet.add(new Couple(this, s));
            if (!visited.contains(s)) {
                edgeSet.addAll(s.getEdges(visited));
            }
        }
        return edgeSet;
    }

    @Override
    public boolean doCheckForAllSuccessors(String prefix, List<String> participantList, List<State> successors, List<MessageFlow> messageFlows) {
        checked = true;
        return successors.stream()
                .allMatch(z -> z.checkConditionsFromSpec(prefix + space, participantList, messageFlows));
    }


}
