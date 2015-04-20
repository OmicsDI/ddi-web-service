package uk.ac.ebi.ddi.ws.util;

import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.Domain;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ypriverol
 */
public class WsUtilities {

    /**
     * Covet elements from a domain
     * @param parentDomain
     * @param domain
     * @return
     */

    public static String[] getSubdomainList(String parentDomain, DomainList domain){
        List<String> domainList = new ArrayList<String>();
        if(domain != null && domain.list.length > 0 && parentDomain != null){
            for(Domain domainInfo: domain.list){
                if(domainInfo.getName().equalsIgnoreCase(parentDomain)){
                    for(Domain subdomainInfo: domainInfo.getSubDomains()){
                        domainList.add(subdomainInfo.getName());
                    }
                }
            }
        }
        String[] records = new String[domainList.size()];
        for(int i = 0; i < domainList.size(); i++)
            records[i] = domainList.get(i);
        return records;
    }
}
