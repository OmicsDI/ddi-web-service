package uk.ac.ebi.ddi.ws.modules.stats.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.ebe.ws.dao.model.europmc.Citation;
import uk.ac.ebi.ddi.service.db.model.similarity.Citations;
import uk.ac.ebi.ddi.service.db.service.similarity.CitationService;
import uk.ac.ebi.ddi.ws.modules.dataset.controller.DatasetController;
import uk.ac.ebi.ddi.ws.modules.dataset.model.OmicsDataset;
import uk.ac.ebi.ddi.ws.util.Constants;

/**
 * Created by gaur on 29/08/17.
 */
@Api(value = "scores", description = "Retrieve the information about the dataset scores", position = 0)
@Controller
@RequestMapping(value = "/scores")
public class ScoresController {

    private static final Logger logger = LoggerFactory.getLogger(ScoresController.class);

    private CitationService citationService;

    @ApiOperation(value = "Retrieve an Specific Dataset Citation Count", position = 1, notes = "Retrieve an specific dataset citation count")
    @RequestMapping(value = "/{domain}/{acc}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    Citations getDataset(
            @ApiParam(value = "Accession of the Dataset in the resource, e.g : E-TIGR-123")
            @PathVariable(value = "acc") String acc,
            @ApiParam(value = "Database accession id, e.g: arrayexpress_repository")
            @PathVariable(value = "domain") String domain){
        String database = Constants.Database.retriveAnchorName(domain);
        return citationService.read(acc,database);
    }

}
