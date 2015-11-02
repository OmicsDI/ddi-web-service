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


    private List<String> getEnrichedWordsInDataset(DatasetEnrichmentInfo enrichmentInfo) {
        List<String> words = new ArrayList<String>();
        List<WordInField> wordsInField = new ArrayList<>();

        for (WordInField word : enrichmentInfo.getTitle()) {
            wordsInField.add(word);
        }

        for (WordInField word1 : enrichmentInfo.getAbstractDescription()) {
            wordsInField.add(word1);
        }

        for (WordInField word2 : enrichmentInfo.getSampleProtocol()) {
            wordsInField.add(word2);
        }

        for (WordInField wordInField : wordsInField) {
            if (!words.contains(wordInField.getText())) {
                words.add(wordInField.getText());
            }
        }

        return words;
    }


    @ApiOperation(value = "get similar datasets for a dataset", position = 1, notes = "retrieve all similar datasets for the dataset")
    @RequestMapping(value = "/getSimilarDatasetsByExpData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public
    @ResponseBody
    DataSetResult getSimilarDatasetsByExpData(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD000002") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database
    ) {

        DataSetResult result = new DataSetResult();
        List<DatasetSummary> datasetSummaryList = new ArrayList<DatasetSummary>();
        Map<String, Set<String>> currentIds = new HashMap<String, Set<String>>();

        //"PRIDE Archive" is the name stored in MongoDB
        String databaseNameInMongoDB = database;
        if (databaseNameInMongoDB.equals("PRIDE")) {
            databaseNameInMongoDB = "PRIDE Archive";
        }

        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, databaseNameInMongoDB);
        if (datasetStatInfo != null) {
            List<IntersectionInfo> intersectionInfos = datasetStatInfo.getIntersectionInfos();
            for (IntersectionInfo intersectionInfo : intersectionInfos) {

                if (intersectionInfo.getRelatedDatasetAcc() != null && intersectionInfo.getRelatedDatasetDatabase() != null) {
                    String tempDatabaseName = intersectionInfo.getRelatedDatasetDatabase();
                    if (tempDatabaseName.equals("PRIDE Archive")) {
                        tempDatabaseName = "PRIDE";
                    }
                    Set<String> ids = currentIds.get(tempDatabaseName);
                    if (ids == null)
                        ids = new HashSet<String>();
                    if (!(intersectionInfo.getRelatedDatasetAcc().equalsIgnoreCase(accession) && tempDatabaseName.equalsIgnoreCase(databaseNameInMongoDB)))
                        ids.add(intersectionInfo.getRelatedDatasetAcc());
                    currentIds.put(database, ids);
                }

            }

            for (String currentDomain : currentIds.keySet()) {
                QueryResult datasetResult = dataWsClient.getDatasetsById(currentDomain, Constants.DATASET_DETAIL, currentIds.get(currentDomain));
                datasetSummaryList.addAll(WsUtilities.transformDatasetSummary(datasetResult, currentDomain, null));
            }

        }
        result.setDatasets(datasetSummaryList);
        result.setCount(datasetSummaryList.size());

        return result;
    }

    @ApiOperation(value = "get similarity information for a dataset", position = 1, notes = "retrieve similarity info between the datasets that is similar to the given dataset")
    @RequestMapping(value = "/getSimilarityInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public
    @ResponseBody
    SimilarInfoResult getSimilarityInfo(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD000002") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database
    ) {
        //"PRIDE Archive" is the name stored in MongoDB
        String databaseNameInMongoDB = database;
        if (databaseNameInMongoDB.equals("PRIDE")) {
            databaseNameInMongoDB = "PRIDE Archive";
        }

        Map<String, String> similarDatasets = new HashMap<>();
        Set<Triplet> Scores = new HashSet<>();
        similarDatasets.put(accession, databaseNameInMongoDB);//put itself in the set;
        String combatName = accession + "@" + databaseNameInMongoDB;

        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, databaseNameInMongoDB);
        if (datasetStatInfo != null) {
            List<IntersectionInfo> intersectionInfos = datasetStatInfo.getIntersectionInfos();
            int length = intersectionInfos.size();
            for (int i=0; i<length; i++) {
                IntersectionInfo intersectionInfo = intersectionInfos.get(i);
                String combatName2 = intersectionInfo.getRelatedDatasetAcc() + "@" + intersectionInfo.getRelatedDatasetDatabase();

                if (intersectionInfo.getRelatedDatasetAcc() != null && intersectionInfo.getRelatedDatasetDatabase() != null) {
                    similarDatasets.put(intersectionInfo.getRelatedDatasetAcc(), intersectionInfo.getRelatedDatasetDatabase());
                    Triplet<String, String, Float> score = new Triplet<>(combatName,combatName2,(float)intersectionInfo.getCosineScore());
                    Scores.add(score);
                findSimilarDatasetsFor(intersectionInfo.getRelatedDatasetAcc(), intersectionInfo.getRelatedDatasetDatabase(), similarDatasets, Scores);
                }
            }
        }
        SimilarInfoResult similarInfoResult = new SimilarInfoResult(accession, databaseNameInMongoDB, Scores);
        return similarInfoResult;
    }

    private void findSimilarDatasetsFor(String accession, String databaseNameInMongoDB, Map<String, String> similarDatasets, Set<Triplet> Scores) {
        String combatName = accession + "@" + databaseNameInMongoDB;
        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, databaseNameInMongoDB);
        if (datasetStatInfo != null) {
            List<IntersectionInfo> intersectionInfos = datasetStatInfo.getIntersectionInfos();
            int length = intersectionInfos.size();
            for (int i=0; i<length; i++) {
                IntersectionInfo intersectionInfo = intersectionInfos.get(i);
                String combatName2 = intersectionInfo.getRelatedDatasetAcc() + "@" + intersectionInfo.getRelatedDatasetDatabase();
                if (intersectionInfo.getRelatedDatasetAcc() != null && intersectionInfo.getRelatedDatasetDatabase() != null) {
                    similarDatasets.put(intersectionInfo.getRelatedDatasetAcc(), intersectionInfo.getRelatedDatasetDatabase());
                    Triplet<String, String, Float> score = new Triplet<>(combatName,combatName2,(float)intersectionInfo.getCosineScore());
                    if (!Scores.contains(score)) {
                        Scores.add(score);
                    }
                }
            }
        }
    }


}
