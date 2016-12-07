package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.config.AbstractEbeyeWsConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 6/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DatasetController.class,DatasetWsClient.class, DomainWsClient.class,AbstractEbeyeWsConfig.class})
//@ContextConfiguration({"classpath:src/test/resources/test-context.xml", "classpath:src/mvc-config.xml"})
public class DatasetControllerTest {

    @Autowired
    private DatasetController datasetController;

    @Autowired
    private DatasetWsClient datasetWsClient;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final String OMICS_DATASET = "PXD002999";
    private static final String OMICS_DATABASE = "pride";

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        datasetController = new DatasetController();

        // this must be called for the @Mock annotations above to be processed.
        MockitoAnnotations.initMocks(this);
    }

    @Test // /{domain}/{acc}
    public void getDatasetByDomain() throws Exception
    {
        mockMvc.perform(get("/OMICS_DATASET/OMICS_DATABASE")).
                andExpect(status().isOk());
    }
}
