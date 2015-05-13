package uk.ac.ebi.ddi.ws.modules.stats.controller;

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
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.facet.FacetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.FacetValue;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Facet;
import uk.ac.ebi.ddi.ebe.ws.dao.model.facet.FacetList;
import uk.ac.ebi.ddi.ws.modules.stats.model.DomainStats;
import uk.ac.ebi.ddi.ws.modules.stats.model.StatOmicsRecord;
import uk.ac.ebi.ddi.ws.modules.stats.model.StatRecord;
import uk.ac.ebi.ddi.ws.modules.stats.util.RepoStatsToWsStatsMapper;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.WsUtilities;



import java.util.ArrayList;
import java.util.List;

/**
 * Controller for accessing the statistics
 *
 * @author ypriverol Yasset Perez-Riverol
 */

@Api(value = "stats", description = "retrieve statistics about the DDI repositories, access, etc", position = 0)
@Controller
@RequestMapping(value = "/stats")

public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    DomainWsClient domainWsClient;

    @Autowired
    DatasetWsClient dataWsClient;

    @Autowired
    FacetWsClient facetWsClient;

    @ApiOperation(value = "Return statistics about the number of datasets per Repository", position = 1, notes = "Return statistics about the number of datasets per Repository")
    @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    List<DomainStats> getDomainEntries() {

        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);

        return RepoStatsToWsStatsMapper.asDomainStatsList(domain);
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Organisms", position = 1, notes = "Return statistics about the number of datasets per Organisms")
    @RequestMapping(value = "/organisms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    List<StatRecord> getTaxonomies(@ApiParam(value = "Organisms to be retrieved: maximum 100")
                                   @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        DomainList domain    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);

        String[] dubdomains  = WsUtilities.getSubdomainList(domain);

        FacetList taxonomies = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,dubdomains,Constants.TAXONOMY_FIELD, 100);

        return RepoStatsToWsStatsMapper.asFacetCount(taxonomies, Constants.TAXONOMY_FIELD);
    }

    @ApiOperation(value = "Return statistics about the number of datasets per Tissue", position = 1, notes = "Return statistics about the number of datasets per Tissue")
     @RequestMapping(value = "/tissues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
     @ResponseStatus(HttpStatus.OK) // 200
     public @ResponseBody
     List<StatRecord> getTissues(
            @ApiParam(value = "Tissues to be retrieved: maximum 100")
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        DomainList domain    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);

        String[] dubdomains  = WsUtilities.getSubdomainList(domain);

        FacetList tissues = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,dubdomains,Constants.TISSUE_FIELD, size);

        return RepoStatsToWsStatsMapper.asFacetCount(tissues, Constants.TISSUE_FIELD);
    }

     @ApiOperation(value = "Return statistics about the number of datasets per Omics Type", position = 1, notes = "Return statistics about the number of datasets per Omics Type")
     @RequestMapping(value = "/omicsType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
     @ResponseStatus(HttpStatus.OK) // 200
     public @ResponseBody
     List<StatRecord> getOmics() {

        DomainList domain    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);

        String[] dubdomains  = WsUtilities.getSubdomainList(domain);

        FacetList omics = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,dubdomains,Constants.OMICS_TYPE_FIELD, 100);

        return RepoStatsToWsStatsMapper.asFacetCount(omics, Constants.OMICS_TYPE_FIELD);
    }

     @ApiOperation(value = "Return statistics about the number of datasets per dieases", position = 1, notes = "Return statistics about the number of datasets per diseases")
     @RequestMapping(value = "/diseases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
     @ResponseStatus(HttpStatus.OK) // 200
     public @ResponseBody
     List<StatRecord> getDiseases(@ApiParam(value = "Dieseases to be retrieved: maximum 100")
                                  @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        DomainList domain    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);

        String[] subdomains  = WsUtilities.getSubdomainList(domain);

        FacetList diseases = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,subdomains,Constants.DISEASE_FIELD, size);

        return RepoStatsToWsStatsMapper.asFacetCount(diseases, Constants.DISEASE_FIELD);
    }

    @ApiOperation(value = "Return General statistics about the Services", position = 1, notes = "Return General statistics about the Services")
    @RequestMapping(value = "/general", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    List<StatRecord> getGeneral() {

        DomainList domain    = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);

        String[] subdomains  = WsUtilities.getSubdomainList(domain);

        List<StatRecord> resultStat = new ArrayList<StatRecord>();

        resultStat.add(new StatRecord("Different Repositories/Databases", String.valueOf(subdomains.length)));

        Integer numberOfDatasets = WsUtilities.getNumberofEntries(Constants.MAIN_DOMAIN, domain);

        resultStat.add(new StatRecord("Different Datasets", String.valueOf(numberOfDatasets)));

        FacetList facet = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,subdomains,Constants.DISEASE_FIELD, 100);

        if(facet.getFacets() != null && facet.getFacets()[0] != null && facet.getFacets()[0].getFacetValues()!= null){
            if(facet.getFacets()[0].getFacetValues().length >= 100){
                resultStat.add(new StatRecord("More than 100 Diseases", null));
            }else{
                resultStat.add(new StatRecord("Different Diseases", String.valueOf(facet.getFacets()[0].getFacetValues().length)));
            }
        }

        facet = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,subdomains,Constants.TISSUE_FIELD, 100);

        if(facet.getFacets() != null && facet.getFacets()[0] != null && facet.getFacets()[0].getFacetValues()!= null){
            if(facet.getFacets()[0].getFacetValues().length >= 100){
                resultStat.add(new StatRecord("More than 100 Tissues", null));
            }else{
                resultStat.add(new StatRecord("Different Tissues", String.valueOf(facet.getFacets()[0].getFacetValues().length)));
            }
        }

        facet = facetWsClient.getFacetEntriesByDomains(Constants.MAIN_DOMAIN,subdomains,Constants.TAXONOMY_FIELD, 100);

        if(facet.getFacets() != null && facet.getFacets()[0] != null && facet.getFacets()[0].getFacetValues()!= null){
            if(facet.getFacets()[0].getFacetValues().length >= 100){
                resultStat.add(new StatRecord("More than 100 Species/Organisms", null));
            }else{
                resultStat.add(new StatRecord("Different Species/Organisms", String.valueOf(facet.getFacets()[0].getFacetValues().length)));
            }
        }

        return resultStat;
    }

    @ApiOperation(value = "Return statistics about the number of datasets per OmicsType on recent 5 years ", position = 1, notes = "Return statistics about the number of datasets per OmicsType on recent 5 years ")
    @RequestMapping(value = "/omicsType_annual", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    List<StatOmicsRecord> getOmicsNo() {

        List<StatOmicsRecord> resultStat = new ArrayList<StatOmicsRecord>();

        String sortfield = "";
        String order = "";
        int start = 0;
        int size = 1;
        int facetCount = 20;

        String proteomicsQuery =  "*:* AND omics_type:\"Proteomics\"";
        String metabolomicsQuery =  "*:* AND omics_type:\"Metabolomics\"";
        String genomicsQuery =  "*:* AND omics_type:\"Genomics\"";

        QueryResult queryResultOfGenomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, genomicsQuery, Constants.DATASET_SUMMARY, sortfield, order, start, size, facetCount);
        QueryResult queryResultOfMetabolomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, metabolomicsQuery, Constants.DATASET_SUMMARY, sortfield, order, start, size, facetCount);
        QueryResult queryResultOfProteomics = dataWsClient.getDatasets(Constants.MAIN_DOMAIN, proteomicsQuery, Constants.DATASET_SUMMARY, sortfield, order, start, size, facetCount);

        Facet[] facetsG = queryResultOfGenomics.getFacets();
        Facet[]  facetsM = queryResultOfMetabolomics.getFacets();
        Facet[]  facetsP = queryResultOfProteomics.getFacets();

        FacetValue[] publicationDateFacetValueOfG = facetsM[2].getFacetValues(); //use metabolomics now, need to be changed
        FacetValue[] publicationDateFacetValueOfM = facetsM[2].getFacetValues();
        FacetValue[] publicationDateFacetValueOfP = facetsM[2].getFacetValues();  //use metabolomics now, need to be changed

        String year = "";
        String genomicsNo = "";
        String metabolomicsNo = "";
        String proteomicsNo = "";


        for(int i=0; i<4; i++){  //latest 4 years
            year = publicationDateFacetValueOfG[i].getLabel();
            genomicsNo  = publicationDateFacetValueOfG[i].getCount();
            metabolomicsNo  = publicationDateFacetValueOfM[i].getCount();
            proteomicsNo  = publicationDateFacetValueOfP[i].getCount();
            StatOmicsRecord record = new StatOmicsRecord(year,genomicsNo,metabolomicsNo,proteomicsNo);
            resultStat.add(record);
        }


        return resultStat;
    }

}
