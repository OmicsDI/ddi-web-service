package uk.ac.ebi.ddi.ws.modules.seo.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.ddi.ws.modules.dataset.controller.DatasetController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@EnableMongoRepositories
public class SeoControllerTest {
    @Mock
    private StructuredDataController structuredDataController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(structuredDataController).build();
    }

    @Test
    public void seoHome() throws Exception {
        mockMvc.perform(get("/seo/home")).andExpect(status().isOk());
    }

    @Test
    public void seoSearch() throws Exception {
        mockMvc.perform(get("/seo/search")).andExpect(status().isOk());
    }

    @Test
    public void seoApi() throws Exception {
        mockMvc.perform(get("/seo/api")).andExpect(status().isOk());
    }

    @Test
    public void seoDatabase() throws Exception {
        mockMvc.perform(get("/seo/database")).andExpect(status().isOk());
    }

    @Test
    public void seoDatasetDetail() throws Exception {
        mockMvc.perform(get("/seo/dataset/pride/PXD000210")).andExpect(status().isOk());
    }

    @Test
    public void seoAbout() throws Exception {
        mockMvc.perform(get("/seo/about")).andExpect(status().isOk());
    }
}
