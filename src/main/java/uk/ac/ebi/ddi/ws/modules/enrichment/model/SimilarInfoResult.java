package uk.ac.ebi.ddi.ws.modules.enrichment.model;

import uk.ac.ebi.ddi.ws.util.Triplet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mingze on 02/11/15.
 */
public class SimilarInfoResult {

    private String accession;
    private String database;
    private Set<Triplet> Scores ;

    public SimilarInfoResult(String accession, String database, Set<Triplet> scores) {
        this.accession = accession;
        this.database = database;
        Scores = scores;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Set<Triplet> getScores() {
        return Scores;
    }

    public void setScores(Set<Triplet> scores) {
        Scores = scores;
    }
}
