package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 6/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@EnableMongoRepositories
public class DatasetControllerTest {

    @Mock
    private DatasetController datasetController;

    private MockMvc mockMvc;

    private static final String OMICS_DATASET = "PXD002999";
    private static final String OMICS_DATABASE = "pride";

    @Before
    public void setup() {
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
        mockMvc.perform(get("/dataset/mostAccessed?size=20")).andExpect(status().isOk());
    }

    @Test
    public void testGet() throws Exception{
        mockMvc.perform(get("/dataset/get?acc=PXD000210&database=pride")).andExpect(status().isOk());
    }

    @Test
    public void testSearch() throws Exception{
        mockMvc.perform(get("/dataset/search?query=proteins")).andExpect(status().isOk());
    }

    @Test
    public void testDataset()throws Exception{
        mockMvc.perform(get("/dataset/get?acc=PXD001416&database=pride")).andExpect(status().isOk());
    }

    @Test
    public void testSearchDataset()throws Exception{
        mockMvc.perform(get("/dataset/search?query= (tissue: (\"Liver\"))")).andExpect(status().isOk());
    }



}
