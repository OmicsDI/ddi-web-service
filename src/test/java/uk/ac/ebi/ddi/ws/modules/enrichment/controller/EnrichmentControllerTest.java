package uk.ac.ebi.ddi.ws.modules.enrichment.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Created by gaur on 12/12/16.
 */
public class EnrichmentControllerTest {
    @Mock
    private EnrichmentController enrichmentController;

    private MockMvc mockMvc;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(enrichmentController).build();
    }

    @Test
    public void getSimilarDatasets() throws Exception{
        mockMvc.perform(get("/enrichment/getSimilarDatasetsByBiologicalData?accession=PXD000002&database=PRIDE"))
                .andExpect(status().isOk());
    }

    @Test
    public void getSimilarityInfo() throws Exception{
        mockMvc.perform(get("/enrichment/getSimilarityInfo?accession=PXD000002&database=PRIDE&threshold=0.50"))
                .andExpect(status().isOk());
    }

    @Test
    public void getSynonyms() throws Exception {
        mockMvc.perform(get("/enrichment/getSynonymsForDataset?accession=PXD002287&database=PRIDE"))
                .andExpect(status().isOk());
    }

    @Test
    public void getEnrichmentInfo() throws Exception {
        mockMvc.perform(get("/enrichment/getSynonymsForDataset?accession=PXD002287&database=PRIDE"))
                .andExpect(status().isOk());
    }
}
