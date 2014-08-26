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

import java.util.Set;

public interface AlphabetElement extends Comparable<AlphabetElement> {
    // an alphabet element encodes a choreography alphabet element, ie a triple m[x,P]
    // where m is the message, x is the initiating peer, and P are the other participants
    // important: x in P denotes that x is the initiating peer AND a receiving peer, else x not in P

    public Message getMessage(); // get m from m[x,P]

    public Peer getInitiatingPeer(); // get x from m[x,P]

    public Set<Peer> getParticipants(); // get P from m[x,P]

    @Override
    public boolean equals(Object o);

    @Override
    public int hashCode();

    @Override
    public int compareTo(AlphabetElement o);
}
