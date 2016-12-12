package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 6/12/16.
 */

public class DatasetControllerTest {

    @Mock
    private DatasetController datasetController;

    private MockMvc mockMvc;

    private static final String OMICS_DATASET = "PXD002999";
    private static final String OMICS_DATABASE = "pride";
    private static final String OMICS_SIZE = "20";

    @Before
    public void setup() {

        // this must be called for the @Mock annotations above to be processed.
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(datasetController).build();
    }

    @Test // /{domain}/{acc}
    public void getDatasetByDomain() throws Exception
    {
        mockMvc.perform(get("/dataset/" + OMICS_DATABASE + "/"+OMICS_DATASET)).
                andExpect(status().isOk());
    }

    @Test// /dataset/mostaccessed?size
    public void getMostAccessed() throws Exception{
        mockMvc.perform(get("/dataset/mostAccessed?size=" + OMICS_SIZE))
                .andExpect(status().isOk());
    }

    @Test // /dataset/get?acc={}&database={}
    public void getDataset() throws Exception{
        mockMvc.perform(get("/dataset/get?acc=" + OMICS_DATASET + "&"+ "database=" + OMICS_DATABASE))
                .andExpect(status().isOk());
    }

    @Test
    public void getLatest() throws Exception{
        mockMvc.perform(get("/dataset/latest?size=" + OMICS_SIZE))
                .andExpect(status().isOk());
    }

    @Test
    public void searchDatasets() throws Exception{
        mockMvc.perform(get("/dataset/search?query=*%3A*&sortfield=id&order=ascending&start=0&size=20&faceCount=20"))
                .andExpect(status().isOk());
    }

    @Test
    public void getFileLinks() throws Exception{
        mockMvc.perform(get("/dataset/getFileLinks?acc=" + OMICS_DATASET + "&database=" + OMICS_DATABASE))
                .andExpect(status().isOk());
    }

    @Test
    public void getSimilar() throws Exception{
        mockMvc.perform(get("/dataset/getSimilar?acc=" + OMICS_DATASET + "&database=" + OMICS_DATABASE))
                .andExpect(status().isOk());
    }

}
