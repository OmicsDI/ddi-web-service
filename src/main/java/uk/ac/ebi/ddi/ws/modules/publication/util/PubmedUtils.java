package uk.ac.ebi.ddi.ws.modules.publication.util;


import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.ws.modules.publication.model.PublicationDetail;
import uk.ac.ebi.ddi.ws.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @ypriverol
 */
public class PubmedUtils {

    private PubmedUtils() {
    }

    /**
     * Retirve the List of publications from the QueryResult
     * @param result
     * @return
     */
   public static List<PublicationDetail> transformPublication(QueryResult result) {

       List<PublicationDetail> publications = new ArrayList<>();

       if (result != null && result.getEntries() != null && result.getEntries().length > 0) {
           for (Entry entry: result.getEntries()) {
               PublicationDetail publication = transformDatasetSummary(entry);
               if (publication != null) {
                   publications.add(publication);
               }
           }
       }
       return publications;

   }

    /**
     * Transform a web-service entry to a PublicationDetail
     * @param entry the original entry from the dataset
     * @return a Dataset Summary
     */
    private static PublicationDetail transformDatasetSummary(Entry entry) {

        PublicationDetail publication = new PublicationDetail();

        if (entry == null) {
            return publication;
        }

        publication.setId(entry.getId());
        publication.setSource(entry.getSource());

        if (entry.getFields() == null) {
            return publication;
        }

        Map<String, String[]> fields = entry.getFields();

        if (fields.get(Constants.PUBMED_NAME_FIELD) != null && fields.get(Constants.PUBMED_NAME_FIELD).length > 0) {
            publication.setTitle(fields.get(Constants.PUBMED_NAME_FIELD)[0]);
        }

        if (fields.get(Constants.PUBMED_ABSTRACT_FIELD) != null
                && fields.get(Constants.PUBMED_ABSTRACT_FIELD).length > 0) {
            publication.setPubAbstract(fields.get(Constants.PUBMED_ABSTRACT_FIELD));
        }

        if (fields.get(Constants.PUBMED_DATE_FIELD) != null && fields.get(Constants.PUBMED_DATE_FIELD).length > 0) {
            publication.setDate(fields.get(Constants.PUBMED_DATE_FIELD)[0]);
        }

        if (fields.get(Constants.PUBMED_KEYS_FIELD) != null && fields.get(Constants.PUBMED_KEYS_FIELD).length > 0) {
            publication.setKeywords(fields.get(Constants.PUBMED_KEYS_FIELD));
        }

        if (fields.get(Constants.PUBMED_AUTHOR_FIELD) != null && fields.get(Constants.PUBMED_AUTHOR_FIELD).length > 0) {
            publication.setAuthors(fields.get(Constants.PUBMED_AUTHOR_FIELD));
        }

        if (fields.get(Constants.PUBMED_JOURNAL_FIELD) != null
                && fields.get(Constants.PUBMED_JOURNAL_FIELD).length > 0) {
            publication.setJournal(fields.get(Constants.PUBMED_JOURNAL_FIELD)[0]);
        }

        if (fields.get(Constants.PUBMED_ISSUE_FIELD) != null && fields.get(Constants.PUBMED_ISSUE_FIELD).length > 0) {
            publication.setIssue(fields.get(Constants.PUBMED_ISSUE_FIELD)[0]);
        }

        if (fields.get(Constants.PUBMED_PAG_FIELD) != null && fields.get(Constants.PUBMED_PAG_FIELD).length > 0) {
            publication.setPagination(fields.get(Constants.PUBMED_PAG_FIELD)[0]);
        }

        if (fields.get(Constants.PUBMED_VOL_FIELD) != null && fields.get(Constants.PUBMED_VOL_FIELD).length > 0) {
            publication.setVolume(fields.get(Constants.PUBMED_VOL_FIELD)[0]);
        }

        if (fields.get(Constants.PUBMED_AFFILATION_FIELD) != null
                && fields.get(Constants.PUBMED_AFFILATION_FIELD).length > 0) {
            publication.setAffiliation(fields.get(Constants.PUBMED_AFFILATION_FIELD));
        }
        return publication;
    }
}
