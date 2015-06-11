package uk.ac.ebi.ddi.ws.modules.dataset.model;

import java.util.ArrayList;
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
    List<String> publicationIds;

    /*
     * Data Protocol of the dataset
     */
    List<Protocol> protocols = null;

    /**
     * List of instruments related with the experiment
     */
    List<String> instruments    = null;

    /**
     * Keywords related wit the type of the experiment, this keywords are
     * assigned by the DDI system
     *
     */

    List<String> experimentType = null;
    /**
     * Lab members are those people related with the dataset
     * it can be the submitter, the head of the lab or even the
     * collaborators using the Paper information.
     */

    List<LabMember> labMembers = null;

    /**
     * The full dataset link is the original home of the dataset in the repository
     * as provided by the repositories.
     */
    String full_dataset_link   = null;

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

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setPublications(List<String> publications) {
        this.publicationIds = publications;
    }

    public void setArrayPublicationIds(String[] ids){
        if(ids != null && ids.length> 0){
            publicationIds = new ArrayList<String>();
            for(String id: ids)
                if(id != null && id.length() > 0)
                    publicationIds.add(id);
        }
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Organism> getOrganisms() {
        return organisms;
    }

    public void setOrganisms(List<Organism> organisms) {
        this.organisms = organisms;
    }

    public List<Protocol> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    public List<LabMember> getLabMembers() {
        return labMembers;
    }

    public void setLabMembers(List<LabMember> labMembers) {
        this.labMembers = labMembers;
    }

    public String getFull_dataset_link() {
        return full_dataset_link;
    }

    public void setFull_dataset_link(String full_dataset_link) {
        this.full_dataset_link = full_dataset_link;
    }

    public List<String> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<String> instruments) {
        this.instruments = instruments;
    }

    public List<String> getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(List<String> experimentType) {
        this.experimentType = experimentType;
    }

    /**
     * Add multiple protocols with the same name but different descriptions.
     * @param protocolField the protocol Name
     * @param protocol_descriptions the Protocol descriptions
     */
    public void addProtocols(String protocolField, String[] protocol_descriptions) {
        if(protocols == null)
            protocols = new ArrayList<Protocol>();
        if(protocol_descriptions != null && protocol_descriptions.length > 0 && protocolField != null){
            for(String protocol: protocol_descriptions){
                if(protocol != null && protocol.length() > 0){
                    protocols.add(new Protocol(protocolField, protocol));
                }
            }
        }
    }

    /**
     * Set a list of instrument types from an array of elements
     * @param instruments An array of Instruments.
     */
    public void setArrayInstruments(String[] instruments) {
         if(instruments != null && instruments.length > 0){
             this.instruments = new ArrayList<String>();
             for(String instrument: instruments){
                if(instrument != null && instrument.length() >0)
                    this.instruments.add(instrument);
            }
        }
    }

    /**
     * Set the list of experiment types from an Array of elements
     * @param experimentTypes An array of Experiment type terms like Mass Spectrometry, etc
     */
    public void setArrayExperimentType(String[] experimentTypes) {
        if(experimentTypes != null && experimentTypes.length > 0){
            this.experimentType = new ArrayList<String>();
            for(String experimentType: experimentTypes){
                if(experimentType != null && experimentType.length() > 0)
                    this.experimentType.add(experimentType);
            }
        }
    }
}
