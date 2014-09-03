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
import refactoring_from_python.Checker;
import refactoring_from_python.Couple;
import refactoring_from_python.StringAlphabetElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SubsetSelectState extends GatewaySplitState {
    public SubsetSelectState(String id) {
        this(id, new HashSet<>());
    }

    public SubsetSelectState(String id, Set<State> successors) {
        super(id, successors);
    }

    @Override
    public List<Couple<String, Integer>> reachableInclusiveMerge(List<String> visited, int depth) {
        List<Couple<String, Integer>> rtr;
        rtr = new ArrayList<>();
        if (!Checker.isInList(getId(), visited)) {
            List<String> visited2 = new ArrayList<>();
            visited2.add(getId());
            visited2.addAll(visited);
            for (State successor : getSuccessors()) {
                rtr.addAll(successor.reachableInclusiveMerge(visited2, depth + 1));
            }
        }
        return rtr;
    }

    // TODO SHOULD STRONGLY BE CHECKED, PYTHON CODE WAS VERY ILLEGIBLE
    @Override
    public String lnt(List<AlphabetElement> alphabet) throws IllegalModelException {
        // loop over all successors e.g. A,B,C (and default)
        // generate processes of the form:
        // sync ->     A || (B [] sync; null || C [] sync; null)
        //          [] B || (A [] null || C [] null)
        //          [] C || (A [] null || B [] null)
        //         ([] default)
        // sync; null is important!
        // we suppose we have at least 2 branches excluding the default case (not meaningful else)
        boolean hasDefault = Checker.existDefaultBranch(getSuccessors());
        int alternatives = getSuccessors().size() - 1;
        if (!((alternatives == 2 && !hasDefault) || (alternatives > 2))) {
            throw new IllegalModelException("Wrong model, subset select state with less than 2 choices");
        }
        List<String> items = new ArrayList<>();
        List<String> rpm = getSyncSet().stream()
                .map(state -> state.getId())
                .collect(Collectors.toList());
        for (State activeProcess : getSuccessors()) {
            String item;
            if (hasDefault && activeProcess.getId().equals(Checker.default_branch_state_name)) {
                item = String.format("default [%s%s]",
                        Checker.dumpAlphabetToString(alphabet, false, false, false),
                        Checker.dumpAlphabetToString(getSyncSet().stream()
                                .map(state -> new StringAlphabetElement(state.getId()))
                                .collect(Collectors.toList()), false, true, true));
            } else {
                List<AlphabetElement> alphaSync = activeProcess.getSyncSet().stream()
                        .map(state -> new StringAlphabetElement(state.getId()))
                        .collect(Collectors.toList());
                // left part
                String leftpart = String.format("%s%s [%s%s]",
                        dumpSynchronization(alphaSync, false, true),
                        activeProcess.getId(),
                        Checker.dumpAlphabetToString(alphabet, false, false, false),
                        Checker.dumpAlphabetToString(alphaSync, false, true, true));
                // right part
                List<State> otherProcesses = getSuccessors().stream()
                        .filter(state -> !state.getId().equals(Checker.default_branch_state_name) &&
                                !state.getId().equals(activeProcess.getId()))
                        .collect(Collectors.toList());
                // #successors =  #otherProcesses + 1 (activeProcess) + 1 (if hasDefault)
                String prefix;
                String suffix;
                if (otherProcesses.size() > 1) {
                    prefix = " par\n";
                    suffix = " end par\n";
                } else {
                    prefix = "";
                    suffix = "";
                }
                List<String> subitems = new ArrayList<>();
                String subitem;
                for (State perhapsProcess : otherProcesses) {
                    alphaSync = perhapsProcess.getSyncSet().stream()
                            .map(state -> new StringAlphabetElement(state.getId()))
                            .collect(Collectors.toList());
                    subitem = dumpSynchronization(alphaSync, false, true);
                    subitem += " select\n";
                    subitem += String.format("%s [%s%s] []\n",
                            perhapsProcess.getId(),
                            Checker.dumpAlphabetToString(alphabet, false, false, false),
                            Checker.dumpAlphabetToString(alphaSync, false, true, true));
                    if (alphaSync.size()>0) {
                        subitem += Checker.dumpParallelSyncsToString(alphaSync);
                    }
                    else {
                        subitem += " null\n";
                    }
                    subitem += " end select\n";
                    subitems.add(subitem);
                }
                item = String.format(" par\n%s || %s%s%s\n end par\n", leftpart, prefix, String.join(" ||\n", subitems), suffix);
            }
            items.add(item);
        }
        return String.format(" select\n%s\n end select\n", String.join(" []\n", items));
    }
}
