package uk.ac.ebi.ddi.ws.modules.dataset.controller;

/**
 * @author Yasset Perez-Riverol ypriverol
 */

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.result.QueryResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;

import uk.ac.ebi.ddi.ws.modules.dataset.util.RepoDatasetMapper;
import uk.ac.ebi.ddi.ws.util.Constants;

import java.util.List;

@Api(value = "dataset", description = "Retrieve the information about the dataset including search functionalities", position = 0)
@Controller
@RequestMapping(value = "/dataset")

public class DatasetController {

    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);

    @Autowired
    DatasetWsClient dataWsClient;

    @ApiOperation(value = "Search for datasets in the resource", position = 1, notes = "retrieve datasets in the resource using different queries")
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult search(
            @ApiParam(value = "general search term against multiple fields including: cancer human")
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @ApiParam(value = "the starting point for the search: 0")
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @ApiParam(value = "the number of records to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "the starting point for the search: 0")
            @RequestParam(value = "start", required = false, defaultValue = "20") int facetCount) {

        QueryResult queryResult = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, query, Constants.DATASET_SUMMARY, start, size, facetCount);

        return RepoDatasetMapper.asDataSummary(queryResult);

    }
}
