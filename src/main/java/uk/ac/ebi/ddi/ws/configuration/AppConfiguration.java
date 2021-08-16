package uk.ac.ebi.ddi.ws.configuration;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dictionary.DictionaryClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.facet.FacetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.publication.PublicationWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.config.EbeyeWsConfigDev;
//import uk.ac.ebi.ddi.ebe.ws.dao.config.EbeyeWsConfigProd;

@Configuration
@EnableSwagger
@EnableMongoRepositories(value = "uk.ac.ebi.ddi.service.db.repo")
@ComponentScan({"uk.ac.ebi.ddi.service.db"})
public class AppConfiguration {

    //private EbeyeWsConfigProd configProd = new EbeyeWsConfigProd();

    private EbeyeWsConfigDev configProd = new EbeyeWsConfigDev();

    @Bean
    public DatasetWsClient datasetWsClient() {
        return new DatasetWsClient(configProd);
    }

    @Bean
    public DomainWsClient domainWsClient() {
        return new DomainWsClient(configProd);
    }

    @Bean
    public FacetWsClient facetWsClient() {
        return new FacetWsClient(configProd);
    }

    @Bean
    public DictionaryClient dictionaryClient() {
        return new DictionaryClient(configProd);
    }

    @Bean
    public PublicationWsClient publicationWsClient() {
        return new PublicationWsClient(configProd);
    }
}
