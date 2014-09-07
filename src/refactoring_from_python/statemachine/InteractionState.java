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
import refactoring_from_python.ChoreographyAlphabetElement;
import refactoring_from_python.MessageFlow;
import refactoring_from_python.verification.helpers.Collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

// TODO this class only works for binary interactions and 2 partners (even if its data model supports more)

public class InteractionState extends IntermediateState {
    private List<MessageFlow> messageFlows;
    private List<String> participants; // list without doubles TODO check if a set would be ok
    private String initiatingPeer;
    private String partnerPeer;

    public InteractionState(String id, List<String> peers, String initiator, List<MessageFlow> messageFlows) throws IllegalModelException {
        this(id, new HashSet<>(), peers, initiator, messageFlows);
    }

    public InteractionState(String id, Set<State> successors, List<String> participants, String initiatingPeer, List<MessageFlow> messageFlows) throws IllegalModelException {
        super(id, successors);
        if (participants.size() != 2) {
            throw new IllegalModelException("Incorrect alphabet element, more than 2 peers");
        }
        if (!participants.contains(initiatingPeer)) {
            throw new IllegalModelException(String.format("Incorrect alphabet element, initiating peer %s not in participants", initiatingPeer));
        }
        this.messageFlows = messageFlows;
        this.participants = participants;
        this.initiatingPeer = initiatingPeer;
        if (participants.get(0).equals(initiatingPeer)) {
            this.partnerPeer = participants.get(1);
        } else {
            this.partnerPeer = participants.get(0);
        }
    }

    public String getInitiator() {
        return initiatingPeer;
    }

    public String getPartner() {
        return partnerPeer;
    }

    public List<MessageFlow> getMessageFlows() {
        return messageFlows;
    }

    @Override
    public List<String> getPeers() {
        return participants;
    }

    @Override
    public List<AlphabetElement> getAlphabet() {
        return buildList(getInitiator(), getPeers());
    }

    private List<AlphabetElement> buildList(String initiator, List<String> peers) {
        List<AlphabetElement> rtr = new ArrayList<>();
        try {
            if (messageFlows.size() == 1) { // initiator -> partner message
                rtr.add(new ChoreographyAlphabetElement(messageFlows.get(0).getMessage(), peers, getInitiator()));
            }
            if (messageFlows.size() >= 2) { // partner -> initiator message, for two-way communication
                rtr.add(new ChoreographyAlphabetElement(messageFlows.get(0).getMessage(), peers, getPartner()));
            }
        } catch (IllegalModelException e) {
        } // impossible
        return rtr;
    }

    @Override
    public boolean checkConditionsFromSpec(String prefix, List<String> prePeerList, List<MessageFlow> preMessageFlows) {
        if (prePeerList.isEmpty()) {

        } else {
            List<String> listPeerIntersect = prePeerList.stream()
                    .filter(x -> x.equals(getInitiator()))
                    .collect(Collectors.toList());
            if(listPeerIntersect.isEmpty()) {
                return false;
            }
        }
        return doCheckForAllSuccessors(prefix + getSpace(), getPeers(), getSuccessors(), getMessageFlows());
    }

    /**
     * TODO could possibly be factorized in AbstractState
     *
     * @param visited
     * @param depth
     * @return
     */
    @Override
    public List reachableParallelMerge(List<String> visited, int depth) {
        if (Collections.isInList(getId(), visited)) {
            return new ArrayList<>();
        } else {
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            visited2.add(getId());
            return getSuccessors().get(0).reachableParallelMerge(visited2, depth);
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
    public List reachableInclusiveMerge(List<String> visited, int depth) {
        if (Collections.isInList(getId(), visited)) {
            return new ArrayList<>();
        } else {
            List<String> visited2 = new ArrayList<>();
            visited2.addAll(visited);
            visited2.add(getId());
            return getSuccessors().get(0).reachableInclusiveMerge(visited2, depth);
        }
    }

    @Override
    public String visit_lnt(Checker checker, List<AlphabetElement> alpha) {
        String rtr = "";
        rtr += dumpMessage();
        List<String> alphaSync = getSyncSet().stream().map(x -> x.getId()).collect(Collectors.toList());
        rtr += checker.dumpSucc(alpha, getSuccessors(), true, alphaSync);
        return rtr;
    }

    private String dumpMessage() {
        return String.format("%s_%s_%s", getInitiator(), getPartner(), getMessageFlows().get(0).getMessage());
    }

}
