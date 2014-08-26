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

package base;

import models.base.IllegalResourceException;

import java.util.HashMap;

public class Choreography {

    private ChoreographySpecification choreographySpecification;
    private HashMap<PeerId, Peer> peers;

    public Choreography(ChoreographySpecification choreographySpecification) {
        this.choreographySpecification = choreographySpecification;
        peers = new HashMap<>();
    }

    public Choreography(ChoreographySpecification choreographySpecification, HashMap<PeerId, Peer> peers) {
        this.choreographySpecification = choreographySpecification;
        this.peers = peers;
    }

    public ChoreographySpecification getSpecification() {
        return choreographySpecification;
    }

    public HashMap<PeerId, Peer> getPeers() {
        return peers;
    }

    public Peer getPeer(PeerId peerId) throws UnknownPeerId {
        if (peers.containsKey(peerId)) {
            return peers.get(peerId);
        } else {
            throw new UnknownPeerId(UnknownPeerId.MESSAGE + "(" + peerId + ")");
        }
    }

    public boolean isRealizable() throws IllegalResourceException {  // checks whether the choreography specification is realizable or not
        return choreographySpecification.isRealizable();
    }

    public boolean isSynchronizable() throws IllegalResourceException { // checks whether the choreography specification is synchronizable or not
        return choreographySpecification.isSynchronizable();
    }

    public boolean isConform() throws IllegalResourceException { // checks whether the set of peers conform to the choreography specification
        return choreographySpecification.conformsWith(peers);
    }

    public void project() throws IllegalResourceException { // computes the set of peers from the choreography specification (side-effect)
        peers = choreographySpecification.project();
    }

}
