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
import refactoring_from_python.verification.Checker;
import refactoring_from_python.verification.helpers.Couple;
import refactoring_from_python.MessageFlow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FinalState extends AbstractState {
    public FinalState(String id) {
        super(id, new HashSet<>());
    }

    @Override
    public boolean checkConditionsFromSpec(String prefix, List<String> prePeerList, List<MessageFlow> preMessageFlows) {
        return true;
    }

    @Override
    public List<Couple<String, Integer>> reachableParallelMerge(List<String> visited, int depth) {
        return new ArrayList<>();
    }

    @Override
    public List<Couple<String, Integer>> reachableInclusiveMerge(List<String> visited, int depth) {
        return new ArrayList<>();
    }

    @Override
    public String visit_lnt(Checker checker, List<AlphabetElement> alpha) {
        return "null\n";
    }

}


