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

import refactoring_from_python.verification.Checker;
import refactoring_from_python.verification.helpers.Collections;
import refactoring_from_python.verification.helpers.Couple;
import refactoring_from_python.MessageFlow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public abstract class GatewayMergeState extends GatewayState {
    public GatewayMergeState(String id) {
        this(id, new HashSet<>());
    }

    public GatewayMergeState(String id, Set<State> successors) {
        super(id, successors);
    }

    @Override
    public boolean checkConditionsFromSpec(String prefix, List<String> prePeerList, List<MessageFlow> preMessageFlows) {
        return doCheckForAllSuccessors(prefix + getSpace(), prePeerList, getSuccessors(), preMessageFlows);
    }

    /**
     * TODO could possibly be factorized in AbstractState
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List<Couple<String, Integer>> reachableParallelMerge(List<String> visited, int depth) {
        if(Collections.isInList(getId(), visited)) {
            return new ArrayList<>();
        }
        else {
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            visited2.add(getId());
            return getSuccessors().get(0).reachableParallelMerge(visited2, depth);
        }
    }

    /**
     * TODO could possibly be factorized in AbstractState
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List<Couple<String, Integer>> reachableInclusiveMerge(List<String> visited, int depth) {
        if(Collections.isInList(getId(), visited)) {
            return new ArrayList<>();
        }
        else {
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            visited2.add(getId());
            return getSuccessors().get(0).reachableInclusiveMerge(visited2, depth);
        }
    }
}
