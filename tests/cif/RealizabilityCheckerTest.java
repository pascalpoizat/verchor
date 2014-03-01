package cif;

import base.Choreography;
import base.ChoreographySpecification;
import models.choreography.cif.CifFactory;
import models.choreography.cif.CifModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

/**
 * Created by pascalpoizat on 01/03/2014.
 */
public class RealizabilityCheckerTest {

    CifFactory cifFactory;
    CifModel model;

    @DataProvider
    public Object[][] inputData() {
        return new Object[][]{
                {"/Users/pascalpoizat/IdeaProjects/verchor/examples/atomic_test_cases/simple.cif"},
                {"/Users/pascalpoizat/IdeaProjects/verchor/examples/atomic_test_cases/repeated_exchange.cif"}//,
                //{"/Users/pascalpoizat/IdeaProjects/verchor/examples/comanche/comanche.cif"},
                //{"/Users/pascalpoizat/IdeaProjects/verchor/examples/online_shopping/online_shopping_v1.cif"},
                //{"/Users/pascalpoizat/IdeaProjects/verchor/examples/online_shopping/online_shopping_v2.cif"}
        };
    }

    @BeforeMethod // could possibly by @BeforeClass
    public void setUp() throws Exception {
        cifFactory = CifFactory.getInstance();
    }

    @Test(dataProvider = "inputData")
    public void testIsRealizable(String filename) {
        try {
            model = (CifModel) cifFactory.createFromFile(filename);
            ChoreographySpecification specification = new CifChoreographySpecification(model);
            Choreography choreography = new Choreography(specification);
            specification.setVerbose(true);
            specification.about();
            boolean result = choreography.isRealizable();
            System.out.println(result);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }
}
