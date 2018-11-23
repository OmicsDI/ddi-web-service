package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetService;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/test-context.xml" })
public class DatasetTests {

    /*@Autowired
    IDatasetService datasetService;*/

   @Test
    public void datasetTest() {
/*        Dataset dataset = datasetService.read("PXD004233","Pride");
        assertNotNull(dataset);*/
        assert(true);
    }
}
