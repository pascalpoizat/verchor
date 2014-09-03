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
import refactoring_from_python.Choreography;
import refactoring_from_python.Couple;
import refactoring_from_python.MessageFlow;

import java.util.List;
import java.util.Set;

public interface State {

    /**
     * returns the state id
     *
     * @return
     */
    public String getId();

    /**
     * returns the choreography the state is in
     *
     * @return
     */
    public Choreography getChoreography();

    /**
     * tests if the state has been checked TODO should use visitor pattern instead
     *
     * @return
     */
    public boolean isChecked();

    /**
     * returns the offset to print tree TODO should use visitor pattern instead
     *
     * @return
     */
    public String getSpace();

    /**
     * returns the predecessors of the state
     *
     * @return
     */
    public Set<State> getPredecessors();

    /**
     * returns the successors of the state
     *
     * @return
     */
    public List<State> getSuccessors();

    /**
     * returns the peers of the state
     *
     * @return
     */
    public List<String> getPeers();

    /**
     * returns the alphabet of the state
     *
     * @return
     */
    public List<AlphabetElement> getAlphabet();

    /**
     * returns the synchronisation set of the state
     *
     * @return
     */
    public Set<State> getSyncSet();

    /**
     * adds a predecessor to the state // TODO should not be possible for initial state
     *
     * @param predecessor
     * @return true if the state was not already present
     */
    public boolean addPredecessor(State predecessor);

    /**
     * adds a successor to the state // TODO should not be possible for final states
     *
     * @param successor
     * @return
     */
    public boolean addSuccessor(State successor);

    /**
     * sets the choreography the state is member of
     *
     * @param choreography
     */
    public void setChoreography(Choreography choreography);

    /**
     * computes all edges reachable from the current state
     *
     * @param visited
     * @return
     */
    public Set<Couple<State, State>> getEdges(Set<State> visited);

    /**
     * performs check for all successor states
     *
     * @param prefix
     * @param participantList
     * @param successors
     * @param messageFlows
     * @return
     */
    public boolean doCheckForAllSuccessors(String prefix, List<String> participantList, List<State> successors, List<MessageFlow> messageFlows);

    /**
     * checks a state
     *
     * @param prefix
     * @param prePeerList
     * @param preMessageFlows
     * @return
     */
    public boolean checkConditionsFromSpec(String prefix, List<String> prePeerList, List<MessageFlow> preMessageFlows);


    // TODO for these two methods, check if visited should be used in "in" mode or in "inout" mode
    public List<Couple<String, Integer>> reachableParallelMerge(List<String> visited, int depth);
    public List<Couple<String, Integer>> reachableInclusiveMerge(List<String> visited, int depth);

    public String lnt(List<AlphabetElement> alphabet) throws IllegalModelException;

}
