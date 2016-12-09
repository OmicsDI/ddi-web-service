package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.config.AbstractEbeyeWsConfig;
import uk.ac.ebi.ddi.service.db.model.dataset.Dataset;
import uk.ac.ebi.ddi.service.db.repo.dataset.IDatasetRepo;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetService;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetSimilarsService;
import uk.ac.ebi.ddi.service.db.service.logger.DatasetResourceService;
import uk.ac.ebi.ddi.service.db.service.logger.HttpEventService;
import uk.ac.ebi.ddi.ws.modules.dataset.model.OmicsDataset;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 6/12/16.
 */

//@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
@EnableMongoRepositories
//@ContextConfiguration(classes = {DatasetController.class,DatasetWsClient.class, DomainWsClient.class,AbstractEbeyeWsConfig.class})
//@ContextConfiguration({"classpath:test-context.xml", "classpath:mvc-config.xml"})
public class DatasetControllerTest {

    @Mock
    private DatasetController datasetController;

    /*@Mock
    private DatasetWsClient datasetWsClient;

    @Mock
    private DatasetService datasetService;

    @Mock
    private IDatasetRepo datasetRepo;

    @Mock
    private DatasetResourceService datasetResourceService;

    @Mock
    private OmicsDataset omicsDataset;

    @Mock
    private DatasetSimilarsService datasetSimilarsService;

    @Mock
    private HttpEventService eventService;

    @Mock
    private IDatasetRepo datasetAccessRepo;
*/
    private MockMvc mockMvc;

    private static final String OMICS_DATASET = "PXD002999";
    private static final String OMICS_DATABASE = "pride";

    @Before
    public void setup() {
        //this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        // this must be called for the @Mock annotations above to be processed.
        MockitoAnnotations.initMocks(this);
        //ReflectionTestUtils.setField(datasetService, "datasetRepo", datasetRepo);
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(datasetController).build();
    }

    @Test // /{domain}/{acc}
    public void getDatasetByDomain() throws Exception
    {
        mockMvc.perform(get("/dataset/pride/PXD000210")).
                andExpect(status().isOk());

    }

    @Test
    public void testmostAccessed() throws Exception{
        mockMvc.perform(get("/dataset/mostAccessed?size=20")).andExpect(status().isOk())
        ;
    }

    @Test
    public void testGet() throws Exception{
        mockMvc.perform(get("/dataset/get?acc=PXD000210&database=pride")).andExpect(status().isOk())
                ;
    }

}
