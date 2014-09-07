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

import refactoring_from_python.*;
import refactoring_from_python.verification.Checker;
import refactoring_from_python.verification.helpers.Collections;
import refactoring_from_python.verification.helpers.Couple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GatewaySplitState extends GatewayState {

    private Set<State> coneSet;

    public GatewaySplitState(String id) {
        this(id, new HashSet<>());
    }

    public GatewaySplitState(String id, Set<State> successors) {
        super(id, successors);
        this.coneSet = new HashSet<>();
    }

    public Set<State> getConeSet() {
        return coneSet;
    }

    public void setConeSet(Set<State> coneSet) {
        this.coneSet = coneSet;
    }

    @Override
    public boolean checkConditionsFromSpec(String prefix, List<String> prePeerList, List<MessageFlow> preMessageFlows) {
        return doCheckForAllSuccessors(prefix + getSpace(), prePeerList, getSuccessors(), preMessageFlows);
    }

    /**
     * TODO could possibly be factorized in AbstractState
     *
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List<Couple<String, Integer>> reachableParallelMerge(List<String> visited, int depth) {
        if (Collections.isInList(getId(), visited)) {
            return new ArrayList<>();
        } else {
            List<Couple<String, Integer>> rtr = new ArrayList<>();
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            visited2.add(getId());
            for (State s : getSuccessors()) {
                rtr.addAll(s.reachableParallelMerge(visited2, depth));
            }
            return rtr;
        }
    }

    /**
     * TODO could possibly be factorized in AbstractState
     *
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List<Couple<String, Integer>> reachableInclusiveMerge(List<String> visited, int depth) {
        if (Collections.isInList(getId(), visited)) {
            return new ArrayList<>();
        } else {
            List<Couple<String, Integer>> rtr = new ArrayList<>();
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            visited2.add(getId());
            for (State s : getSuccessors()) {
                rtr.addAll(s.reachableInclusiveMerge(visited2, depth));
            }
            return rtr;
        }
    }

    public String computeSetParallelComposition(List<State> synchroProcessSet, List<AlphabetElement> alphabet) {
        List<String> items = new ArrayList<>();
        for (State s : synchroProcessSet) {
            String rtr = "";
            List<AlphabetElement> syncMsg = s.getSyncSet().stream()
                    .map(x -> new StringAlphabetElement(x.getId()))
                    .collect(Collectors.toList());
            rtr += dumpSynchronization(syncMsg, false, true);
            rtr += s.getId();
            rtr += " [";
            rtr += Checker.generateAlphabet(alphabet, false, false, false);
            List<AlphabetElement> alphaSync = s.getSyncSet().stream()
                    .map(x -> new StringAlphabetElement(x.getId()))
                    .collect(Collectors.toList());
            rtr += Checker.generateAlphabet(alphaSync, false, true, true);
            rtr += "]\n";
            items.add(rtr);
        }
        return String.format(" par\n%s\n end par\n", String.join(" ||\n", items));
    }

    public String dumpSynchronization(List<AlphabetElement> rpm, boolean datatype, boolean arrow) {
        String rtr = "";
        if (rpm.size() > 0) {
            rtr += rpm.stream()
                    .map(merge -> String.format("%s%s%s", Checker.synchronous_prefix, merge, datatype ? datatype : ""))  // TODO bizarre ce datatype
                    .collect(Collectors.joining(","));
            if (arrow) {
                rtr += " -> ";
            }
        }
        return rtr;
    }

    public Set<State> computeConeSet(Set<Couple<State, State>> edges, Set<State> states) {
        Set<State> coneSet = new HashSet<>();
        for (State state : states) {
            if (state.equals(this) ||
                    (Checker.existsPath(this, state, edges, new HashSet<>()) &&
                            (Checker.isSynchroMerge(state) || (Checker.existsMergeSuccessor(state, edges, new HashSet<>()))
                            )
                    )
                    ) {
                coneSet.add(state);
            }
        }
        return coneSet;
    }

}
