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
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.TermResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;

import uk.ac.ebi.ddi.ws.modules.dataset.model.Term;
import uk.ac.ebi.ddi.ws.modules.dataset.util.RepoDatasetMapper;
import uk.ac.ebi.ddi.ws.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
            @ApiParam(value = "general search term against multiple fields including: cancer human, default is *:* ")
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @ApiParam(value = "field to sort the output of the search results: e.g description")
            @RequestParam(value = "sortfield", required = false, defaultValue = "") String sortfield,
            @ApiParam(value = "type of sorting ascending or descending: default: ascending")
            @RequestParam(value = "order", required = false, defaultValue = "") String order,
            @ApiParam(value = "the starting point for the search: 0")
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @ApiParam(value = "the number of records to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "the starting point for the search: 0")
            @RequestParam(value = "faceCount", required = false, defaultValue = "20") int facetCount) {

        query = (query == null || query.isEmpty() || query.length() == 0)? "*:*": query;

        QueryResult queryResult = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, query, Constants.DATASET_SUMMARY, sortfield, order, start, size, facetCount);

        QueryResult taxonomies = null;

        Set<String> taxonomyIds    = RepoDatasetMapper.getTaxonomyIds(queryResult);
        /*
         * The number of queries should be controlled using the maximun QUERY threshold in this case 100 entries for the EBE web service.
         */

        if(taxonomyIds.size() > Constants.HIGH_QUERY_THRESHOLD){
            List<QueryResult> resultList = new ArrayList<QueryResult>();
            List<String> list = new ArrayList<String>(taxonomyIds);
            int count = 0;
            for(int i=0 ; i < taxonomyIds.size(); i += Constants.HIGH_QUERY_THRESHOLD){
               Set<String> currentIds = new HashSet<String>(list.subList(i, Constants.HIGH_QUERY_THRESHOLD));
               resultList.add(dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, currentIds));
               count = i;
            }
            Set<String> currentIds = new HashSet<String>(list.subList(count, taxonomyIds.size()-1));
            resultList.add(dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, currentIds));
            taxonomies = RepoDatasetMapper.mergeQueryResult(resultList);

        }else if(taxonomyIds.size() > 0){
           taxonomies   = dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, taxonomyIds);
        }

        return RepoDatasetMapper.asDataSummary(queryResult, taxonomies);

    }

    @ApiOperation(value = "Retrieve frequently terms from the Repo", position = 1, notes = "Retrieve frequently terms from the Repo")
     @RequestMapping(value = "/terms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
     @ResponseStatus(HttpStatus.OK) // 200
     public @ResponseBody
     List<Term> frequentTerms(
                    @ApiParam(value = "Number of terms to be retrieved: maximum 100")
                    @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        TermResult termResult = dataWsClient.getFrequentlyTerms(Constants.MAIN_DOMAIN, Constants.DESCRIPTION_FIELD, Constants.EXCLUSION_WORDS, size);

        return RepoDatasetMapper.asTermResults(termResult);

    }

    @ApiOperation(value = "Retrieve the latest datasets in the repository", position = 1, notes = "Retrieve the latest datasets in the repository")
    @RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult latest(
            @ApiParam(value = "Number of terms to be retrieved: maximum 100, default 20")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {


        String query = "*:*";

        QueryResult queryResult = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, query, Constants.DATASET_SUMMARY, Constants.PUB_DATE_FIELD, "descending", 0, size, 10);

        QueryResult taxonomies = null;

        Set<String> taxonomyIds    = RepoDatasetMapper.getTaxonomyIds(queryResult);
        /*
         * The number of queries should be controlled using the maximun QUERY threshold in this case 100 entries for the EBE web service.
         */

        if(taxonomyIds.size() > Constants.HIGH_QUERY_THRESHOLD){
            List<QueryResult> resultList = new ArrayList<QueryResult>();
            List<String> list = new ArrayList<String>(taxonomyIds);
            int count = 0;
            for(int i=0 ; i < taxonomyIds.size(); i += Constants.HIGH_QUERY_THRESHOLD){
                Set<String> currentIds = new HashSet<String>(list.subList(i, Constants.HIGH_QUERY_THRESHOLD));
                resultList.add(dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, currentIds));
                count = i;
            }
            Set<String> currentIds = new HashSet<String>(list.subList(count, taxonomyIds.size()-1));
            resultList.add(dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, currentIds));
            taxonomies = RepoDatasetMapper.mergeQueryResult(resultList);

        }else if(taxonomyIds.size() > 0){
            taxonomies   = dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, taxonomyIds);
        }

        return RepoDatasetMapper.asDataSummary(queryResult, taxonomies);

    }




}
