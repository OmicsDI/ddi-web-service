package uk.ac.ebi.ddi.ws.modules.dataset.util;

import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.TermResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;
import uk.ac.ebi.ddi.ws.modules.dataset.model.Organism;
import uk.ac.ebi.ddi.ws.modules.dataset.model.Term;
import uk.ac.ebi.ddi.ws.util.Constants;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yasset Perez-Riverol ypriverol
 */

public class RepoDatasetMapper {

    /**
     * Transform the information form the query to the Web service strucutre
     * @param queryResults The original results from the Query
     * @return             The set of datasets form the query
     */
    public static DataSetResult asDataSummary(QueryResult queryResults){

        DataSetResult dataset = new DataSetResult();

        List<DatasetSummary> datasets = new ArrayList<DatasetSummary>();

        dataset.setCount(queryResults.getCount());

        if(queryResults.getFacets() != null && queryResults.getFacets().length > 0)
            dataset.setFacets(Arrays.asList(queryResults.getFacets()));

        if(queryResults.getEntries() != null && queryResults.getEntries().length > 0){
          for(Entry entry: queryResults.getEntries())
              datasets.add(RepoDatasetMapper.transformDatasetSummary(entry));
        }
        dataset.setDatasets(datasets);

        return dataset;
    }

    /**
     * Transform a web-service entry to a DatasetSummary
     * @param entry the original entry from the dataset
     * @return
     */
    private static DatasetSummary transformDatasetSummary(Entry entry){

        DatasetSummary datasetSummary = new DatasetSummary();

        if(entry != null){
            /**
             * Set the id of the entry
             */
            datasetSummary.setId(entry.getId());

            datasetSummary.setSource(entry.getSource());

            if(entry.getFields() != null){
                if(entry.getFields().containsKey(Constants.NAME_FIELD))
                    if(entry.getFields().get(Constants.NAME_FIELD) != null && entry.getFields().get(Constants.NAME_FIELD).length > 0)
                        datasetSummary.setTitle(entry.getFields().get(Constants.NAME_FIELD)[0]);

                if(entry.getFields().containsKey(Constants.DESCRIPTION_FIELD))
                    if(entry.getFields().get(Constants.DESCRIPTION_FIELD) != null && entry.getFields().get(Constants.DESCRIPTION_FIELD).length > 0)
                        datasetSummary.setDescription(entry.getFields().get(Constants.DESCRIPTION_FIELD)[0]);

                if(entry.getFields().containsKey(Constants.PUB_DATE_FIELD))
                    if(entry.getFields().get(Constants.PUB_DATE_FIELD) != null && entry.getFields().get(Constants.PUB_DATE_FIELD).length > 0)
                        datasetSummary.setPublicationDate(entry.getFields().get(Constants.PUB_DATE_FIELD)[0]);

                List<String> keywords = new ArrayList<String>();

                if(entry.getFields().containsKey(Constants.CURATOR_KEY_FIELD))
                    if(entry.getFields().get(Constants.CURATOR_KEY_FIELD) != null && entry.getFields().get(Constants.CURATOR_KEY_FIELD).length > 0)
                        keywords.addAll(Arrays.asList(entry.getFields().get(Constants.CURATOR_KEY_FIELD)));

                if(entry.getFields().containsKey(Constants.SUBMITTER_KEY_FIELD))
                    if(entry.getFields().get(Constants.SUBMITTER_KEY_FIELD) != null && entry.getFields().get(Constants.SUBMITTER_KEY_FIELD).length > 0)
                        keywords.addAll(Arrays.asList(entry.getFields().get(Constants.SUBMITTER_KEY_FIELD)));

                if(keywords.size() > 0){
                    String[] arrayKeywords = new String[keywords.size()];
                    for(int i = 0; i < keywords.size(); i++)
                        arrayKeywords[i] = keywords.get(i);
                    datasetSummary.setKeywords(arrayKeywords);
                }

                List<Organism> organisms = new ArrayList<Organism>();

                //Todo: get the organisms

//                if(entry.getFields().containsKey(Constants.TAXONOMY_FIELD))
//                    if(entry.getFields().get(Constants.TAXONOMY_FIELD) != null && entry.getFields().get(Constants.TAXONOMY_FIELD).length > 0){
//
//                    }
//                        organisms.addAll(Arrays.asList(entry.getFields().get(Constants.TAXONOMY_FIELD)));

            }
        }
        return datasetSummary;

    }

    /**
     * Returns the Terms frequency List
     * @param termResult terms from the web service
     * @return List of terms
     */
    public static List<Term> asTermResults(TermResult termResult) {
        List<Term> terms = new ArrayList<Term>();
        if(termResult != null && termResult.getTerms() != null && termResult.getTerms().length > 0){
            for(uk.ac.ebi.ddi.ebe.ws.dao.model.common.Term oldTerm: termResult.getTerms())
                terms.add(new Term(oldTerm.getText(), oldTerm.getFrecuency()));
        }
        return terms;
    }
}
