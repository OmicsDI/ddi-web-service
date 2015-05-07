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
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.TermResult;
import uk.ac.ebi.ddi.service.db.model.logger.DatasetResource;
import uk.ac.ebi.ddi.service.db.model.logger.HttpEvent;
import uk.ac.ebi.ddi.service.db.service.logger.DatasetResourceService;
import uk.ac.ebi.ddi.service.db.service.logger.HttpEventService;
import uk.ac.ebi.ddi.service.db.utils.Tuple;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;

<<<<<<< HEAD
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;
=======
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetDetail;
import uk.ac.ebi.ddi.ws.modules.dataset.model.PubmedPublication;
>>>>>>> c834715ab520d8c8381c188302ffda66140e26a1
import uk.ac.ebi.ddi.ws.modules.dataset.model.Term;
import uk.ac.ebi.ddi.ws.modules.dataset.util.RepoDatasetMapper;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.WsUtilities;

<<<<<<< HEAD
import javax.servlet.http.HttpServletRequest;
=======
import uk.ac.ebi.ddi.ws.modules.dataset.util.PubmedUtil;

>>>>>>> c834715ab520d8c8381c188302ffda66140e26a1
import java.util.*;


@Api(value = "dataset", description = "Retrieve the information about the dataset including search functionalities", position = 0)
@Controller
@RequestMapping(value = "/dataset")

public class DatasetController {

    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);

    @Autowired
    DatasetWsClient dataWsClient;

    @Autowired
    private DatasetResourceService resourceService;

    @Autowired
    HttpEventService eventService;


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
            @RequestParam(value = "faceCount", required = false, defaultValue = "20") int facetCount,
            HttpServletRequest httpServletRequest) {



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
                    @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                    HttpServletRequest httpServletRequest) {

        TermResult termResult = dataWsClient.getFrequentlyTerms(Constants.PRIDE_DOMAIN, Constants.DESCRIPTION_FIELD, Constants.EXCLUSION_WORDS, size);

        return RepoDatasetMapper.asTermResults(termResult);

    }

    @ApiOperation(value = "Retrieve the latest datasets in the repository", position = 1, notes = "Retrieve the latest datasets in the repository")
    @RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult latest(
            @ApiParam(value = "Number of terms to be retrieved: maximum 100, default 20")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            HttpServletRequest httpServletRequest) {


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

    @ApiOperation(value = "Retrieve an Specific Dataset", position = 1, notes = "Retrieve an specific dataset")
    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
<<<<<<< HEAD
    DataSetResult get(
            @ApiParam(value = "Accession of the Dataset in the resource")
            @RequestParam(value = "acc", required = true) String acc,
            @ApiParam(value = "Database accession id")
            @RequestParam(value = "datatabase", required = true) String domain,
            HttpServletRequest httpServletRequest
=======
    public @ResponseBody DatasetDetail get(
            @ApiParam(value = "Accession of the Dataset in the resource")
            @RequestParam(value = "acc", required = true) String acc,
            @ApiParam(value = "Database")
            @RequestParam(value = "database", required = true) String domain
>>>>>>> c834715ab520d8c8381c188302ffda66140e26a1

    ) {
        acc = acc.replaceAll("\\s","");
        DatasetDetail datasetDetail= new DatasetDetail();
        Set<String> currentIds =  new HashSet(Arrays.asList(new String[] {acc}));
        List <PubmedPublication> pubmedPublications;

        QueryResult datasetResult = dataWsClient.getDatasetsById(domain, Constants.DATASET_DETAIL, currentIds);
        Entry[] entries = datasetResult.getEntries();
        if(entries.length<=0) return null;
        Entry entry1 = entries[0];
        Map<String, String[]> fields = entry1.getFields();

        String[] names = fields.get("name");
        String[] descriptions = fields.get("description");
        String[] publication_dates = fields.get("publication_date");
        String[] full_dataset_links = fields.get("full_dataset_link");
        String[] data_protocols = fields.get("data_protocol");
        String[] sample_protocols = fields.get("sample_protocol");
        String[] pubmedids = fields.get("PUBMED");

        datasetDetail.setId(acc);
        datasetDetail.setName(names[0]);
        datasetDetail.setDescription(descriptions[0]);
        datasetDetail.setPublicationDate(publication_dates[0]);
        datasetDetail.setData_protocol(data_protocols[0]);
        datasetDetail.setSample_protocol(sample_protocols[0]);

        if ((pubmedids!=null) && (pubmedids.length > 0)) {
            try {
                pubmedPublications = PubmedUtil.getPubmedList(pubmedids);
                datasetDetail.setPublications(pubmedPublications);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

<<<<<<< HEAD
        DatasetResource resource = resourceService.read(acc, domain);
        if(resource == null){
            resource = new DatasetResource("http://www.ebi.ac.uk/ddi/" + domain + "/" + acc,acc,domain);
            resource = resourceService.save(resource);
        }
        HttpEvent event = WsUtilities.tranformServletResquestToEvent(httpServletRequest);
        event.setResource(resource);
        eventService.save(event);

        return null;
    }

    @ApiOperation(value = "Retrieve an Specific Dataset", position = 1, notes = "Retrieve an specific dataset")
    @RequestMapping(value = "/mostAccessed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult getMostAccessed(
            @ApiParam(value = "The most accessed datasets size")
            @RequestParam(value = "size", required = true, defaultValue = "20") int size
    ) {

        DataSetResult result = new DataSetResult();
        List<DatasetSummary> datasetSummaryList = new ArrayList<DatasetSummary>();
        Map<Tuple<String, String>, Integer> mostAccesedIds = eventService.moreAccessedDatasetResource(5);
        for(Tuple<String, String> dataset: mostAccesedIds.keySet()){
            DatasetSummary datatsetSummary = new DatasetSummary();
            datatsetSummary.setId(dataset.getKey());
            datatsetSummary.setSource(dataset.getValue());
            datasetSummaryList.add(datatsetSummary);
        }
        result.setDatasets(datasetSummaryList);
        result.setCount(datasetSummaryList.size());
        return result;
    }














=======
        return datasetDetail;
    }

>>>>>>> c834715ab520d8c8381c188302ffda66140e26a1
}
