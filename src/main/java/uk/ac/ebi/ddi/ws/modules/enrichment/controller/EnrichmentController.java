package uk.ac.ebi.ddi.ws.modules.enrichment.controller;

/**
 * Created by mingze on 27/10/15.
 */

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.service.db.model.dataset.DatasetShort;
import uk.ac.ebi.ddi.service.db.model.enrichment.DatasetEnrichmentInfo;
import uk.ac.ebi.ddi.service.db.model.enrichment.WordInField;
import uk.ac.ebi.ddi.service.db.model.similarity.DatasetStatInfo;
import uk.ac.ebi.ddi.service.db.model.similarity.IntersectionInfo;
import uk.ac.ebi.ddi.service.db.service.database.DatabaseDetailService;
import uk.ac.ebi.ddi.service.db.service.enrichment.IEnrichmentInfoService;
import uk.ac.ebi.ddi.service.db.service.enrichment.ISynonymsService;
import uk.ac.ebi.ddi.service.db.service.similarity.IDatasetStatInfoService;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;
import uk.ac.ebi.ddi.ws.modules.enrichment.model.SimilarInfoResult;
import uk.ac.ebi.ddi.ws.modules.enrichment.model.SynonymsForDataset;
import uk.ac.ebi.ddi.ws.modules.enrichment.model.SynonymsForWord;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.Triplet;
import uk.ac.ebi.ddi.ws.util.WsUtilities;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(value = "enrichment", description = "Retrieve the information about the enrichment and synonyms ")
@Controller
@RequestMapping(value = "/enrichment")
@ApiIgnore
public class EnrichmentController {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    IEnrichmentInfoService enrichmentService;

    @Autowired
    ISynonymsService wordService;

    @Autowired
    IDatasetStatInfoService datasetStatInfoService;

    @Autowired
    DatasetWsClient dataWsClient;

    @Autowired
    DatabaseDetailService databaseDetailService;

