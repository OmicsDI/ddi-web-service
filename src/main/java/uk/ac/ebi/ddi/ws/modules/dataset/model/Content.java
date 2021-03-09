package uk.ac.ebi.ddi.ws.modules.dataset.model;

public class Content {

    private String id;

    private String name;

    private String drsURI;

    private String drsURL;

    public String getDrsURI() {
        return drsURI;
    }

    public void setDrsURI(String drsURI) {
        this.drsURI = drsURI;
    }

    public String getDrsURL() {
        return drsURL;
    }

    public void setDrsURL(String drsURL) {
        this.drsURL = drsURL;
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



}
