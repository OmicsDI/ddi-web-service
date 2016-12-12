package uk.ac.ebi.ddi.ws.modules.publication.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 12/12/16.
 */
public class PublicationControllerTest {

    @Mock
    private PublicationController publicationController;

    MockMvc mockMvc;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(publicationController).build();
    }

    @Test
    public void getPublications() throws Exception{
        mockMvc.perform(get("/publication/list?acc=PXD000210"))
                .andExpect(status().isOk());
    }


}
