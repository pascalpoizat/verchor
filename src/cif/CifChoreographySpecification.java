package cif;

import base.*;
import java.util.HashMap;
import models.choreography.cif.CifModel;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class CifChoreographySpecification extends ChoreographySpecification {

    private CifModel model;

    public CifChoreographySpecification(CifModel model) {
        this.model = model;
    }

    @Override
    public boolean conformsWith(HashMap<PeerId, Peer> peers) {
        return false; // TODO (issue : peers cannot be CIF models. May require "combination" factories eg CIF/LTS or PNML/PNML)
    }

    @Override
    public boolean isRealizable() {
        // 1 - transform the CIF model into an LNT one
        // since LNT is not a regular fmt model (reified), and for efficiency purposes, we will directly generate an Lnt file from a CIF model instance
        // 2 - dump the LNT model
        // 3 - create the DVL scripts
        // 4 - call SVL
        // 5 - get and return results
        return false; // TODO
    }

    @Override
    public boolean isSynchronizable() {
        return false; // TODO
    }

    @Override
    public HashMap<PeerId, Peer> project() {
        return null; // TODO
    }
}
