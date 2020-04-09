package uk.ac.ebi.ddi.ws.modules.statistics.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.ddi.ws.modules.seo.controller.StructuredDataController;
import uk.ac.ebi.ddi.ws.modules.stats.controller.StatisticsController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@EnableMongoRepositories
public class StatisticsControllerTest {
    @Mock
    private StatisticsController statisticsController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    public void statisticsOrganisms() throws Exception {
        mockMvc.perform(get("/statistics/organisms")
                .param("size", "20")
                ).andExpect(status().isOk());
    }

    @Test
    public void statisticsTissues() throws Exception {
        mockMvc.perform(get("/statistics/tissues")
                .param("size", "20")
        ).andExpect(status().isOk());
    }

    @Test
    public void statisticsOmics() throws Exception {
        mockMvc.perform(get("/statistics/omics")).andExpect(status().isOk());
    }

    @Test
    public void statisticsDiseases() throws Exception {
        mockMvc.perform(get("/statistics/diseases")
                .param("size", "20")
        ).andExpect(status().isOk());
    }

    @Test
    public void statisticsDomains() throws Exception {
        mockMvc.perform(get("/statistics/domains")).andExpect(status().isOk());
    }

    @Test
    public void statisticsOmicsByYear() throws Exception {
        mockMvc.perform(get("/statistics/omicsByYear")).andExpect(status().isOk());
    }
}
