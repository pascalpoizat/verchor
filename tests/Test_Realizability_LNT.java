
import base.Choreography;
import base.ChoreographySpecification;
import cif.CifChoreographySpecification;
import models.base.FmtException;
import models.cif.CifFactory;
import models.cif.CifModel;

import java.io.IOException;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class Test_Realizability_LNT {
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
            // check realizability
            boolean result = choreography.isRealizable();
            if (result)
                System.out.println("realizable");
            else
                System.out.println("not realizable");
        } catch (FmtException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
