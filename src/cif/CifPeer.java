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

import base.Behaviour;
import base.PeerId;
import base.Peer;

public class CifPeer implements Peer {
    // adapted
    private models.choreography.cif.generated.Peer peer;
    // own data
    private PeerId id;
    private Behaviour behaviour;

    public CifPeer(models.choreography.cif.generated.Peer peer) {
        this.peer = peer;
        this.id = new PeerId(peer.getPeerID());
        this.behaviour = null;
    }

    public PeerId getId() {
        return id;
    }

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public boolean equals(Object o) {
        // two peers are equals if they have the same id
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifPeer cifPeer = (CifPeer) o;

        if (!id.equals(cifPeer.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
