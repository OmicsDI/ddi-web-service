package uk.ac.ebi.ddi.ws.swagger;

import com.google.common.collect.Ordering;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiDescription;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.ordering.ResourceListingPositionalOrdering;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

//import com.wordnik.swagger.model.ApiDescription;
//import com.wordnik.swagger.model.ApiInfo;

/**
 * @author Yasset Perez-Riverol ypriverol@gmail.com
 *
 */
@Configuration
@EnableSwagger
@ComponentScan("uk.ac.ebi.ddi.ws.modules")
public class SwaggerConfig {

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                 .apiListingReferenceOrdering(new ResourceListingPositionalOrdering())
                .directModelSubstitute(URL.class, String.class)
                .apiDescriptionOrdering(new ApiDescriptionPositionOrdering())
                .includePatterns("/.*");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Data Discovery Index web service API",
                "Programmatic access to the Data Discovery Index data via RESTful Web Services.",
                "http://www.ebi.ac.uk/about/terms-of-use",
                "omicsdi-support@ebi.ac.uk",
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );
    }

    private class ApiDescriptionPositionOrdering extends Ordering<ApiDescription> {
        @Override
        public int compare(ApiDescription apiDescription, ApiDescription other) {
            int pos1 = apiDescription.getOperations().iterator().next().getPosition();
            int pos2 = other.getOperations().iterator().next().getPosition();
            return Integer.compare(pos1, pos2);
        }
    }

}
