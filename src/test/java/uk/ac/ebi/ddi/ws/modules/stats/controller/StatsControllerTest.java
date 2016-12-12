package uk.ac.ebi.ddi.ws.modules.stats.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 12/12/16.
 */
public class StatsControllerTest {

    @Mock
    private StatisticsController statisticsController;

    MockMvc mockMvc;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    public void getOrganisms() throws  Exception{
        mockMvc.perform(get("/statistics/organisms?size=20"))
                .andExpect(status().isOk());
    }

    @Test
    public void getTissuesStats() throws  Exception{
        mockMvc.perform(get("/statistics/tissues?size=20"))
                .andExpect(status().isOk());
    }

    @Test
    public void getOmicsStats() throws Exception{
        mockMvc.perform(get("/statistics/omics"))
                .andExpect(status().isOk());
    }

    @Test
    public void getDiseasesStats() throws Exception{
        mockMvc.perform(get("/statistics/diseases?size=20"))
                .andExpect(status().isOk());
    }

    @Test
    public void getDomainsStats() throws Exception{
        mockMvc.perform(get("/statistics/domains"))
                .andExpect(status().isOk());
    }

    @Test
    public void getOmicsByYearStats() throws Exception{
        mockMvc.perform(get("/statistics/omicsByYear"))
                .andExpect(status().isOk());
    }

    @Test
    public void getGeneralStatistics() throws Exception{
        mockMvc.perform(get("/statistics/general"))
                .andExpect(status().isOk());
    }
}
