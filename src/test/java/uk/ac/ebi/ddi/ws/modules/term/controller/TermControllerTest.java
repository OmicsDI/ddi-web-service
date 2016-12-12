package uk.ac.ebi.ddi.ws.modules.term.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by gaur on 12/12/16.
 */
public class TermControllerTest {

    @Mock
    private TermController termController;

    MockMvc mockMvc;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(termController).build();
    }

    @Test
    public void getFrequentTerm() throws  Exception{
        mockMvc.perform(get("/term/getTermByPattern?q=hom&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    public void getTermPattern() throws Exception{
        mockMvc.perform(get("/term/frequentlyTerm/list?size=20&domain=pride&field=description"))
                .andExpect(status().isOk());
    }

}
