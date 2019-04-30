package uk.ac.ebi.ddi.ws.modules.stats.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
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

/**
 * Controller for accessing the statistics
 *
 * @author ypriverol Yasset Perez-Riverol
*/
@Api(value = "statistics", description = "retrieve statistics about the DDI repositories, access, etc")
@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController {

    @Autowired
    DomainWsClient domainWsClient;

    @Autowired
    DatasetWsClient dataWsClient;

    @Autowired
    FacetWsClient facetWsClient;

    @Value("${app.version}")
    private String version;

    @ApiOperation(value = "Return statistics about the number of datasets per Repository", position = 1,
            notes = "Return statistics about the number of datasets per Repository")
    @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<DomainStats> getDomainEntries() {
        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        return RepoStatsToWsStatsMapper.asDomainStatsList(domain);
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Organisms", position = 1,
            notes = "Return statistics about the number of datasets per Organisms")
    @RequestMapping(value = "/organisms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getTaxonomies(@ApiParam(value = "Organisms to be retrieved: maximum 100")
                                   @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        String[] dubdomains  = WsUtilities.getSubdomainList(domain);
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
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        DomainList domain    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        String[] dubdomains  = WsUtilities.getSubdomainList(domain);
        FacetList tissues = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, dubdomains, DSField.Additional.TISSUE_FIELD.key(), size);
        return RepoStatsToWsStatsMapper.asFacetCount(tissues, DSField.Additional.TISSUE_FIELD.key());
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Omics Type", position = 1,
            notes = "Return statistics about the number of datasets per Omics Type")
    @RequestMapping(value = "/omics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getOmics() {

        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        String[] dubdomains  = WsUtilities.getSubdomainList(domain);
        FacetList omics = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, dubdomains, DSField.Additional.OMICS.key(), 100);
        return RepoStatsToWsStatsMapper.asFacetCount(omics, DSField.Additional.OMICS.key());
    }

    @ApiOperation(value = "Return statistics about the number of datasets per dieases", position = 1,
             notes = "Return statistics about the number of datasets per diseases")
    @RequestMapping(value = "/diseases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public List<StatRecord> getDiseases(@ApiParam(value = "Dieseases to be retrieved: maximum 100")
                                  @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        String[] subdomains  = WsUtilities.getSubdomainList(domain);
        FacetList diseases =
                facetWsClient.getFacetEntriesByDomains(
                        Constants.MAIN_DOMAIN, subdomains, DSField.Additional.DISEASE_FIELD.key(), size);
        return RepoStatsToWsStatsMapper.asFacetCount(diseases, DSField.Additional.DISEASE_FIELD.key());
    }

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
    public List<StatRecord> getGeneral() {

        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
        String[] subdomains  = WsUtilities.getSubdomainList(domain);
        List<StatRecord> resultStat = new ArrayList<>();
        resultStat.add(new StatRecord("Different Repositories/Databases",
                String.valueOf(subdomains.length), null));
        Integer numberOfDatasets = WsUtilities.getNumberofEntries(domain);
        resultStat.add(new StatRecord("Different Datasets", String.valueOf(numberOfDatasets), null));
        FacetList facet = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, subdomains, DSField.Additional.DISEASE_FIELD.key(), 100);

        if (facet.getFacets() != null && facet.getFacets()[0] != null
                && facet.getFacets()[0].getFacetValues() != null) {
                resultStat.add(new StatRecord("Different Diseases",
                        String.valueOf(facet.getFacets()[0].getTotal()), null));
        }

        facet = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, subdomains, DSField.Additional.TISSUE_FIELD.key(), 100);

        if (facet.getFacets() != null && facet.getFacets()[0] != null
                && facet.getFacets()[0].getFacetValues() != null) {
            resultStat.add(new StatRecord("Different Tissues",
                    String.valueOf(facet.getFacets()[0].getTotal()), null));
        }

        facet = facetWsClient.getFacetEntriesByDomains(
                Constants.MAIN_DOMAIN, subdomains, DSField.CrossRef.TAXONOMY.key(), 100);

        if (facet.getFacets() != null && facet.getFacets()[0] != null
                && facet.getFacets()[0].getFacetValues() != null) {
            resultStat.add(new StatRecord("Different Species/Organisms",
                    String.valueOf(facet.getFacets()[0].getTotal()), null));
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
        int facetCount = 20;

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
}
