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
     * Name of the entry
     */
    String name = null;

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
     * List of Related Datasets for this dataset, can be one or more than one related datasets
     */
    List<DatasetSummary> relatedDatasets;


    /*
     * Data Protocol of the dataset
     */
   String data_protocol = null;

    /*
     * Sample Protocol of the dataset
     */
    String sample_protocol = null;

    public DatasetDetail() {
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

//    public List<Organism> getOrganisms() {
//        return organisms;
//    }
//
//    public void setOrganisms(List<Organism> organisms) {
//        this.organisms = organisms;
//    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

//    public String getSource() {
//        return source;
//    }
//
//    public void setSource(String source) {
//        this.source = source;
//    }

    public List<PubmedPublication> getPublications() {
        return publications;
    }

    public void setPublications(List<PubmedPublication> publications) {
        this.publications = publications;
    }

    public List<DatasetSummary> getRelatedDatasets() {
        return relatedDatasets;
    }

    public void setRelatedDatasets(List<DatasetSummary> relatedDatasets) {
        this.relatedDatasets = relatedDatasets;
    }

    public String getData_protocol() {
        return data_protocol;
    }

    public void setData_protocol(String data_protocol) {
        this.data_protocol = data_protocol;
    }

    public String getSample_protocol() {
        return sample_protocol;
    }

    public void setSample_protocol(String sample_protocol) {
        this.sample_protocol = sample_protocol;
    }
}
