package uk.ac.ebi.ddi.ws.util;

import uk.ac.ebi.ddi.ddidomaindb.dataset.DSField;

/**
 * @author Yasset Perez-Riverol ypriverol
 */
public class Constants {

    private Constants() {
    }

    public static final String ENTRY_COUNT = "Number of entries";
    public static final String MAIN_DOMAIN = "omics";
    public static final String MODELEXCHANGE_DOMAIN = "modelexchange";
    public static final String REPOSITORY_TAG = "Repositories";
    public static final String PUB_DATE_FIELD = "publication_date";
    public static final String[] PUB_DATES = new String[] {PUB_DATE_FIELD, DSField.Date.PUBLICATION.key()};

    public static final String LAB_HEAD_FIELD = "labhead";

    public static final String LAB_HEAD_MAIL_FIELD = "labhead_mail";

    public static final String ENSEMBL = "ENSEMBL";

    public static final String UNIPROT = "UNIPROT";

    public static final String CHEBI = "CHEBI";

    public static final String BIOMODELS = "BioModels";

    public static final String[] DATASET_SUMMARY = {
            DSField.DESCRIPTION.key(),
            DSField.NAME.key(),
            DSField.Additional.SUBMITTER_KEYWORDS.key(),
            DSField.Additional.CURATOR_KEYWORDS.key(),
            Constants.PUB_DATE_FIELD,
            DSField.CrossRef.TAXONOMY.key(),
            DSField.Additional.OMICS.key(),
            Constants.ENSEMBL,
            Constants.UNIPROT,
            Constants.CHEBI,
            DSField.Additional.CITATION_COUNT.key(),
            DSField.Additional.VIEW_COUNT.key(),
            DSField.Additional.REANALYSIS_COUNT.key(),
            DSField.Additional.SEARCH_COUNT.key(),
            DSField.Additional.VIEW_COUNT_SCALED.key(),
            DSField.Additional.REANALYSIS_COUNT_SCALED.key(),
            DSField.Additional.CITATION_COUNT_SCALED.key(),
            DSField.Additional.SEARCH_COUNT_SCALED.key(),
            DSField.Additional.DOWNLOAD_COUNT.key(),
            DSField.Additional.DOWNLOAD_COUNT_SCALED.key()
    };


    public static final String[] DATASET_DETAIL = {
            DSField.NAME.key(),
            DSField.DESCRIPTION.key(),
            Constants.PUB_DATE_FIELD,
            DSField.Additional.LINK.key(),
            DSField.Additional.DATA.key(),
            DSField.Additional.SAMPLE.key(),
            DSField.Additional.INSTRUMENT.key(),
            DSField.Additional.TECHNOLOGY_TYPE.key(),
            DSField.CrossRef.PUBMED.key(),
            DSField.Additional.SUBMITTER_KEYWORDS.key(),
            DSField.Additional.CURATOR_KEYWORDS.key(),
            DSField.CrossRef.TAXONOMY.key(),
            DSField.Additional.DISEASE_FIELD.key(),
            DSField.Additional.OMICS.key(),
            DSField.Additional.TISSUE_FIELD.key(),
            DSField.Additional.SUBMITTER_AFFILIATION.key(),
            DSField.DATES.key(),
            DSField.Additional.SUBMITTER.key(),
            DSField.Additional.SUBMITTER_MAIL.key(),
            Constants.LAB_HEAD_FIELD,
            Constants.LAB_HEAD_MAIL_FIELD};

    public static final String[] MORELIKE_FIELDS = {
            DSField.NAME.key(),
            DSField.DESCRIPTION.key(),
            DSField.Additional.DATA.key(),
            DSField.Additional.SAMPLE.key(),
            DSField.Additional.OMICS.key()
    };


    public static final String NOT_AVAILABLE = "Not available";

    public static final String NOT_APPLICABLE = "not applicable";

    public static final String DATASET_FILE = "dataset_file";


    public static final String TAXONOMY_NAME = "name";

    public static final String[] TAXONOMY_FIELDS = {Constants.TAXONOMY_NAME};

