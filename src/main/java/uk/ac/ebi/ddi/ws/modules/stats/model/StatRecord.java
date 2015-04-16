package uk.ac.ebi.ddi.ws.modules.stats.model;

/**
 * For all the starts we will use the record StatRecord that contain the value of the property and the value
 * for example number of entries in the resource:
 *    PRIDE   3000
 *
 * @author Yasset Perez-Riverol yperz@ebi.ac.uk
 */
public class StatRecord {

    private String label;

    private String value;

    public StatRecord(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getName() {
        return label;
    }

    public void setName(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
