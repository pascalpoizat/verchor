import base.Choreography;
import base.ChoreographySpecification;
import cif.CifChoreographySpecification;
import models.base.FmtException;
import models.choreography.cif.CifFactory;
import models.choreography.cif.CifModel;

import java.io.IOException;

/**
 * Created by pascalpoizat on 07/02/2014.
 */
public class Test_Synchronizability_LNT {
    public static void main(String[] args) {
        /**
         * takes as input a choreography specification file (CIF format) and checks whether it is synchronizable or not
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
            // check synchronizability
            boolean result = choreography.isSynchronizable();
            if (result)
                System.out.println("synchronizable");
            else
                System.out.println("not synchronizable");
        } catch (FmtException e) {
            // NOTHING
        } catch (IOException e) {
            // NOTHING
        }
    }
}