    @ApiOperation(value = "Get enrichment Info", position = 1, notes = "Retrieve the enrichment data for a dataset")
    @RequestMapping(value = "/getEnrichmentInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public DatasetEnrichmentInfo getEnrichmentInfo(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD002287") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database) {

        database = databaseDetailService.retriveAnchorName(database);
        return enrichmentService.readByAccession(accession, database);
    }


    @ApiOperation(value = "Get synonyms for a dataset", position = 1,
            notes = "Retrieve all synonyms for the words in a dataset")
    @RequestMapping(value = "/getSynonymsForDataset", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public SynonymsForDataset getSynonymsForDataset(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD002287") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database) {

        database = databaseDetailService.retriveAnchorName(database);
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
     *
     * @param enrichmentInfo
     * @return
     */
    private List<String> getEnrichedWordsInDataset(DatasetEnrichmentInfo enrichmentInfo) {
        Set<String> words = new HashSet<>();
        List<WordInField> wordsInField = new ArrayList<>();
        if (enrichmentInfo.getSynonyms() != null) {
            if (enrichmentInfo.getSynonyms().containsKey(DSField.NAME.getName())) {
                wordsInField.addAll(enrichmentInfo.getSynonyms().get(DSField.NAME.getName()));
            }
            if (enrichmentInfo.getSynonyms().containsKey(DSField.DESCRIPTION.getName())) {
                wordsInField.addAll(enrichmentInfo.getSynonyms().get(DSField.DESCRIPTION.getName()));
            }
            if (enrichmentInfo.getSynonyms().containsKey(DSField.Additional.DATA.getName())) {
                wordsInField.addAll(enrichmentInfo.getSynonyms().get(DSField.Additional.DATA.getName()));
            }
            if (enrichmentInfo.getSynonyms().containsKey(DSField.Additional.SAMPLE.getName())) {
                wordsInField.addAll(enrichmentInfo.getSynonyms().get(DSField.Additional.SAMPLE.getName()));
            }
            if (enrichmentInfo.getSynonyms().containsKey(DSField.Additional.PUBMED_TITLE.getName())) {
                wordsInField.addAll(enrichmentInfo.getSynonyms().get(DSField.Additional.PUBMED_TITLE.getName()));
            }
            if (enrichmentInfo.getSynonyms().containsKey(DSField.Additional.PUBMED_ABSTRACT.getName())) {
                wordsInField.addAll(enrichmentInfo.getSynonyms().get(DSField.Additional.PUBMED_ABSTRACT.getName()));
            }
        }
        //unique
        for (WordInField wordInField : wordsInField) {
            words.add(wordInField.getText());
        }

        return new ArrayList<>(words);
    }


    @ApiOperation(value = "Get similar datasets for a dataset", position = 1,
            notes = "Retrieve all similar datasets for the dataset")
    @RequestMapping(value = "/getSimilarDatasetsByBiologicalData", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public DataSetResult getSimilarDatasetsByBiologicalData(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD000002") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database) {

        String anchorDatabase = databaseDetailService.retriveAnchorName(database);
        DataSetResult result = new DataSetResult();
        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(accession, anchorDatabase);
        List<IntersectionInfo> intersectionInfos;

        if (datasetStatInfo != null) {
            intersectionInfos = datasetStatInfo.getIntersectionInfos();
            intersectionInfos.sort(this::compareIntersectionInfo);
            int subListLength = 30;
            if (subListLength > intersectionInfos.size()) {
                subListLength = intersectionInfos.size();
            }
            intersectionInfos = intersectionInfos.subList(0, subListLength);

            List<DatasetSummary> datasetSummaryList = getDataFromEBeyeSearch(accession, database, intersectionInfos);
            datasetSummaryList = addScores(datasetSummaryList, intersectionInfos);

            result.setDatasets(datasetSummaryList);
            result.setCount(datasetSummaryList.size());
        }
        return result;
    }

    private List<DatasetSummary> getDataFromEBeyeSearch(String acc, String db, List<IntersectionInfo> intersecInfos) {

        List<DatasetSummary> datasetSummaryList = new ArrayList<>();
        Map<String, Set<String>> currentIds = new HashMap<>();
        for (IntersectionInfo intersecInfo : intersecInfos) {
            if (intersecInfo.getRelatedDatasetAcc() != null || intersecInfo.getRelatedDatasetDatabase() != null) {
                String tmpDbName = intersecInfo.getRelatedDatasetDatabase();
                tmpDbName = databaseDetailService.retriveSolrName(tmpDbName);
                Set<String> ids = currentIds.get(tmpDbName);

                if (ids == null) {
                    ids = new HashSet<>();
                }
                if (!(intersecInfo.getRelatedDatasetAcc().equalsIgnoreCase(acc) && tmpDbName.equalsIgnoreCase(db))) {
                    ids.add(intersecInfo.getRelatedDatasetAcc());
                }
                currentIds.put(tmpDbName, ids);
            }
        }

        for (String currentDomain : currentIds.keySet()) {
            Set<String> ids = currentIds.get(currentDomain);

            if (ids.size() < 99) {
                QueryResult datasetResult = dataWsClient.getDatasetsById(currentDomain, Constants.DATASET_DETAIL, ids);
                datasetSummaryList.addAll(WsUtilities.transformDatasetSummary(datasetResult, currentDomain, null));
            }
        }

        return datasetSummaryList;
    }

    private List<DatasetSummary> addScores(List<DatasetSummary> dsSummaries, List<IntersectionInfo> intersecInfos) {

        for (DatasetSummary datasetSummary : dsSummaries) {
            String acc = datasetSummary.getId();
            String db = datasetSummary.getSource();
            db = databaseDetailService.retriveAnchorName(db);
            double score = 0.0;
            for (IntersectionInfo intersecInfo : intersecInfos) {
                if (intersecInfo.getRelatedDatasetAcc().equals(acc)
                        && intersecInfo.getRelatedDatasetDatabase().equals(db)) {
                    score = intersecInfo.getCosineScore();
                    break;
                }
            }
            datasetSummary.setScore(String.valueOf(score));
        }

        dsSummaries.sort((o1, o2) -> {
            Double value1 = Double.valueOf(o1.getScore());
            Double value2 = Double.valueOf(o2.getScore());
            if (value1 < value2) {
                return 1;
            } else if (Objects.equals(value1, value2)) {
                return 0;
            } else {
                return -1;
            }
        });

        if (dsSummaries.size() > 10) {
            dsSummaries = dsSummaries.subList(0, 10);
        }

        return dsSummaries;
    }

    @ApiOperation(value = "Get similarity information for a dataset", position = 1,
            notes = "Retrieve similarity info between the datasets that is similar to the given dataset, "
                    + "filtered by similarity threshold score ")
    @RequestMapping(value = "/getSimilarityInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public SimilarInfoResult getSimilarityInfo(
            @ApiParam(value = "Dataset accession")
            @RequestParam(value = "accession", required = true, defaultValue = "PXD000002") String accession,
            @ApiParam(value = "Database name, e.g: PRIDE")
            @RequestParam(value = "database", required = true, defaultValue = "PRIDE") String database,
            @ApiParam(value = "Threshold score, e.g: 0.50")
            @RequestParam(value = "threshold", required = true, defaultValue = "0.50") float threshold) {

        Set<String> similarDatasets = new HashSet<>();
        Map<Triplet, Triplet<String, String, Double>> scores = new HashMap<>();
        String source = accession + "@" + database;
        similarDatasets.add(source); //put itself in the set;

        DatasetStatInfo datasetStatInfo = datasetStatInfoService.readByAccession(
                accession, databaseDetailService.retriveAnchorName(database));
        if (datasetStatInfo == null) {
            return new SimilarInfoResult(accession, database, Collections.emptyList());
        }
        List<IntersectionInfo> intersectionInfos = datasetStatInfo.getIntersectionInfos();
        Set<DatasetShort> toFetch = new HashSet<>();
        for (IntersectionInfo intersect : intersectionInfos) {
            String dest = intersect.getRelatedDatasetAcc() + "@" + intersect.getRelatedDatasetDatabase();

            if (intersect.getRelatedDatasetAcc() == null || intersect.getRelatedDatasetDatabase() == null) {
                continue;
            }
            similarDatasets.add(dest);
            if (intersect.getCosineScore() >= threshold) {
                Triplet<String, String, Double> score = new Triplet<>(source, dest, intersect.getCosineScore());
                scores.put(score, score);
                toFetch.add(new DatasetShort(intersect.getRelatedDatasetDatabase(), intersect.getRelatedDatasetAcc()));
            }
        }
        List<DatasetStatInfo> datasetStatInfos = datasetStatInfoService.findMultiple(toFetch);

        datasetStatInfos.forEach(childDs -> childDs.getIntersectionInfos().forEach(grantChild -> {
            String childSource = childDs.getAccession() + "@" + childDs.getDatabase();
            String childDest = grantChild.getRelatedDatasetAcc() + "@" + grantChild.getRelatedDatasetDatabase();
            Triplet<String, String, Double> score = new Triplet<>(childSource, childDest, grantChild.getCosineScore());
            if (similarDatasets.contains(childDest) && grantChild.getCosineScore() >= threshold) {
                if (!scores.containsKey(score) || scores.get(score).getValue() < score.getValue()) {
                    scores.put(score, score);
                }
            }
        }));
        return new SimilarInfoResult(accession, database, new ArrayList<>(scores.values()));
    }

    private int compareIntersectionInfo(IntersectionInfo o1, IntersectionInfo o2) {
        Double value1 = o1.getCosineScore();
        Double value2 = o2.getCosineScore();
        if (value1 < value2) {
            return 1;
        } else if (Objects.equals(value1, value2)) {
            return 0;
        } else {
            return -1;
        }
    }

    @ApiOperation(value = "Get reanalysis count for dataset", position = 1, notes = "Retrieve reanalysis count")
    @RequestMapping(value = "/getReanalysis", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    private void reanalysisScore() {
        //datasetStatInfoService.reanalysisCount();
    }
}
