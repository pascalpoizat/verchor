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

package refactoring_from_python;

import refactoring_from_python.statemachine.State;
import refactoring_from_python.verification.helpers.Couple;

/**
 * Edge between two states
 * adaptor for Couple
 */
public class Edge {
    private Couple<State, State> couple;

    public Edge(State source, State target) {
        couple = new Couple<>(source, target);
    }

    public State getSource() { return couple.getFirst(); }

    public State getTarget() { return couple.getSecond(); }

    @Override
    public boolean equals(Object o) {
        return couple.equals(o);
    }

    @Override
    public int hashCode() {
        return couple.hashCode();
    }
}
