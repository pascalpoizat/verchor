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
import refactoring_from_python.Checker;
import refactoring_from_python.Couple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllJoinState extends GatewayMergeState {
    public AllJoinState(String id) {
        this(id, new HashSet<>());
    }

    public AllJoinState(String id, Set<State> successors) {
        super(id, successors);
    }

    @Override
    public String lnt(List<AlphabetElement> alphabet) {
        String rtr = "";
        rtr += String.format("%s%s;", Checker.synchronous_prefix, getId());
        if (!(Checker.isSynchroMerge(getSuccessors().get(0)))) {
            rtr += Checker.dumpSucc(alphabet, getSuccessors(), false, new ArrayList<>());
        } else {
            rtr += " null\n";
        }
        return rtr;
    }

    @Override
    public List<Couple<String, Integer>> reachableParallelMerge(List<String> visited, int depth) {
        List<Couple<String, Integer>> rtr;
        rtr = new ArrayList<>();
        if (!Checker.isInList(getId(), visited)) {
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            rtr.add(new Couple(getId(), depth));
            rtr.addAll(getSuccessors().get(0).reachableParallelMerge(visited2, depth - 1));
        }
        return rtr;
    }

}

