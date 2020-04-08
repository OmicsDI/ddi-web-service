package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.util.Constants;

import java.util.List;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })
@WebAppConfiguration
public class DatasetTests {

    @Autowired
    IDatasetService datasetService;

   @Test
    public void getDatasetReadTest() {
        Dataset dataset = datasetService.read("PXD004233","Pride");
        assertNotNull(dataset);
    }

    @Test
    public void getDatasetSimilarByPubmedTest(){
        List<Dataset> similarDatasets = datasetService.getSimilarByPubmed("16585740");
        assertNotNull(similarDatasets);
    }


}
