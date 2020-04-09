package uk.ac.ebi.ddi.ws.modules.term.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.ddi.ws.modules.dataset.controller.DatasetController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
@EnableMongoRepositories
public class TermControllerTest {

    @Mock
    private TermController termController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(termController).build();
    }

    @Test
    public void getFrequentlyList() throws Exception {
        mockMvc.perform(get("/term/frequentlyTerm/list")
                        .param("size", "20")
                        .param("domain", "pride")
                        .param("field", "description")
                        )
                .andExpect(status().isOk());
    }

    @Test
    public void getTermByPattern() throws Exception {
        mockMvc.perform(get("/term/getTermByPattern")
                        .param("q", "hom")
                        .param("size", "10")
                        )
                .andExpect(status().isOk());
    }
}
