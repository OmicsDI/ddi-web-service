package uk.ac.ebi.ddi.ws.util;

import uk.ac.ebi.ddi.ebe.ws.dao.utils.Constans;

/**
 * @author Yasset Perez-Riverol ypriverol
 */
public class Constants {

    public static final String ENTRY_COUNT           = "Number of entries";

    public static final String TAXONOMY_FIELD        = "TAXONOMY";

    public static final String MAIN_DOMAIN           = "omics";

    public static final String REPOSITORY_TAG        = "Repositories";

    public static final String DATASET_TAGS          = "Datasets";

    public static final String CONTRIBUTORS_TAG      = "Contributors";

    public static final String SPECIES_TAG           = "Different Species";

    public static final String MANUSCRIPT_TAG        = "Manuscripts";

    public static final String TISSUE_FIELD          = "tissue" ;

    public static final String OMICS_TYPE_FIELD      = "omics_type";

    public static final String DISEASE_FIELD         = "disease";

    public static String DESCRIPTION_FIELD           = "description";

    public static String NAME_FIELD                  = "name";

    public static String SUBMITTER_KEY_FIELD         = "submitter_keywords";

    public static String CURATOR_KEY_FIELD           = "curator_keywords";

    public static String PUB_DATE_FIELD              = "publication_date";

    public static String[] DATASET_SUMMARY           = {Constants.DESCRIPTION_FIELD,
                                                        Constants.NAME_FIELD,
                                                        Constants.SUBMITTER_KEY_FIELD,Constants.CURATOR_KEY_FIELD,
                                                        Constants.PUB_DATE_FIELD};


}
