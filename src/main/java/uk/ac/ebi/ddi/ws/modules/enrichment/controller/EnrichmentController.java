package uk.ac.ebi.ddi.ws.modules.enrichment.controller;

/**
 * Created by mingze on 27/10/15.
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
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.service.db.model.enrichment.DatasetEnrichmentInfo;
import uk.ac.ebi.ddi.service.db.model.enrichment.Synonym;
import uk.ac.ebi.ddi.service.db.model.enrichment.WordInField;
import uk.ac.ebi.ddi.service.db.model.similarity.DatasetStatInfo;
import uk.ac.ebi.ddi.service.db.model.similarity.IntersectionInfo;
import uk.ac.ebi.ddi.service.db.service.enrichment.IEnrichmentInfoService;
import uk.ac.ebi.ddi.service.db.service.enrichment.ISynonymsService;
import uk.ac.ebi.ddi.service.db.service.similarity.IDatasetStatInfoService;
import uk.ac.ebi.ddi.service.db.service.similarity.IExpOutputDatasetService;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;
import uk.ac.ebi.ddi.ws.modules.enrichment.model.SimilarInfoResult;
import uk.ac.ebi.ddi.ws.modules.enrichment.model.SynonymsForDataset;
import uk.ac.ebi.ddi.ws.modules.enrichment.model.SynonymsForWord;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.Triplet;
import uk.ac.ebi.ddi.ws.util.WsUtilities;

import java.util.*;

@Api(value = "enrichment", description = "Retrieve the information about the enrichment and synonyms ", position = 0)
@Controller
@RequestMapping(value = "/enrichment")

public class EnrichmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);

    @Autowired
    IEnrichmentInfoService enrichmentService;

    @Autowired
    ISynonymsService wordService;

    @Autowired
    IDatasetStatInfoService datasetStatInfoService;

    @Autowired
    DatasetWsClient dataWsClient;

    @ApiOperation(value = "get enrichment Info", position = 1, notes = "retrieve the enrichment data for a dataset")
    @RequestMapping(value = "/getEnrichmentInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public
    @ResponseBody
    DatasetEnrichmentInfo getEnrichmentInfo(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD002287") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database
    ) {
        return enrichmentService.readByAccession(accession, database);
    }


    @ApiOperation(value = "get synonyms for a dataset", position = 1, notes = "retrieve all synonyms for the words in a dataset")
    @RequestMapping(value = "/getSynonymsForDataset", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public
    @ResponseBody
    SynonymsForDataset getSynonymsForDataset(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD002287") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database
    ) {

        DatasetEnrichmentInfo enrichmentInfo = enrichmentService.readByAccession(accession, database);
        if (enrichmentInfo == null) {
            return null;
        }
        List<String> words = getEnrichedWordsInDataset(enrichmentInfo);

        SynonymsForDataset synonymsForDataset = new SynonymsForDataset(accession, database);

        for (String word : words) {
            SynonymsForWord synonymsForWord = new SynonymsForWord(word);
            List<String> synonymList = wordService.getAllSynonyms(word);
            synonymsForWord.setSynonyms(synonymList);
            synonymsForDataset.addSynonymsForWordIntoList(synonymsForWord);
        }

        return synonymsForDataset;
    }


    /**
     * Get all enriched words(string) in a dataset, from different fields
     * @param enrichmentInfo
     * @return
     */
    private List<String> getEnrichedWordsInDataset(DatasetEnrichmentInfo enrichmentInfo) {
        List<String> words = new ArrayList<String>();
        List<WordInField> wordsInField = new ArrayList<>();

        if(enrichmentInfo.getTitle() != null) {
            for (WordInField word : enrichmentInfo.getTitle()) {
                wordsInField.add(word);
            }
        }

        if(enrichmentInfo.getAbstractDescription() != null) {
            for (WordInField word1 : enrichmentInfo.getAbstractDescription()) {
                wordsInField.add(word1);
            }
        }

        if(enrichmentInfo.getSampleProtocol() != null) {
            for (WordInField word2 : enrichmentInfo.getSampleProtocol()) {
                wordsInField.add(word2);
            }
        }

        if(enrichmentInfo.getDataProtocol() != null) {
            for (WordInField word3 : enrichmentInfo.getDataProtocol()) {
                wordsInField.add(word3);
            }
        }

        //unique
        for (WordInField wordInField : wordsInField) {
            if (!words.contains(wordInField.getText())) {
                words.add(wordInField.getText());
            }
        }

        return words;
    }


    @ApiOperation(value = "get similar datasets for a dataset", position = 1, notes = "retrieve all similar datasets for the dataset")
    @RequestMapping(value = "/getSimilarDatasetsByBiologicalData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public
    @ResponseBody
    DataSetResult getSimilarDatasetsByBiologicalData(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD000002") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database
    ) {

        DataSetResult result = new DataSetResult();
        List<DatasetSummary> datasetSummaryList = new ArrayList<DatasetSummary>();
        Map<String, Set<String>> currentIds = new HashMap<String, Set<String>>();
        List<Double> scores = new ArrayList<>();
        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, database);
        List<IntersectionInfo> intersectionInfos;
        if (datasetStatInfo != null) {
            intersectionInfos = datasetStatInfo.getIntersectionInfos();
            for (IntersectionInfo intersectionInfo : intersectionInfos) {

                if (intersectionInfo.getRelatedDatasetAcc() != null || intersectionInfo.getRelatedDatasetDatabase() != null) {
                    String tempDatabaseName = intersectionInfo.getRelatedDatasetDatabase();

                    if(tempDatabaseName.equals("NA")) {
                        continue;
                    }

                    if(tempDatabaseName.equals("MetabolomicsWorkbench")) {
                        tempDatabaseName = "metabolomics_workbench";
                    }
                    scores.add(intersectionInfo.getCosineScore());
                    Set<String> ids = currentIds.get(tempDatabaseName);
                    if (ids == null)
                        ids = new HashSet<String>();
                    if (!(intersectionInfo.getRelatedDatasetAcc().equalsIgnoreCase(accession) && tempDatabaseName.equalsIgnoreCase(database)))
                        ids.add(intersectionInfo.getRelatedDatasetAcc());
                    currentIds.put(tempDatabaseName, ids);
                }

            }

            for (String currentDomain : currentIds.keySet()) {
                QueryResult datasetResult = dataWsClient.getDatasetsById(currentDomain, Constants.DATASET_DETAIL, currentIds.get(currentDomain));
                datasetSummaryList.addAll(WsUtilities.transformDatasetSummary(datasetResult, currentDomain, null));
            }

        Collections.sort(scores);
        Collections.reverse(scores);

        datasetSummaryList = sortDatasetSummaryList(datasetSummaryList, intersectionInfos,scores);
        result.setDatasets(datasetSummaryList);
        result.setCount(datasetSummaryList.size());
        }
        return result;
    }

    private List<DatasetSummary> sortDatasetSummaryList(List<DatasetSummary> datasetSummaryList, List<IntersectionInfo> intersectionInfos, List<Double> scores) {
        List<DatasetSummary> newDatasetSummaryList = new ArrayList<>();
        newDatasetSummaryList.addAll(datasetSummaryList);

        for (DatasetSummary newDatasetSummary : newDatasetSummaryList) {
            newDatasetSummary = null;
        }

        for (DatasetSummary datasetSummary : datasetSummaryList) {
            String accession = datasetSummary.getId();
            String database = datasetSummary.getSource();
            if (database.equals("metabolomics_workbench")) {
                database = "MetabolomicsWorkbench";
            }
            Double score = 0.0;
            for (IntersectionInfo intersectionInfo : intersectionInfos) {
                if (intersectionInfo.getRelatedDatasetAcc().equals(accession) && intersectionInfo.getRelatedDatasetDatabase().equals(database)) {
                    score = intersectionInfo.getCosineScore();
                    break;
                }
            }

            int index = scores.indexOf(score);
            if(index < 0) continue;;

            scores.set(index, 0.0 - index);
            newDatasetSummaryList.set(index, datasetSummary);
        }
        return newDatasetSummaryList;
    }

    @ApiOperation(value = "get similarity information for a dataset", position = 1, notes = "retrieve similarity info between the datasets that is similar to the given dataset, filtered by similarity threshold score ")
    @RequestMapping(value = "/getSimilarityInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public
    @ResponseBody
    SimilarInfoResult getSimilarityInfo(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD000002") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database,
            @ApiParam(value = "Threshold score, e.g: 0.10")
            @RequestParam(value = "threshold", required = true, defaultValue = "0.10") float threshold
    ) {

        List<String> similarDatasets = new ArrayList<>();
        List<Triplet> Scores = new ArrayList<>();
        String combinedName = accession + "@" + database;
        similarDatasets.add(combinedName);//put itself in the set;

        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, database);
        if (datasetStatInfo != null) {
            List<IntersectionInfo> intersectionInfos = datasetStatInfo.getIntersectionInfos();
            int length = intersectionInfos.size();
            for (int i=0; i<length; i++) {
                IntersectionInfo intersectionInfo = intersectionInfos.get(i);
                String combinedName2 = intersectionInfo.getRelatedDatasetAcc() + "@" + intersectionInfo.getRelatedDatasetDatabase();

                if (intersectionInfo.getRelatedDatasetAcc() != null && intersectionInfo.getRelatedDatasetDatabase() != null) {
                    similarDatasets.add(combinedName2);
                    if((float)intersectionInfo.getCosineScore() >= threshold){
                        Triplet<String, String, Float> score = new Triplet<>(combinedName, combinedName2, (float) intersectionInfo.getCosineScore());
                        Scores.add(score);
                    }
                findSimilarScoresFor(intersectionInfo.getRelatedDatasetAcc(), intersectionInfo.getRelatedDatasetDatabase(), similarDatasets, Scores, threshold);
                }
            }
        }
        SimilarInfoResult similarInfoResult = new SimilarInfoResult(accession, database, Scores);
        return similarInfoResult;
    }

    /**
     * This function find the similar scores between this dataset(accession+database) and the other datasets inside the similarDatasets set
     * @param accession
     * @param database
     * @param similarDatasets
     * @param Scores
     */
    private void findSimilarScoresFor(String accession, String database, List<String> similarDatasets, List<Triplet> Scores, float threshold) {
        String combinedName = accession + "@" + database;
        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, database);
        if (datasetStatInfo != null) {
            List<IntersectionInfo> intersectionInfos = datasetStatInfo.getIntersectionInfos();
            int length = intersectionInfos.size();
            for (int i=0; i<length; i++) {
                IntersectionInfo intersectionInfo = intersectionInfos.get(i);
                String combinedName2 = intersectionInfo.getRelatedDatasetAcc() + "@" + intersectionInfo.getRelatedDatasetDatabase();
                if (similarDatasets.contains(combinedName2) && (float)intersectionInfo.getCosineScore() >= threshold) {
                    Triplet<String, String, Float> score = new Triplet<>(combinedName,combinedName2,(float)intersectionInfo.getCosineScore());
                    if (!Scores.contains(score)) {
                        Scores.add(score);
                    }
                }
            }
        }
    }


}
