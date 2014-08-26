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

package cif;

import base.AlphabetElement;
import base.Message;
import base.Peer;
import models.base.IllegalModelException;

import java.util.Set;

public class CifAlphabetElement implements AlphabetElement {

    private Message message;
    private Peer initiator;
    private Set<Peer> receivers; // NEXT RELEASE : support more than 1 receiver (impacts on several methods)

    public CifAlphabetElement(Message message, Peer initiator, Set<Peer> receivers) throws IllegalModelException {
        if (receivers.size() != 1) {
            throw new IllegalModelException("Illegal alphabet element has not exactly one receiver.");
        }
        this.message = message;
        this.initiator = initiator;
        this.receivers = receivers;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public Peer getInitiatingPeer() {
        return initiator;
    }

    @Override
    public Set<Peer> getParticipants() {
        // does not include the initiator
        // always 1 participant
        return receivers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CifAlphabetElement that = (CifAlphabetElement) o;
        if (!initiator.equals(that.initiator)) return false;
        if (!message.equals(that.message)) return false;
        if (!receivers.equals(that.receivers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + initiator.hashCode();
        result = 31 * result + receivers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s_%s_%s", initiator.toString(), receivers.iterator().next().toString(), message.toString());
    }

    @Override
    public int compareTo(AlphabetElement o) {
        return this.toString().compareTo(o.toString());
    }
}
