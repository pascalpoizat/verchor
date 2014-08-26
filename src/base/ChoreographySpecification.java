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

import java.util.List;
import java.util.HashMap;

public abstract class ChoreographySpecification {

    private List<PeerId> peers;
    private HashMap<MessageId, AlphabetElement> messages;
    private Behaviour behaviour;
    private boolean verbose;


    public ChoreographySpecification() {
        verbose = false;
    }

    public List<PeerId> getPeers() {
        return peers;
    }

    public HashMap<MessageId, AlphabetElement> getMessages() {
        return messages;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    protected abstract boolean isRealizable(); // checks whether the choreography specification is realizable or not

    protected abstract boolean isSynchronizable(); // checkes whether the choreography specification is synchronizable or not

    protected abstract boolean conformsWith(HashMap<PeerId, Peer> peers); // checks whether a set of peers is conform to the choreography specification

    protected abstract HashMap<PeerId, Peer> project(); // computes a set of peers by projecting the choreography specification

    // methods below should probably move to Choreography

    public void setVerbose(boolean verbose) {



        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void message(String msg) {
        if (this.verbose) {
            System.out.println("" + msg);
        }
    }

    public void error(String msg) {
        System.out.println("ERROR: " + msg);
    }

    public void warning(String msg) {
        System.out.println("WARNING: " + msg);
    }

    public abstract void about();

}
