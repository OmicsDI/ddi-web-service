package uk.ac.ebi.ddi.ws.modules.dataset.model;

import java.util.List;

/**
 * This is the summary dataset entry for all the omics experiments. Using the current ws data model we fill the information of
 * this DatasetSummary to be use by the clients.
 *
 * @author ypriverol
 */
public class DatasetSummary {

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

    /**
     * Score for the similarity
     */
    String score;

    List<String> omicsType = null;

    int citationsCount;

    int connectionsCount;

    int reanalysisCount;

    int viewsCount;

    int downloadCount;

    double citationsCountScaled;

    double connectionsCountScaled;

    double reanalysisCountScaled;

    double viewsCountScaled;

    double downloadCountScaled;


    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public double getDownloadCountScaled() {
        return downloadCountScaled;
    }

    public void setDownloadCountScaled(double downloadCountScaled) {
        this.downloadCountScaled = downloadCountScaled;
    }

    public double getConnectionsCountScaled() {
        return connectionsCountScaled;
    }

    public void setConnectionsCountScaled(double connectionsCountScaled) {
        this.connectionsCountScaled = connectionsCountScaled;
    }

    public double getReanalysisCountScaled() {
        return reanalysisCountScaled;
    }

    public void setReanalysisCountScaled(double reanalysisCountScaled) {
        this.reanalysisCountScaled = reanalysisCountScaled;
    }

    public double getViewsCountScaled() {
        return viewsCountScaled;
    }

    public void setViewsCountScaled(double viewsCountScaled) {
        this.viewsCountScaled = viewsCountScaled;
    }

    public double getCitationsCountScaled() {
        return citationsCountScaled;
    }

    public void setCitationsCountScaled(double citationsCountScaled) {
        this.citationsCountScaled = citationsCountScaled;
    }

    public int getCitationsCount() {
        return citationsCount;
    }

    public void setCitationsCount(int citationsCount) {
        this.citationsCount = citationsCount;
    }

    public int getConnectionsCount() {
        return connectionsCount;
    }

    public void setConnectionsCount(int connectionsCount) {
        this.connectionsCount = connectionsCount;
    }

    public int getReanalysisCount() {
        return reanalysisCount;
    }

    public void setReanalysisCount(int reanalysisCount) {
        this.reanalysisCount = reanalysisCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Boolean getClaimable() {
        return isClaimable;
    }

    public void setClaimable(Boolean claimable) {
        isClaimable = claimable;
    }

    Boolean isClaimable = false;

    public List<String> getOmicsType() {
        return omicsType;
    }

    public void setOmicsType(List<String> omicsType) {
        this.omicsType = omicsType;
    }

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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }




}
