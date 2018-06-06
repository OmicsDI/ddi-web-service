package uk.ac.ebi.ddi.ws.modules.database.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.ddi.ws.modules.dataset.controller.DatasetController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DatabaseControllerTest {

    @Mock
    private DatabaseController databaseController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // this must be called for the @Mock annotations above to be processed.
        MockitoAnnotations.initMocks(this);
        //ReflectionTestUtils.setField(datasetService, "datasetRepo", datasetRepo);
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(databaseController).build();
    }


}
