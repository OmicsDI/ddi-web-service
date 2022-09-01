package uk.ac.ebi.ddi.ws.modules.dataset.util;

import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.TermResult;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.service.db.service.dataset.IMostAccessedDatasetService;
import uk.ac.ebi.ddi.service.db.service.similarity.CitationService;
import uk.ac.ebi.ddi.service.db.service.similarity.EBIPubmedSearchService;
import uk.ac.ebi.ddi.service.db.service.similarity.ReanalysisDataService;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetDetail;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;
import uk.ac.ebi.ddi.ws.modules.dataset.model.Organism;
import uk.ac.ebi.ddi.ws.modules.term.model.Term;
import uk.ac.ebi.ddi.ws.util.Constants;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Yasset Perez-Riverol ypriverol
 */
public class RepoDatasetMapper {

    public static CitationService citationService;

    public static EBIPubmedSearchService ebiPubmedSearchService;

    public static ReanalysisDataService reanalysisDataService;

    public static IMostAccessedDatasetService mostAccessedDatasetService;

    public static IDatasetService datasetService;

    private RepoDatasetMapper() {
    }

    /**
     * Transform the information form the query to the Web service strucutre
     * @param queryResults The original results from the Query
     * @return             The set of datasets form the query
     */
    public static DataSetResult asDataSummary(QueryResult queryResults, QueryResult taxonomies) {

        Map<String, String> taxonomyMap = RepoDatasetMapper.getTaxonomyMap(taxonomies);

        DataSetResult dataset = new DataSetResult();

        List<DatasetSummary> datasets = new ArrayList<>();

        dataset.setCount(queryResults.getCount());

        if (queryResults.getFacets() != null && queryResults.getFacets().length > 0) {
            dataset.setFacets(Arrays.asList(queryResults.getFacets()));
        }

        if (queryResults.getEntries() != null && queryResults.getEntries().length > 0) {
            for (Entry entry: queryResults.getEntries()) {
                datasets.add(RepoDatasetMapper.transformDatasetSummary(entry, taxonomyMap));
            }
        }
        dataset.setDatasets(datasets);

        return dataset;
    }

    /**
     * Retrieve the information of each taxonomy as id + name
     * @param taxonomies A list of taxonomies
     * @return The map of the taxonomies as Map<id, name>
     */
    private static Map<String, String> getTaxonomyMap(QueryResult taxonomies) {
        Map<String, String> taxonomyMap  = new HashMap<>();

        if (taxonomies != null && taxonomies.getEntries() != null && taxonomies.getEntries().length > 0) {
            for (Entry entry: taxonomies.getEntries()) {
                if (entry != null && entry.getFields() != null && entry.getFields().containsKey(Constants.TAXONOMY_NAME)
                        && entry.getFields().get(Constants.TAXONOMY_NAME).length > 0
                        && entry.getFields().get(Constants.TAXONOMY_NAME)[0] != null) {
                    taxonomyMap.put(entry.getId(), entry.getFields().get(Constants.TAXONOMY_NAME)[0]);
                }
            }
        }
        return taxonomyMap;
    }

    private static boolean hasValue(Map<String, String[]> fields, String key) {
        if (fields.get(key) != null && fields.get(key).length > 0) {
            return true;
        }
        return false;
    }

