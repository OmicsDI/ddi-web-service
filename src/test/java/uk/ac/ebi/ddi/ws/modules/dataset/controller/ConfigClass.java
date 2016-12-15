package uk.ac.ebi.ddi.ws.modules.dataset.controller;

import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dictionary.DictionaryClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.config.AbstractEbeyeWsConfig;
import uk.ac.ebi.ddi.ebe.ws.dao.config.EbeyeWsConfigDev;
import uk.ac.ebi.ddi.service.db.model.logger.DatasetResource;
import uk.ac.ebi.ddi.service.db.repo.dataset.IDatasetRepo;
import uk.ac.ebi.ddi.service.db.repo.logger.IDatasetResourceRepo;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetService;
import uk.ac.ebi.ddi.service.db.service.dataset.DatasetSimilarsService;
import uk.ac.ebi.ddi.service.db.service.logger.DatasetResourceService;
import uk.ac.ebi.ddi.service.db.service.logger.HttpEventService;

import java.util.List;


/**
 * Created by gaur on 13/12/16.
 */

@Configuration
@EnableMongoRepositories(basePackages="uk.ac.ebi.ddi.service.db.repo")
@EnableWebMvc
public class ConfigClass extends WebMvcConfigurerAdapter {


    @Bean
    public EbeyeWsConfigDev ebeyeWsConfigDev(){
        return new EbeyeWsConfigDev();
    };

    @Bean
    public DatasetWsClient datasetWsClient(){
        return new DatasetWsClient(ebeyeWsConfigDev());
    }


    @Bean
    public DomainWsClient domainWsClient(){
        return new DomainWsClient(ebeyeWsConfigDev());
    }

    @Bean
    public DatasetController datasetController(){
        return new DatasetController();
    }

    @Bean
    public DatasetResourceService datasetResourceService(){
        return new DatasetResourceService();
    };

    @Bean
    public DatasetResource datasetResource(){
        return new DatasetResource();
    }

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        UserCredentials userCredentials = new UserCredentials("ddi_user", "V5f3SThe");
        return new SimpleMongoDbFactory(new Mongo("mongos-hxvm7-dev-001.ebi.ac.uk"), "ddi_db",userCredentials,"admin");
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
   /* @Bean
    public MongoFactoryBean mongo() {
        MongoFactoryBean mongo = new MongoFactoryBean();
        mongo.setHost("localhost");
        mongo.setPort(27017);
        return mongo;
    }


    public @Bean MongoTemplate mongoTemplate() throws Exception{
        return new MongoTemplate(mongo().getObject(),"mjur");
    }*/

    public @Bean
    HttpEventService httpEventService(){
        return new HttpEventService();
    }

    public @Bean
    DictionaryClient dictionaryClient()
    {
        return new DictionaryClient(ebeyeWsConfigDev());
    }

    @Bean
    public DatasetSimilarsService datasetSimilarsService(){
        return new DatasetSimilarsService();
    }

    @Bean
    public DatasetService datasetService(){
        return new DatasetService();
    }

/*    @Autowired
    public IDatasetRepo datasetAccessRepo;

    public IDatasetRepo datasetAccessRepo(){
        return datasetAccessRepo;
    }*/

    @Override
    public void configureMessageConverters( List<HttpMessageConverter<?>> converters ) {
        converters.add(converter());
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //do your customizations here...
        return converter;
    }
}
