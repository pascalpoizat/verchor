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
import refactoring_from_python.StringAlphabetElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChoiceState extends GatewaySplitState {
    public ChoiceState(String id) {
        this(id, new HashSet<>());
    }

    public ChoiceState(String id, Set<State> successors) {
        super(id, successors);
    }

    @Override
    public String visit_lnt(Checker checker, List<AlphabetElement> alphabet) {
        List<String> choices = new ArrayList<>();
        List<AlphabetElement> alphaSync;
        for (State successor : getSuccessors()) {
            String rtr = "";
            if (Checker.isSynchroSelect(successor) && !checker.splitInOtherSplitCone((GatewaySplitState)successor)) {
                rtr += Checker.split_prefix;
                alphaSync = getSyncSet().stream().map(x -> new StringAlphabetElement(x.getId())).collect(Collectors.toList());
            } else {
                alphaSync = successor.getSyncSet().stream().map(x -> new StringAlphabetElement(x.getId())).collect(Collectors.toList());
            }
            rtr += String.format("%s [%s]\n",
                    successor.getId(),
                    Checker.dumpAlphabetToString(alphabet, false, false, false),
                    Checker.dumpAlphabetToString(alphaSync, false, true, true));
            choices.add(rtr);
        }
        return String.format(" select\n%s\n end select\n", String.join(" [] ", choices));
    }
}
