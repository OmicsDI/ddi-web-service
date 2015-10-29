package uk.ac.ebi.ddi.ws.modules.enrichment;

/**
 * Created by mingze on 27/10/15.
 */

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.service.db.model.enrichment.DatasetEnrichmentInfo;
import uk.ac.ebi.ddi.service.db.service.enrichment.IEnrichmentInfoService;
import uk.ac.ebi.ddi.service.db.service.logger.HttpEventService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(value = "enrichment", description = "Retrieve the information about the enrichment and synonyms ", position = 0)
@Controller
@RequestMapping(value = "/enrichment")

public class EnrichmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);

    @Autowired
    IEnrichmentInfoService enrichmentService;

    @ApiOperation(value = "get enrichment Info", position = 1, notes = "retrieve the enrichment data for a dataset")
    @RequestMapping(value = "/getEnrichmentInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DatasetEnrichmentInfo getEnrichmentInfo(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD002287") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database
    ) {
        Page<DatasetEnrichmentInfo> enrichedInfos = enrichmentService.readAll(0, 2);
        return enrichmentService.readByAccession(accession, database);
    }
}
