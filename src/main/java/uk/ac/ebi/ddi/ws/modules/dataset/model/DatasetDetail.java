package uk.ac.ebi.ddi.ws.modules.dataset.model;

import java.util.List;

/**
 * This is the Detailed dataset entry for all the omics experiments. Using the current ws data model we fill the information of
 * this DatasetDetail to be use by the clients.
 *
 * @author ypriverol
 */
public class DatasetDetail {

    /**
     * Id of an Entry
     */
    String id = null;

    /**
     * Source for the entry
     */
    String source = null;

    /**
     * Title of the entry
     */
    String title = null;

    /**
     * Publication date of the dataset
     */
    String description = null;

    /**
     * List of keywords or tags associated with the dataset.
     */
    String[] keywords = null;

    /*
     * List of species for this datset, can be one or more than one specie.
     */
    List<Organism> organisms;

    /**
     * Publication date
     */
    String publicationDate = null;

    /*
     * List of Publications for this dataset, can be one or more than one publications
     */
    List<PubmedPublication> publications;


    /*
     * List of Related Datasets for this dataset, can be one or more than one publications
     */
    List<DatasetSummary> relatedDatasets;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public List<Organism> getOrganisms() {
        return organisms;
    }

    public void setOrganisms(List<Organism> organisms) {
        this.organisms = organisms;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
