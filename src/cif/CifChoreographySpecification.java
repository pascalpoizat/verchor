package cif;

import base.*;

import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import models.choreography.cif.CifModel;
import models.base.IllegalResourceException;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class CifChoreographySpecification extends ChoreographySpecification {

    public static final String NAME = "lnt based verification";
    public static final String VERSION = "1.0";

    private static final String synchronizability_suffix = "_synchronizability";
    private static final String realizability_suffix = "_realizability";
    private static final String minimizing_suffix = "_min";
    private static final String choreography_model_suffix = "_bpmnlts_min";
    private static final String asynchronous_composition_suffix = "_acompo";
    private static final String synchronous_composition_suffix = "_compo_sync";
    private static final String synchronizability_equivalence = "strong comparison using bfs";
    private static final String realizability_equivalence = "strong comparison using bfs";
    private static final String equivalence_checker = "bisimulator";
    private static final String svl_suffix = ".svl";
    private static final String lnt_suffix = ".lnt";
    private static final String bcg_suffix = ".bcg";

    // TODO NOTE: some of the elements in this class are to be moved in order to promote separation between LNT-based specific code (in Cif* or Cif*WithLnt classes) wrt generic code (in ChoreographyVerificationEngine)

    private CifModel model;
    private String name;
    private String choreography_model;
    private String synchronous_composition_model;
    private String asynchronous_composition_model;

    public CifChoreographySpecification(CifModel model) {
        super();
        this.model = model;
        setupStrings();
    }

    @Override
    protected boolean conformsWith(HashMap<PeerId, Peer> peers) throws IllegalResourceException {
        return false; // TODO (issue : peers cannot be CIF models. May require "combination" factories eg CIF/LTS or PNML/PNML)
    }

    @Override
    protected boolean isRealizable() throws IllegalResourceException {
        // if the model has changed, re-generate LNT and SVL files
        if (hasChanged()) {
            try {
                setupStrings();
                generateLNT();
                generateSvlForDefinitions();
                generateSvlForRealizability();
            } catch (IllegalResourceException e) {
                error(e.getMessage());
                throw e;
            }
        }
        // - call SVL
        // TODO
        // - get and return results
        // TODO
        return false;
    }

    @Override
    protected boolean isSynchronizable() throws IllegalResourceException {
        // if the model has changed, re-generate LNT and SVL files
        if (hasChanged()) {
            try {
                setupStrings();
                generateLNT();
                generateSvlForDefinitions();
                generateSvlForSynchronizability();
            } catch (IllegalResourceException e) {
                error(e.getMessage());
                throw e;
            }
        }
        // - call SVL
        // TODO
        // - get and return results
        // TODO
        return false;
    }

    @Override
    protected HashMap<PeerId, Peer> project() throws IllegalResourceException {
        return null; // TODO
    }

    private void generateLNT() {
        // generate an LNT model (file) from a CIF one (model instance)
        // since LNT is not a regular fmt model (LNT is not reified), and for efficiency purposes, we will directly generate an LNT file
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        // TODO
    }

    private void generateSvlForDefinitions() {
        // generate SVL file from a CIF model for choregraphy verification (definitions)
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        // TODO
    }

    private void generateSvlForSynchronizability() throws IllegalResourceException {
        // generate SVL file from a CIF model for choregraphy verification (synchronizability check)
        // synchronizability check (WWW 2011) using trace equivalence between the synchronous composition and the 1-bounded asynchronous composition
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        String result_model = name + synchronizability_suffix + bcg_suffix;
        String script = String.format("\"%s\" = %s with %s \"%s\" ==  \"%s\";\n\n", result_model, synchronizability_equivalence, equivalence_checker, synchronous_composition_model, asynchronous_composition_model);
        message("synchronizability checked with: " + script);
        writeSvlToFile(script, synchronizability_suffix);
    }

    private void generateSvlForRealizability() throws IllegalResourceException {
        // generate SVL
        // equivalence between the choreography LTS and the distributed system LTS (async). Nb: here we only consider emissions in the distributed system
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        String result_model = name + realizability_suffix + bcg_suffix;
        String script = String.format("\"%s\" = %s with %s \"%s\" ==  \"%s\";\n\n", result_model, realizability_equivalence, equivalence_checker, choreography_model, asynchronous_composition_model);
        message("synchronizability checked with: " + script);
        writeSvlToFile(script, realizability_suffix);
    }

    private void writeSvlToFile(String script, String suffix) throws IllegalResourceException {
        // writes an SVL script to a file
        // suffix is to be appended to the name of the resource we are operating on to generate distinct file names for distinct scripts used in verification
        try {
            File out = new File(model.getResource().getParent(), name + suffix + svl_suffix);
            FileWriter fw = new FileWriter(out);
            fw.write(script);
            message("File " + out.getAbsolutePath() + " written");
            fw.close();
        } catch (IOException e) {
            IllegalResourceException e2 = new IllegalResourceException("Cannot open output resource");
            throw e2;
        }
    }

    private void setupStrings() {
        // sets up strings to be used in verification for the representation of different processes (synchronous composition, asynchronous composition, etc.)
        name = model.getResource().getName();
        name = name.substring(0, name.length() - (model.getSuffix().length() + 1));
        choreography_model = name + choreography_model_suffix + bcg_suffix;
        synchronous_composition_model = name + synchronous_composition_suffix + minimizing_suffix + bcg_suffix;
        asynchronous_composition_model = name + asynchronous_composition_suffix + minimizing_suffix + bcg_suffix;
        message("name: " + name);
        message("choreography model:" + choreography_model);
        message("synchronous composition model: " + synchronous_composition_model);
        message("asynchronous composition model: " + asynchronous_composition_model);
    }

    @Override
    public void about() {
        System.out.println(NAME + " " + VERSION);
    }


}