    public static final String[] EXCLUSION_WORDS = {
            "ega", "study", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this",
            "to", "was", "will", "with", "able", "about", "across", "after", "all", "almost", "also", "am", "among",
            "an", "and", "any", "are", "as", "at", "be", "because", "been", "can", "could", "dear", "did",
            "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have",
            "he", "her", "hers", "him", "how", "however", "i", "in", "into", "its", "just", "least", "let", "like",
            "likely", "may", "me", "might", "most", "must", "neither", "no", "nor", "not", "of", "off",
            "often", "on", "only", "or", "other", "our", "own", "rather", "should", "since", "so", "some", "than",
            "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "us", "was",
            "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would",
            "yet", "you", "your", "protein", "proteomics", "proteomic", "proteome", "proteomes", "mass", "proteins",
            "lc", "ms", "based", "from", "using", "during", "LC-MS", "LC-MS/MS", "reveals", "as", "non", "data"};

    public static final String[] SHORT_EXCLUSION_WORDS = {"ega", "study", "data",
            "using", "10", "available", "da", "two", "protein", "proteins",
            "peptide", "peptides", "20", "80", "24", "30", "50", "0", "100",
            "15", "24", "rna", "cell", "between", "mouse", "used", "human", "each",
            "dna", "both", "total", "three", "mice", "one", "type", "one", "identify", "here",
            "response", "identified", "different", "replicates", "high", "profiling",
            "response", "log2", "raw", "br", "ratio", "values", "rma", "cel", "processed", "non",
            "time", "changes", "role", "profile", "array", "wild", "specific", "br",
            "version", "files", "file", "array", "arrays", "microarray", "analyzed",
            "cy5", "intensities", "gene", "results", "tissue", "profiles", "levels",
            "associated", "agilent", "quantile", "cy3", "set", "affymetrix", "during",
            "well", "found", "treatment", "treated", "wide", "growth", "chip", "log",
            "package", "default", "reads", "method", "probe", "standard",
            "genome", "mrna", "isolated", "design", "expressed", "show", "lines", "genechip",
            "detection", "median", "genes", "images", "protocol", "al", "et", "images",
            "bioconductor", "four", "microarrays", "development", "seq", "conditions", "based",
            "involved", "control", "mean", "calculated", "parameters", "illumina", "reference",
            "3000", "http", "days", "under", "individual",
            "biological", "compared", "experiment", "transcription", "induced", "global",
            "genome_build", "scanning", "test", "scanning", "scaning", "cell", "whole", "model", "performed",
            "more target", "factors", "hours",
            "hour", "mutant", "cells", "transcriptional", "regulated", "cancer", "function", "normal", "12"

    };

    public static final String TAXONOMY_DOMAIN = "taxonomy";

    public static final int HIGH_QUERY_THRESHOLD = 100;

    public static final String PRIDE_DOMAIN = "pride";

    public static final String ORDER_ASCENDING = "ascending";

    public static final String ORDER_DESCENDING = "descending";

    public static final String PUBMED_AUTHOR_FIELD = "author";

    public static final String PUBMED_ABSTRACT_FIELD = "description";

    public static final String PUBMED_ID_FIELD = "id";

    public static final String PUBMED_ISSUE_FIELD = "issue";

    public static final String PUBMED_JOURNAL_FIELD = "journal";

    public static final String PUBMED_KEYS_FIELD = "keywords";

    public static final String PUBMED_NAME_FIELD = "name";

    public static final String PUBMED_PAG_FIELD = "pagination";

    public static final String PUBMED_VOL_FIELD = "volume";

    public static final String PUBMED_DATE_FIELD = "publication_date";

    public static final String PUBMED_AFFILATION_FIELD = "affiliation";


    public static final String[] PUBLICATION_SUMMARY = {Constants.PUBMED_ABSTRACT_FIELD,
            Constants.PUBMED_AUTHOR_FIELD,
            Constants.PUBMED_DATE_FIELD,
            Constants.PUBMED_ID_FIELD,
            Constants.PUBMED_JOURNAL_FIELD,
            Constants.PUBMED_ISSUE_FIELD,
            Constants.PUBMED_KEYS_FIELD,
            Constants.PUBMED_NAME_FIELD,
            Constants.PUBMED_PAG_FIELD,
            Constants.PUBMED_VOL_FIELD,
            Constants.PUBMED_AFFILATION_FIELD};

    //Todo: We need to do this dynamic

    public static final String[] INITIAL_DOMAINS = {"omics"};
}
