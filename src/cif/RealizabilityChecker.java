package cif;

import base.Choreography;
import base.ChoreographySpecification;
import cif.CifChoreographySpecification;
import models.base.FmtException;
import models.choreography.cif.CifFactory;
import models.choreography.cif.CifModel;

import java.io.IOException;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class RealizabilityChecker {
    public static void main(String[] args) {
        /**
         * takes as input a choreography specification file (CIF format) and checks whether it is realizable or not
         */
        if (args.length != 1) {
            System.out.println("missing argument (choreography specification file, in CIF)");
            System.exit(0);
        }
        try {
            // load model
            CifFactory cifFactory = CifFactory.getInstance();
            CifModel model = (CifModel) cifFactory.createFromFile(args[0]);
            // get choreography
            ChoreographySpecification specification = new CifChoreographySpecification(model);
            Choreography choreography = new Choreography(specification);
            specification.setVerbose(true);
            specification.about();
            // check realizability
            boolean result = choreography.isRealizable();
            if (result)
                System.out.println("realizable");
            else
                System.out.println("not realizable");
        } catch (FmtException e) {
            // NOTHING
        } catch (IOException e) {
            // NOTHING
        }
    }
}
