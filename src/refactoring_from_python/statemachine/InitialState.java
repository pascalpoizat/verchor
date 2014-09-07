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

import models.base.IllegalModelException;
import refactoring_from_python.AlphabetElement;
import refactoring_from_python.verification.Checker;
import refactoring_from_python.verification.helpers.Couple;
import refactoring_from_python.MessageFlow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InitialState extends AbstractState {
    public InitialState(String id) {
        this(id, new HashSet<>());
    }

    public InitialState(String id, Set<State> successors) {
        super(id, successors);
    }

    @Override
    public boolean checkConditionsFromSpec(String prefix, List<String> prePeerList, List<MessageFlow> preMessageFlows) {
        return doCheckForAllSuccessors(prefix + getSpace(), new ArrayList<>(), getSuccessors(), new ArrayList<>());
    }

    /**
     * TODO does not checks if the state is visited ?
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List<Couple<String, Integer>> reachableParallelMerge(List<String> visited, int depth) {
        List visited2 = new ArrayList<>();
        visited2.addAll(visited);
        visited2.add(getId());
        return getSuccessors().get(0).reachableParallelMerge(visited2, depth);
    }

    /**
     * TODO does not checks if the state is visited ?
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List<Couple<String, Integer>> reachableInclusiveMerge(List<String> visited, int depth) {
        List visited2 = new ArrayList<>();
        visited2.addAll(visited);
        visited2.add(getId());
        return getSuccessors().get(0).reachableInclusiveMerge(visited2, depth);
    }

    @Override
    public String visit_lnt(Checker checker, List<AlphabetElement> alphabet) throws IllegalModelException {
        throw new IllegalModelException("Should not happen. Check if there is an incoming edge to the initial state");
    }

}

