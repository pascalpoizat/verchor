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

import base.Choreography;
import base.ChoreographySpecification;
import cif.CifChoreographySpecification;
import models.base.FmtException;
import models.choreography.cif.CifCifReader;
import models.choreography.cif.CifModel;

import java.io.File;
import java.io.IOException;

public class SynchronizabilityChecker {
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
            CifModel model = new CifModel();
            CifCifReader cifCifReader = new CifCifReader();
            model.setResource(new File(args[0]));
            model.modelFromFile(cifCifReader);
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
        } catch (FmtException | IOException e) {
            // NOTHING
        }
    }
}
