package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gaur on 13/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigClass.class})
//@ContextConfiguration({"classpath: /test-context.xml"})
//@ContextConfiguration(locations = {"/webapp-config.xml"})
@EnableMongoRepositories(basePackages="uk.ac.ebi.ddi.service.db.repo")
@WebAppConfiguration
@EnableWebMvc
public class DatasetTest {

    private MockMvc mockMvc;

    private static final String OMICS_DATASET = "PXD002999";
    private static final String OMICS_DATABASE = "pride";
    private static final String OMICS_SIZE = "20";

    @Autowired
    private WebApplicationContext wac;


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test //most accessed datasets list
    public void testCreationOfANewProjectSucceeds() throws Exception {
        mockMvc.perform(get("/dataset/mostAccessed?size=20")).
                andExpect(status().isOk());
    }


    @Test // get particular dataset
    public void getDataset() throws Exception{
        mockMvc.perform(get("/dataset/get").param("acc",OMICS_DATASET).param("database",OMICS_DATABASE)).
                andExpect(status().isOk());
    }

    @Test // /{domain}/{acc}
    public void getDatasetByDomain() throws Exception
    {
        mockMvc.perform(get("/dataset/" + OMICS_DATABASE + "/"+OMICS_DATASET)).
                andExpect(status().isOk());
    }
}

