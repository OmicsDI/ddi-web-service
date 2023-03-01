package uk.ac.ebi.ddi.ws.modules.stats.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.facet.FacetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Facet;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.FacetValue;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.IndexInfo;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.ebe.ws.dao.model.facet.FacetList;
import uk.ac.ebi.ddi.ws.modules.stats.model.DomainStats;
import uk.ac.ebi.ddi.ws.modules.stats.model.StatOmicsRecord;
import uk.ac.ebi.ddi.ws.modules.stats.model.StatRecord;
import uk.ac.ebi.ddi.ws.modules.stats.util.RepoStatsToWsStatsMapper;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.WsUtilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for accessing the statistics
 *
 * @author ypriverol Yasset Perez-Riverol
*/
@Api(value = "statistics", description = "retrieve statistics about the DDI repositories, access, etc")
@Controller
@RequestMapping(value = "/statistics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StatisticsController {

    @Autowired
    DomainWsClient domainWsClient;

    @Autowired
    DatasetWsClient dataWsClient;

    @Autowired
    FacetWsClient facetWsClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);

    @Value("${app.version}")
    private String version;

    @ApiOperation(value = "Return statistics about the number of datasets per Repository", position = 1,
            notes = "Return statistics about the number of datasets per Repository")
    @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<DomainStats> getDomainEntries(
            @ApiParam(value = "domain for search results, e.g : ")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain
    ) {
        List<DomainStats> domainStats = new ArrayList<>();
        try {
            String order = Constants.ORDER_ASCENDING;
            int start = 0;
            int size = 1;
            int facetCount = 25;
            DomainList domainList;
            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
            } else {
                domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
            }
            String privateModelQuery = "(isprivate:true)";
            QueryResult queryResultOfPrivateModel;
            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                queryResultOfPrivateModel = dataWsClient.getDatasets(Constants.MODELEXCHANGE_DOMAIN,
                        privateModelQuery,
                        Constants.DATASET_SUMMARY, null, order, start, size, facetCount);
            } else {
                queryResultOfPrivateModel = dataWsClient.getDatasets(Constants.MAIN_DOMAIN,
                        privateModelQuery,
                        Constants.DATASET_SUMMARY, null, order, start, size, facetCount);
            }
            Integer privateCount = queryResultOfPrivateModel.getCount();
            IndexInfo biomodelInfo = Arrays.stream(domainList.getList()).filter(r -> r.getName().
                    equals(Constants.BIOMODELS)).findFirst().get().getIndexInfo()[0];
            Integer modelCount = Integer.parseInt(biomodelInfo.getValue());
            Integer nonPrivateCount = modelCount - privateCount;
            biomodelInfo.setValue(nonPrivateCount.toString());
            domainStats = RepoStatsToWsStatsMapper.asDomainStatsList(domainList);
        } catch (Exception ex) {
            LOGGER.error("error in domain api of Statistic controller", ex.getMessage());
        }
        return domainStats;
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Organisms", position = 1,
            notes = "Return statistics about the number of datasets per Organisms")
    @RequestMapping(value = "/organisms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getTaxonomies(
            @ApiParam(value = "Organisms to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "domain to find the information, e.g: omics")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain) {

        DomainList domainList = null;
        if (!domain.equals(Constants.MAIN_DOMAIN)) {
            domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
        } else {
            domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        }
        String[] dubdomains  = WsUtilities.getSubdomainList(domainList);
        FacetList taxonomies = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, dubdomains, DSField.CrossRef.TAXONOMY.key(), 100);
        return RepoStatsToWsStatsMapper.asFacetCount(taxonomies, DSField.CrossRef.TAXONOMY.key());
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Tissue", position = 1,
            notes = "Return statistics about the number of datasets per Tissue")
    @RequestMapping(value = "/tissues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getTissues(
            @ApiParam(value = "Tissues to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "domain to find the information, e.g: omics")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain) {
        DomainList domainList = null;
        if (!domain.equals(Constants.MAIN_DOMAIN)) {
            domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
        } else {
            domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        }
        String[] dubdomains  = WsUtilities.getSubdomainList(domainList);
        FacetList tissues = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, dubdomains, DSField.Additional.TISSUE_FIELD.key(), size);
        return RepoStatsToWsStatsMapper.asFacetCount(tissues, DSField.Additional.TISSUE_FIELD.key());
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Omics Type", position = 1,
            notes = "Return statistics about the number of datasets per Omics Type")
    @RequestMapping(value = "/omics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getOmics(
            @ApiParam(value = "domain to find the information, e.g: omics")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain) {


        List<StatRecord> statRecords = new ArrayList<StatRecord>();
        try {
            DomainList domainList = null;
            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
            } else {
                domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
            }
            String[] dubdomains = WsUtilities.getSubdomainList(domainList);
            FacetList omics = facetWsClient.getFacetEntriesByDomains(
                    Constants.MAIN_DOMAIN, dubdomains, DSField.Additional.OMICS.key(), 100);
            statRecords = RepoStatsToWsStatsMapper.asFacetCount(omics, DSField.Additional.OMICS.key());
        } catch (Exception ex) {
            LOGGER.error("exception in omics api in statistic controller", ex.getMessage());
        }
        return statRecords;
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Omics Type", position = 1,
            notes = "Return statistics about the number of datasets per Omics Type")
    @RequestMapping(value = "/modelexchange ", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getModelExchange() {

        DomainList domain = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
        String[] dubdomains  = WsUtilities.getSubdomainList(domain);
        FacetList omics = facetWsClient.getFacetEntriesByDomains(
                Constants.MODELEXCHANGE_DOMAIN, dubdomains, DSField.Additional.OMICS.key(), 100);
        return RepoStatsToWsStatsMapper.asFacetCount(omics, DSField.Additional.OMICS.key());
    }
    @ApiOperation(value = "Return statistics about the number of datasets per dieases", position = 1,
             notes = "Return statistics about the number of datasets per diseases")
    @RequestMapping(value = "/diseases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getDiseases(
            @ApiParam(value = "Dieseases to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "domain to find the information, e.g: omics")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain) {

        DomainList domainList = null;
         if (!domain.equals(Constants.MAIN_DOMAIN)) {
             domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
         } else {
             domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
         }

        String[] subdomains  = WsUtilities.getSubdomainList(domainList);
        FacetList diseases =
                facetWsClient.getFacetEntriesByDomains(
                        Constants.MAIN_DOMAIN, subdomains, DSField.Additional.DISEASE_FIELD.key(), size);
        return RepoStatsToWsStatsMapper.asFacetCount(diseases, DSField.Additional.DISEASE_FIELD.key());
    }

    @ApiOperation(value = "Return statistics about the number of datasets per repository", position = 1,
            notes = "Return statistics about the number of datasets per diseases")
    @RequestMapping(value = "/repositories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getRepositories(
            @ApiParam(value = "Repository to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @ApiParam(value = "domain to find the information, e.g: omics")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain) {

        DomainList domainList = null;
        if (!domain.equals(Constants.MAIN_DOMAIN)) {
            domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
        } else {
            domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        }

        String[] subdomains  = WsUtilities.getSubdomainList(domainList);
        FacetList repository =
                facetWsClient.getFacetEntriesByDomains(
                        Constants.MAIN_DOMAIN, subdomains, DSField.Additional.REPOSITORY.key(), size);

        String privateModelQuery = "(isprivate:true)";
        QueryResult queryResultOfPrivateModel;
        if (!domain.equals(Constants.MAIN_DOMAIN)) {
            queryResultOfPrivateModel = dataWsClient.getDatasets(Constants.MODELEXCHANGE_DOMAIN,
                    privateModelQuery,
                    Constants.DATASET_SUMMARY, null, Constants.ORDER_ASCENDING, 0, size, 100);
        } else {
            queryResultOfPrivateModel = dataWsClient.getDatasets(Constants.MAIN_DOMAIN,
                    privateModelQuery,
                    Constants.DATASET_SUMMARY, null, Constants.ORDER_ASCENDING, 0, size, 100);
        }
        Integer privateCount = queryResultOfPrivateModel.getCount();
        FacetValue facetValue = Arrays.stream(repository.getFacets()[0].getFacetValues()).
                filter(r -> r.getLabel().equals(Constants.BIOMODELS)).findFirst().get();
        Integer modelCount = Integer.parseInt(facetValue.getCount());
        Integer nonPrivateCount = modelCount - privateCount;
        facetValue.setCount(nonPrivateCount.toString());


        return RepoStatsToWsStatsMapper.asFacetCount(repository, DSField.Additional.REPOSITORY.key());
    }

    @ApiIgnore
    @ApiOperation(value = "Get current webservice version", position = 1, notes = "Get current webservice version")
    @RequestMapping(value = "/version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public Map<String, String> getVersion() {
        Map<String, String> res = new HashMap<>();
        res.put("version", version);
        return res;
    }

    @ApiOperation(value = "Return General statistics about the Services", position = 1,
            notes = "Return General statistics about the Services")
    @RequestMapping(value = "/general", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getGeneral(
            @ApiParam(value = "domain to find the information, e.g: omics")
            @RequestParam(value = "domain", required = false, defaultValue = "omics") String domain
    ) {
        List<StatRecord> resultStat = new ArrayList<>();

        try {
            DomainList domainList = null;
            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                domainList = domainWsClient.getDomainByName(Constants.MODELEXCHANGE_DOMAIN);
            } else {
                domainList = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
            }
            String[] subdomains = WsUtilities.getSubdomainList(domainList);

            resultStat.add(new StatRecord("Different Repositories/Databases",
                    String.valueOf(subdomains.length), null));
            Integer numberOfDatasets = WsUtilities.getNumberofEntries(domainList);
            resultStat.add(new StatRecord("Different Datasets", String.valueOf(numberOfDatasets), null));

            FacetList facet;
            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                facet = facetWsClient.getFacetEntriesByDomains(
                        Constants.MODELEXCHANGE_DOMAIN, subdomains, DSField.Additional.DISEASE_FIELD.key(), 100);
            } else {
                facet = facetWsClient.getFacetEntriesByDomains(
                        Constants.MAIN_DOMAIN, subdomains, DSField.Additional.DISEASE_FIELD.key(), 100);
            }

            if (facet.getFacets() != null && facet.getFacets().length > 0 && facet.getFacets()[0] != null
                    && facet.getFacets()[0].getFacetValues() != null) {
                resultStat.add(new StatRecord("Different Diseases",
                        String.valueOf(facet.getFacets()[0].getTotal()), null));
            }

            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                facet = facetWsClient.getFacetEntriesByDomains(
                        Constants.MODELEXCHANGE_DOMAIN, subdomains, DSField.Additional.TISSUE_FIELD.key(), 100);
            } else {
                facet = facetWsClient.getFacetEntriesByDomains(
                        Constants.MAIN_DOMAIN, subdomains, DSField.Additional.TISSUE_FIELD.key(), 100);
            }
            if (facet.getFacets() != null && facet.getFacets().length > 0 && facet.getFacets()[0] != null
                    && facet.getFacets()[0].getFacetValues() != null) {
                resultStat.add(new StatRecord("Different Tissues",
                        String.valueOf(facet.getFacets()[0].getTotal()), null));
            }

            if (!domain.equals(Constants.MAIN_DOMAIN)) {
                facet = facetWsClient.getFacetEntriesByDomains(
                        Constants.MODELEXCHANGE_DOMAIN, subdomains, DSField.CrossRef.TAXONOMY.key(), 100);
            } else {
                facet = facetWsClient.getFacetEntriesByDomains(
                        Constants.MAIN_DOMAIN, subdomains, DSField.CrossRef.TAXONOMY.key(), 100);
            }

            if (facet.getFacets() != null && facet.getFacets().length > 0 && facet.getFacets()[0] != null
                    && facet.getFacets()[0].getFacetValues() != null) {
                resultStat.add(new StatRecord("Different Species/Organisms",
                        String.valueOf(facet.getFacets()[0].getTotal()), null));
            }
        } catch (Exception ex) {
            LOGGER.error("exception in latest api in data controller", ex.getMessage());
        }
        return resultStat;
    }

    @ApiOperation(value = "Return statistics about the number of datasets By Omics type on recent 5 years ",
            position = 1, notes = "Return statistics about the number of datasets per OmicsType on recent 5 years ")
    @RequestMapping(value = "/omicsByYear", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatOmicsRecord> getOmicsByYear() {

        List<StatOmicsRecord> resultStat = new ArrayList<>();

        // String sortfield = Constants.DESCRIPTION_FIELD;
        ///Todo: We need to update the fields that can be sorted
        String order = Constants.ORDER_ASCENDING;
        int start = 0;
        int size = 1;
        int facetCount = 25;

        String proteomicsQuery   =    "*:* AND omics_type:\"Proteomics\"";
        String metabolomicsQuery =    "*:* AND omics_type:\"Metabolomics\"";
        String genomicsQuery     =    "*:* AND omics_type:\"Genomics\"";
        String transcriptomicsQuery   =    "*:* AND omics_type:\"Transcriptomics\"";

        QueryResult queryResultOfProteomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, proteomicsQuery,
                Constants.DATASET_SUMMARY, null, order, start, size, facetCount);
        QueryResult queryResultOfGenomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, genomicsQuery,
                Constants.DATASET_SUMMARY, null, order, start, size, facetCount);
        QueryResult queryResultOfMetabolomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, metabolomicsQuery,
                Constants.DATASET_SUMMARY, null, order, start, size, facetCount);
        QueryResult queryResultOfTranscriptomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, transcriptomicsQuery,
                Constants.DATASET_SUMMARY, null, order, start, size, facetCount);

        Facet[]  facetsG = queryResultOfGenomics.getFacets();
        Facet[]  facetsM = queryResultOfMetabolomics.getFacets();
        Facet[]  facetsP = queryResultOfProteomics.getFacets();
        Facet[]  facetsT = queryResultOfTranscriptomics.getFacets();

        FacetValue[] publicationDateFacetValueOfG = WsUtilities.getFacetValues(facetsG, Constants.PUB_DATES);
        FacetValue[] publicationDateFacetValueOfM = WsUtilities.getFacetValues(facetsM, Constants.PUB_DATES);
        FacetValue[] publicationDateFacetValueOfP = WsUtilities.getFacetValues(facetsP, Constants.PUB_DATES);
        FacetValue[] publicationDateFacetValueOfT = WsUtilities.getFacetValues(facetsT, Constants.PUB_DATES);


        List<String> distinctYears = WsUtilities.distinctYears(publicationDateFacetValueOfG,
                publicationDateFacetValueOfM, publicationDateFacetValueOfP, publicationDateFacetValueOfT);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        distinctYears = distinctYears.stream().filter(r -> (Integer.parseInt(r) <= currentYear)).
                collect(Collectors.toList());
        distinctYears.sort(Collections.reverseOrder());

        for (int i = 0; i < 4; i++) {  //latest 4 years
            String year                   = distinctYears.get(i);
            String genomicsNo             = WsUtilities.getFacetValueLabel(publicationDateFacetValueOfG, year);
            String metabolomicsNo         = WsUtilities.getFacetValueLabel(publicationDateFacetValueOfM, year);
            String proteomicsNo           = WsUtilities.getFacetValueLabel(publicationDateFacetValueOfP, year);
            String transcriptomicsNo      = WsUtilities.getFacetValueLabel(publicationDateFacetValueOfT, year);

            StatOmicsRecord record = new StatOmicsRecord(year, genomicsNo, metabolomicsNo, proteomicsNo,
                    transcriptomicsNo);
            resultStat.add(record);
        }

        return resultStat;
    }

    @ApiOperation(value = "Return statistics about the number of datasets By Omics type on recent 5 years ",
            position = 1, notes = "Return statistics about the number of datasets per OmicsType on recent 5 years ")
    @RequestMapping(value = "/modelExchangeByYear", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatOmicsRecord> getModelExchangeByYear() {

        List<StatOmicsRecord> resultStat = new ArrayList<>();

        // String sortfield = Constants.DESCRIPTION_FIELD;
        ///Todo: We need to update the fields that can be sorted
        String order = Constants.ORDER_ASCENDING;
        int start = 0;
        int size = 1;
        int facetCount = 25;

        String proteomicsQuery   =    "*:* AND omics_type:\"Models\"";
        String metabolomicsQuery =    "*:* AND omics_type:\"Metabolomics\"";
        String genomicsQuery     =    "*:* AND omics_type:\"Genomics\"";
        String transcriptomicsQuery   =    "*:* AND omics_type:\"Transcriptomics\"";

        QueryResult queryResultOfProteomics = dataWsClient.getDatasets(Constants.MODELEXCHANGE_DOMAIN, proteomicsQuery,
                Constants.DATASET_SUMMARY, null, order, start, size, facetCount);

        Facet[]  facetsP = queryResultOfProteomics.getFacets();


        FacetValue[] publicationDateFacetValueOfP = WsUtilities.getFacetValues(facetsP, Constants.PUB_DATES);



        List<String> distinctYears = WsUtilities.addFacetValues(publicationDateFacetValueOfP);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        distinctYears = distinctYears.stream().filter(r -> (Integer.parseInt(r) <= currentYear)).
                collect(Collectors.toList());
        distinctYears.sort(Collections.reverseOrder());

        for (int i = 0; i < 4; i++) {  //latest 4 years
            String year                   = distinctYears.get(i);
            String genomicsNo             = "0";
            String metabolomicsNo         = "0";
            String proteomicsNo           = "0";
            String transcriptomicsNo      = "0";
            String modelsNo               = WsUtilities.getFacetValueLabel(publicationDateFacetValueOfP, year);
            StatOmicsRecord record = new StatOmicsRecord(year, genomicsNo, metabolomicsNo, proteomicsNo,
                    transcriptomicsNo, modelsNo);
            resultStat.add(record);
        }

        return resultStat;
    }
}
