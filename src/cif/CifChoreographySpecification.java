package cif;

import base.*;
import models.cif.CifModel;

import java.util.HashMap;

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
        return false; // TODO (issue : peers cannot be CIF model. Should required "combination" factories eg CIF/LTS or PNML/PNML)
    }

    @Override
    public boolean isRealizable() {
        // 1 - transform the Cif model into an Lnt one
        // 2 - dump the Lnt model
        // 3 - create the Svl scripts
        // 4 - call svl
        // 5 - get and return results
        return false; // TODO
    }

    @Override
    public HashMap<PeerId, Peer> project() {
        return null; // TODO
    }
}