    /**
     * Transform a web-service entry to a DatasetSummary
     * @param entry the original entry from the dataset
     * @param taxonomyMap The map of all taxonomies included in this query
     * @return a Dataset Summary
     */
    private static DatasetSummary transformDatasetSummary(Entry entry, Map<String, String> taxonomyMap) {

        DatasetSummary datasetSummary = new DatasetSummary();

        if (entry == null) {
            return datasetSummary;
        }
        datasetSummary.setId(entry.getId());
        datasetSummary.setSource(entry.getSource());

        if (entry.getFields() == null) {
            return datasetSummary;
        }

        Map<String, String[]> fields = entry.getFields();

        if (hasValue(fields, DSField.NAME.key())) {
            datasetSummary.setTitle(fields.get(DSField.NAME.key())[0]);
        }

        if (hasValue(fields, DSField.DESCRIPTION.key())) {
            datasetSummary.setDescription(fields.get(DSField.DESCRIPTION.key())[0]);
        }

        if (hasValue(fields, Constants.PUB_DATE_FIELD)) {
            datasetSummary.setPublicationDate(fields.get(Constants.PUB_DATE_FIELD)[0]);
        }

        List<String> keywords = new ArrayList<>();

        if (hasValue(fields, DSField.Additional.CURATOR_KEYWORDS.key())) {
            keywords.addAll(formatKeywords(Arrays.asList(fields.get(DSField.Additional.CURATOR_KEYWORDS.key()))));
        }

        if (hasValue(fields, DSField.Additional.OMICS.key())) {
            datasetSummary.setOmicsType(Arrays.asList(fields.get(DSField.Additional.OMICS.key())));
        }

        if (hasValue(fields, DSField.Additional.SUBMITTER_KEYWORDS.key())) {
            keywords.addAll(formatKeywords(Arrays.asList(fields.get(DSField.Additional.SUBMITTER_KEYWORDS.key()))));
        }

        if (hasValue(fields, DSField.Additional.CITATION_COUNT.key())) {
            datasetSummary.setCitationsCount(Double.valueOf(
                    fields.get(DSField.Additional.CITATION_COUNT.key())[0]).intValue());
        }

        if (hasValue(fields, DSField.Additional.SEARCH_COUNT.key())) {
            datasetSummary.setConnectionsCount(Double.valueOf(
                    fields.get(DSField.Additional.SEARCH_COUNT.key())[0]).intValue());
        }

        if (hasValue(fields, DSField.Additional.VIEW_COUNT.key())) {
            datasetSummary.setViewsCount(Double.valueOf(fields.get(DSField.Additional.VIEW_COUNT.key())[0]).intValue());
        }

        if (hasValue(fields, DSField.Additional.REANALYSIS_COUNT.key())) {
            datasetSummary.setReanalysisCount(
                    Double.valueOf(fields.get(DSField.Additional.REANALYSIS_COUNT.key())[0]).intValue());
        }

        if (hasValue(fields, DSField.Additional.DOWNLOAD_COUNT.key())) {
            datasetSummary.setDownloadCount(Integer.valueOf(fields.get(DSField.Additional.DOWNLOAD_COUNT.key())[0]
                    .replace(".0", "")));
        }

        if (hasValue(fields, DSField.Additional.DOWNLOAD_COUNT_SCALED.key())) {
            datasetSummary.setDownloadCountScaled(
                    Double.valueOf(fields.get(DSField.Additional.DOWNLOAD_COUNT_SCALED.key())[0]));
        }

        if (hasValue(fields, DSField.Additional.CITATION_COUNT_SCALED.key())) {
            datasetSummary.setCitationsCountScaled(
                    Double.valueOf(fields.get(DSField.Additional.CITATION_COUNT_SCALED.key())[0]));
        }

        if (hasValue(fields, DSField.Additional.SEARCH_COUNT_SCALED.key())) {
            datasetSummary.setConnectionsCountScaled(
                    Double.valueOf(fields.get(DSField.Additional.SEARCH_COUNT_SCALED.key())[0]));
        }

        if (hasValue(fields, DSField.Additional.VIEW_COUNT_SCALED.key())) {
            datasetSummary.setViewsCountScaled(
                    Double.valueOf(fields.get(DSField.Additional.VIEW_COUNT_SCALED.key())[0]));
        }

        if (hasValue(fields, DSField.Additional.REANALYSIS_COUNT_SCALED.key())) {
            datasetSummary.setReanalysisCountScaled(
                    Double.valueOf(fields.get(DSField.Additional.REANALYSIS_COUNT_SCALED.key())[0]));
        }

        if (keywords.size() > 0) {
            String[] arrayKeywords = new String[keywords.size()];
            for (int i = 0; i < keywords.size(); i++) {
                arrayKeywords[i] = keywords.get(i);
            }
            datasetSummary.setKeywords(arrayKeywords);
        }

        //List<Organism> organisms = new ArrayList<>();



        if (hasValue(fields, DSField.Additional.SPECIE_FIELD.key())) {
            List<Organism> organisms = Arrays.stream(fields.get(DSField.Additional.SPECIE_FIELD.getName()))
                    .map(r -> new Organism("", r)).collect(Collectors.toList());
            datasetSummary.setOrganisms(organisms);
        }

      /*  if (hasValue(fields, DSField.CrossRef.TAXONOMY.key())) {
            for (String taxonomyId: fields.get(DSField.CrossRef.TAXONOMY.key())) {
                organisms.add(new Organism(taxonomyId, taxonomyMap.get(taxonomyId)));
            }
        }
        datasetSummary.setOrganisms(organisms);*/

        return datasetSummary;
    }

