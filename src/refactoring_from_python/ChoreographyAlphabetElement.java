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

import models.base.IllegalModelException;

import java.util.List;

/**
 * element of alphabet for a choreography
 * m[x,P] where m is the message, x is the initiating peer, and P the set of participating peers
 * constrains:
 * - x in P
 * - size of P is 2
 */
public class ChoreographyAlphabetElement implements AlphabetElement {


    private String message;
    private List<String> participants; // list without doubles TODO check if a set would be ok
    private String initiatingPeer;
    private String partnerPeer;

    public ChoreographyAlphabetElement(String message, List<String> participants, String initiatingPeer) throws IllegalModelException {
        if (participants.size() != 2) {
            throw new IllegalModelException("Incorrect alphabet element, more than 2 peers");
        }
        if (!participants.contains(initiatingPeer)) {
            throw new IllegalModelException(String.format("Incorrect alphabet element, initiating peer %s not in participants", initiatingPeer));
        }
        this.message = message;
        this.participants = participants;
        this.initiatingPeer = initiatingPeer;
        if (participants.get(0).equals(initiatingPeer)) {
            this.partnerPeer = participants.get(1);
        } else {
            this.partnerPeer = participants.get(0);
        }
    }

    public String getMessage() {
        return message;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getInitiatingPeer() {
        return initiatingPeer;
    }

    private String getPartnerPeer() {
        return partnerPeer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChoreographyAlphabetElement that = (ChoreographyAlphabetElement) o;

        if (!initiatingPeer.equals(that.initiatingPeer)) return false;
        if (!message.equals(that.message)) return false;
        if (!participants.equals(that.participants)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + initiatingPeer.hashCode();
        result = 31 * result + participants.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s_%s_%s",
                getInitiatingPeer(),
                getPartnerPeer(),
                getMessage());
    }

    // TODO check consistency wrt equals()
    @Override
    public int compareTo(AlphabetElement o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public boolean concernsPeer(String peerId) {
        return (peerId.equals(getInitiatingPeer())|| peerId.equals(getPartnerPeer()));
    }
}
