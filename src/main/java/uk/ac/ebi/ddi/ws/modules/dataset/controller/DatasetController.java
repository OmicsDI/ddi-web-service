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
import uk.ac.ebi.ddi.ebe.ws.dao.client.dictionary.DictionaryClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.SimilarResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.TermResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.service.db.model.logger.DatasetResource;
import uk.ac.ebi.ddi.service.db.model.logger.HttpEvent;
import uk.ac.ebi.ddi.service.db.service.logger.DatasetResourceService;
import uk.ac.ebi.ddi.service.db.service.logger.HttpEventService;
import uk.ac.ebi.ddi.service.db.utils.Tuple;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;

import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetDetail;
import uk.ac.ebi.ddi.ws.modules.dataset.model.Term;
import uk.ac.ebi.ddi.ws.modules.dataset.util.RepoDatasetMapper;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.WsUtilities;

import javax.servlet.http.HttpServletRequest;


import java.util.*;


@Api(value = "dataset", description = "Retrieve the information about the dataset including search functionalities", position = 0)
@Controller
@RequestMapping(value = "/dataset")

public class DatasetController {

    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);

    @Autowired
    DatasetWsClient dataWsClient;

    @Autowired
    DomainWsClient domainWsClient;

    @Autowired
    private DatasetResourceService resourceService;

    @Autowired
    HttpEventService eventService;

    @Autowired
    private DictionaryClient dictionaryClient;


    @ApiOperation(value = "Search for datasets in the resource", position = 1, notes = "retrieve datasets in the resource using different queries")
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult search(
            @ApiParam(value = "general search term against multiple fields including, e.g: cancer human, default is *:* ")
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @ApiParam(value = "field to sort the output of the search results, e.g:  description")
            @RequestParam(value = "sortfield", required = false, defaultValue = "") String sortfield,
            @ApiParam(value = "type of sorting ascending or descending, e.g: ascending")
            @RequestParam(value = "order", required = false, defaultValue = "") String order,
            @ApiParam(value = "the starting point for the search, e.g: 0")
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @ApiParam(value = "the number of records to be retrieved, e.g: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "the starting point for the search, e.g: 0")
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
                Set<String> currentIds = null;
                if((i+Constants.HIGH_QUERY_THRESHOLD) < taxonomyIds.size())
                    currentIds   = new HashSet<String>(list.subList(i, i+Constants.HIGH_QUERY_THRESHOLD));
                else
                    currentIds   = new HashSet<String>(list.subList(i , taxonomyIds.size()-1));

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


    @ApiOperation(value = "Search dictionary words", position = 1, notes = "retrive the words for a pattern")
    @RequestMapping(value = "/words", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    List<String> getWords(
            @ApiParam(value = "general pattern term to be search in the dictionary: hom")
            @RequestParam(value = "pattern", required = false, defaultValue = "") String pattern,
            @ApiParam(value = "the number of records to be retrieved, e.g: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
            ) {
        if(pattern.length() > 2){
            return dictionaryClient.getWordsDomains(Constants.INITIAL_DOMAINS, pattern, size);
        }
        return Collections.EMPTY_LIST;
    }

    @ApiOperation(value = "Retrieve frequently terms from the Repo", position = 1, notes = "Retrieve frequently terms from the Repo")
     @RequestMapping(value = "/terms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
     @ResponseStatus(HttpStatus.OK) // 200
     public @ResponseBody
     List<Term> frequentTerms(
                    @ApiParam(value = "Number of terms to be retrieved, e.g: maximum 100")
                    @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                    @ApiParam(value = "Repository to find the information, e.g: pride")
                    @RequestParam(value = "domain", required = true, defaultValue = "pride") String domain,
                    @ApiParam(value = "Field to search for the specific Terms, e.g: description")
                    @RequestParam(value = "field", required = true, defaultValue = "description") String field,
                    HttpServletRequest httpServletRequest) {


        if(!Constants.MAIN_DOMAIN.equalsIgnoreCase(domain)){
            DomainList domainList    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
            String[] subdomains  = WsUtilities.getSubdomainList(domainList);
            domain = WsUtilities.validateDomain(subdomains, domain);
        }


        TermResult termResult = null;

        if(domain != null)
             termResult = dataWsClient.getFrequentlyTerms(domain, field, Constants.SHORT_EXCLUSION_WORDS, size);

        return RepoDatasetMapper.asTermResults(termResult);

    }

    @ApiOperation(value = "Retrieve the latest datasets in the repository", position = 1, notes = "Retrieve the latest datasets in the repository")
    @RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult latest(
            @ApiParam(value = "Number of terms to be retrieved, e.g : maximum 100, default 20")
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
    public @ResponseBody
    DatasetDetail get(
            @ApiParam(value = "Accession of the Dataset in the resource, e.g : PXD000210")
            @RequestParam(value = "acc", required = true) String acc,
            @ApiParam(value = "Database accession id, e.g: pride")
            @RequestParam(value = "database", required = true) String domain,
            HttpServletRequest httpServletRequest){

        acc = acc.replaceAll("\\s","");

        DatasetDetail datasetDetail= new DatasetDetail();
        Set<String> currentIds =  new HashSet(Arrays.asList(new String[] {acc}));


        QueryResult datasetResult = dataWsClient.getDatasetsById(domain, Constants.DATASET_DETAIL, currentIds);
        Entry[] entries = datasetResult.getEntries();

        if(!(entries.length<=0)){

            Entry entry1 = entries[0];
            Map<String, String[]> fields = entry1.getFields();

            datasetDetail.setId(acc);

            datasetDetail.setSource(entry1.getSource());

            String[] names = fields.get(Constants.NAME_FIELD);
            datasetDetail.setName(names[0]);

            String[] descriptions = fields.get(Constants.DESCRIPTION_FIELD);
            datasetDetail.setDescription(descriptions[0]);

            String[] publication_dates = fields.get(Constants.PUB_DATE_FIELD);
            if(publication_dates != null && publication_dates.length > 0 && publication_dates[0] != null)
                   datasetDetail.setPublicationDate(publication_dates[0]);

            String[] data_protocols = fields.get(Constants.DATA_PROTOCOL_FIELD);
            datasetDetail.addProtocols(Constants.DATA_PROTOCOL_FIELD, data_protocols);

            String[] sample_protocols = fields.get(Constants.SAMPLE_PROTOCOL_FIELD);
            datasetDetail.addProtocols(Constants.SAMPLE_PROTOCOL_FIELD, sample_protocols);

            String[] full_dataset_links = fields.get(Constants.DATASET_LINK_FIELD);
            if(full_dataset_links != null && full_dataset_links.length > 0){
                datasetDetail.setFull_dataset_link(full_dataset_links[0]);
            }

            String[] diseases = fields.get(Constants.DISEASE_FIELD);
            if(diseases != null && diseases.length > 0){
                datasetDetail.setDiseases(diseases);
            }

            String[] tissues = fields.get(Constants.TISSUE_FIELD);
            if(tissues != null && tissues.length > 0){
                datasetDetail.setTissues(tissues);
            }

            String[] instruments = fields.get(Constants.INSTRUMENT_FIELD);
            datasetDetail.setArrayInstruments(instruments);

            String[] experiment_type = fields.get(Constants.EXPERIMENT_TYPE_FIELD);
            datasetDetail.setArrayExperimentType(experiment_type);

            String[] pubmedids = fields.get(Constants.PUBMED_FIELD);
            if ((pubmedids!=null) && (pubmedids.length > 0)) {
                datasetDetail.setArrayPublicationIds(pubmedids);
            }

            String[] submitterKeys = fields.get(Constants.SUBMITTER_KEY_FIELD);
            String[] curatorKeys   = fields.get(Constants.CURATOR_KEY_FIELD);
            datasetDetail.setKeywords(submitterKeys, curatorKeys);

            Set<String> taxonomyIds    = RepoDatasetMapper.getTaxonomyIds(datasetResult);

            QueryResult taxonomies = null;

            if(taxonomyIds.size() > 0){
                taxonomies   = dataWsClient.getDatasetsById(Constants.TAXONOMY_DOMAIN, Constants.TAXONOMY_FIELDS, taxonomyIds);
            }


            datasetDetail = RepoDatasetMapper.addTaxonomy(datasetDetail, taxonomies);
            /**
             * Trace the access to the dataset
             */
            DatasetResource resource = resourceService.read(acc, domain);
            if(resource == null){
                resource = new DatasetResource("http://www.ebi.ac.uk/Tools/ddi/" + domain + "/" + acc,acc,domain);
                resource = resourceService.save(resource);
            }
            HttpEvent event = WsUtilities.tranformServletResquestToEvent(httpServletRequest);
            event.setResource(resource);
            eventService.save(event);
        }

        return datasetDetail;

    }

    @ApiOperation(value = "Retrieve an Specific Dataset", position = 1, notes = "Retrieve an specific dataset")
    @RequestMapping(value = "/mostAccessed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult getMostAccessed(
            @ApiParam(value = "The most accessed datasets size, e.g: 20")
            @RequestParam(value = "size", required = true, defaultValue = "20") int size
    ) {

        DataSetResult result = new DataSetResult();
        List<DatasetSummary> datasetSummaryList = new ArrayList<DatasetSummary>();
        Map<Tuple<String, String>, Integer> mostAccesedIds = eventService.moreAccessedDatasetResource(size);
        Map<String, Set<String>> currentIds = new HashMap<String, Set<String>>();

        for(Tuple<String, String> dataset: mostAccesedIds.keySet()){
            Set<String> ids = currentIds.get(dataset.getValue());
            if(ids == null)
                ids = new HashSet<String>();
            ids.add(dataset.getKey());
            currentIds.put(dataset.getValue(), ids);
        }
        for(String domain: currentIds.keySet()){
            QueryResult datasetResult = dataWsClient.getDatasetsById(domain, Constants.DATASET_DETAIL, currentIds.get(domain));
            datasetSummaryList.addAll(WsUtilities.transformDatasetSummary(datasetResult,domain, mostAccesedIds));
        }
        result.setDatasets(datasetSummaryList);
        result.setCount(datasetSummaryList.size());

        return result;
    }


    @ApiOperation(value = "Retrieve the related datasets to one Dataset", position = 1, notes = "Retrieve the related datasets to one Dataset")
    @RequestMapping(value = "/moreLikeThis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    DataSetResult moreLikeThis(
            @ApiParam(value = "Accession of the Dataset in the resource, e.g : PXD000210")
            @RequestParam(value = "acc", required = true) String acc,
            @ApiParam(value = "Database accession id, e.g : pride")
            @RequestParam(value = "database", required = true) String domain
    ) {

        SimilarResult queryResult = dataWsClient.getSimilarProjects(domain, acc, Constants.MORELIKE_FIELDS);

        DataSetResult result = new DataSetResult();
        List<DatasetSummary> datasetSummaryList = new ArrayList<DatasetSummary>();

        Map<String, Set<String>> currentIds = new HashMap<String, Set<String>>();


        if(queryResult != null && queryResult.getEntries() != null && queryResult.getEntries().length > 0){

            for(Entry entry: queryResult.getEntries()){
                if(entry.getId() != null && entry.getSource() != null){
                    Set<String> ids = currentIds.get(entry.getSource());
                    if(ids == null)
                        ids = new HashSet<String>();
                    if(!(entry.getId().equalsIgnoreCase(acc) && entry.getSource().equalsIgnoreCase(domain)))
                        ids.add(entry.getId());
                    currentIds.put(entry.getSource(), ids);
                }
            }

            for(String currentDomain: currentIds.keySet()){
                QueryResult datasetResult = dataWsClient.getDatasetsById(currentDomain, Constants.DATASET_DETAIL, currentIds.get(currentDomain));
                datasetSummaryList.addAll(WsUtilities.transformDatasetSummary(datasetResult,currentDomain, null));
            }

            result.setDatasets(datasetSummaryList);
            result.setCount(datasetSummaryList.size());

            return result;
        }

        return null;
    }


    @ApiOperation(value = "Retrieve all file links for a given dataset", position = 1, notes = "Retrieve all file links for a given dataset")
    @RequestMapping(value = "/getFileLinks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    List<String> getFileLinks(
            @ApiParam(value = "Accession of the Dataset in the resource, e.g : PXD000210")
            @RequestParam(value = "acc", required = true) String acc,
            @ApiParam(value = "Database accession id, e.g : pride")
            @RequestParam(value = "database", required = true) String domain
    ) {
        List<String> files = new ArrayList<String>();

        String[] fields = {
                Constants.DATASET_FILE
        };

        Set<String> currentIds =  new HashSet(Arrays.asList(new String[] {acc}));

        QueryResult datasetResult = dataWsClient.getDatasetsById(domain, fields, currentIds);

        if(datasetResult != null && datasetResult.getEntries() != null &&
                datasetResult.getEntries().length > 0){
            Entry entry = datasetResult.getEntries()[0];
            String[] fileNames = entry.getFields().get(Constants.DATASET_FILE);
            if(fileNames != null && fileNames.length > 0){
                for(String fileName: fileNames)
                    if(fileName != null && fileNames.length > 0 )
                        files.add(fileName);
            }
        }
        return files;
    }


}
