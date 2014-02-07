package cif;

import base.*;

import java.util.HashMap;

import models.choreography.cif.CifModel;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class CifChoreographySpecification extends ChoreographySpecification {

    private CifModel model;
    private boolean has_changed; // used to indicate that the model has changed and LNT/SVL generation should be done

    public CifChoreographySpecification(CifModel model) {
        this.model = model;
        has_changed = true;
    }

    @Override
    public boolean conformsWith(HashMap<PeerId, Peer> peers) {
        return false; // TODO (issue : peers cannot be CIF models. May require "combination" factories eg CIF/LTS or PNML/PNML)
    }

    @Override
    public boolean isRealizable() {
        // if the model has changed, re-generate LNT and SVL files
        if (has_changed) {
            generateLNT();
            generateSVL();
        }
        // - call SVL
        // TODO
        // - get and return results
        // TODO
        return false;
    }

    @Override
    public boolean isSynchronizable() {
        return false; // TODO
    }

    @Override
    public HashMap<PeerId, Peer> project() {
        return null; // TODO
    }

    public void signalChange() {
        // used to signal that the model has changed
        // should be called each time the model is changed in order to keep synchronization between CIF model and generated LNT/SVL files
        has_changed = true;
    }

    private void generateLNT() {
        // generate an LNT model (file) from a CIF one (model instance)
        // since LNT is not a regular fmt model (LNT is not reified), and for efficiency purposes, we will directly generate an LNT file
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        // TODO
    }

    private void generateSVL() {
        // generate SVL file from a CIF model for choregraphy verification
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        // TODO
    }

}