    private static Collection<? extends String> formatKeywords(List<String> strings) {
        List<String> newKeywords;
        if (strings != null && !strings.isEmpty()) {
            newKeywords = new ArrayList<>();
            for (String oldkeyword: strings) {
                Pattern pattern = Pattern.compile(";");
                Pattern pattern2 = Pattern.compile("；");
                String[] split = pattern.split(oldkeyword, 0);
                String[] split2 = pattern2.split(oldkeyword, 0);
                if (split.length > 1) {
                    newKeywords.addAll(Arrays.asList(split));
                } else if (split2.length > 1) {
                    newKeywords.addAll(Arrays.asList(split2));
                } else {
                    newKeywords.add(oldkeyword);
                }
            }
            strings = newKeywords;
        }
        return strings;
    }

    /**
     * Returns the Terms frequency List
     * @param termResult terms from the web service
     * @return List of terms
     */
    public static List<Term> asTermResults(TermResult termResult) {
        List<Term> terms = new ArrayList<>();
        if (termResult != null && termResult.getTerms() != null && termResult.getTerms().length > 0) {
            for (uk.ac.ebi.ddi.ebe.ws.dao.model.common.Term oldTerm: termResult.getTerms()) {
                terms.add(new Term(oldTerm.getText(), oldTerm.getFrequency()));
            }
        }
        return terms;
    }

    /**
     * Return a set of taxonomy ids from the Dataset List
     * @param queryResult the datasets to be anaylzed
     * @return a list of taxonomy ids
     */
    public static Set<String> getTaxonomyIds(QueryResult queryResult) {
        Set<String> ids = new HashSet<>();
        if (queryResult != null && queryResult.getEntries() != null && queryResult.getEntries().length > 0) {
            for (Entry entry: queryResult.getEntries()) {
               if (entry.getFields() != null && entry.getFields().containsKey(DSField.CrossRef.TAXONOMY.key())) {
                   Collections.addAll(ids, entry.getFields().get(DSField.CrossRef.TAXONOMY.key()));
               }
            }
        }
        return ids;
    }

    /**
     * Merge a set of queries and retrieve only one query. This function is specially interesting
     * when you have more than the limits of the entries to be query (100).
     * We are not merging the facets now because it is not interesting but in the future we can do it.
     * @param resultList List of QueryResult to be merge
     * @return One big QueryResult
     */
    public static QueryResult mergeQueryResult(List<QueryResult> resultList) {
        QueryResult result = new QueryResult();
        List<Entry> entries = new ArrayList<>();

        for (QueryResult query: resultList) {
            entries.addAll(Arrays.asList(query.getEntries()));
        }

        Entry[] entryArray = new Entry[entries.size()];

        for (int i = 0; i < entries.size(); i++) {
            entryArray[i] = entries.get(i);
        }

        result.setEntries(entryArray);
        result.setCount(entryArray.length);
        return result;
    }

    public static DatasetDetail addTaxonomy(DatasetDetail datasetDetail, QueryResult taxonomies) {
        List<Organism> organismList = new ArrayList<>();
        if (taxonomies != null && taxonomies.getEntries() != null && taxonomies.getEntries().length > 0) {
            for (Entry entry: taxonomies.getEntries()) {
                if (entry != null) {
                    String acc = entry.getId();
                    String name = entry.getFields().get(Constants.TAXONOMY_NAME)[0];
                    Organism organism = new Organism(acc, name);
                    organismList.add(organism);
                }
            }
        }
        //datasetDetail.setOrganisms(organismList);
        return datasetDetail;
    }
}
