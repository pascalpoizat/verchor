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

package refactoring_from_python.verification.helpers;

import refactoring_from_python.statemachine.State;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Collections {

    /**
     * checks wether a string belongs to a list
     * TODO useless, remove and replace by native method
     *
     * @param element
     * @param list
     * @return
     */
    public static <T> boolean isInList(T element, List<T> list) {
        return (list.contains(element));
    }

    /**
     * checks whether a list is included in another list. Used only in buildChoreoFromFile to sort states
     * TODO should be refactored to simplify
     *
     * @param src        list of state ids to look for
     * @param dst        list of states
     * @param successors list of states in dst that match ids in src
     * @return indicator if successors is relevant or not
     */
    public static boolean hasSuccInList(List<String> src, List<State> dst, List<State> successors) {
        boolean resGlob = true;
        for (String succ : src) {
            boolean resLoc = false;
            for (State e : dst) {// e = instance of already encoded state
                if (succ.equals(e.getId())) {
                    successors.add(e);
                    resLoc = true;
                }
            }
            resGlob = resGlob && resLoc;
        }
        return resGlob;
    }

    /**
     * filters a list of couples (string,depth) to keep only strings where depth is 0
     *
     * @param l list of couples (string,depth)
     * @return the strings from l where the depth is 0
     */
    public static List<String> keepZeroDepthStrings(List<Couple<String, Integer>> l) {
        List<String> res = l.stream()
                .filter(couple -> couple.getSecond() == 0)
                .map(Couple::getFirst)
                .collect(Collectors.toList());
        return res;
    }

    /**
     * removes double occurences of strings in a list
     * TODO should be refactored to simplify + useless ? it seems just to return l in the end (not removing multiple occurences in l !)
     *
     * @param l    list of string to check for
     * @param full list of strings possibly with doubles
     * @return
     */
    public static <T> List<T> removeDoubles(List<T> l, List<T> full) {
        List<T> single = new ArrayList<>();
        List<T> dble = new ArrayList<>();
        for (T name : l) {
            if (full.stream().filter(e -> e.equals(name)).count() <= 1) {
                single.add(name);
            } else {
                if (!dble.contains(name)) {
                    dble.add(name);
                }
            }
        }
        single.addAll(dble);
        return single;
    }


}
