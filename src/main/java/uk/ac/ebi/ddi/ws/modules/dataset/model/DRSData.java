package uk.ac.ebi.ddi.ws.modules.dataset.model;

import java.util.ArrayList;
import java.util.List;

public class DRSData {

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public DRSData() {
        this.contents = new ArrayList<>();
    }

    List<Content> contents;

   /* public String getId() {
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

    String id;

    String name;*/


}
