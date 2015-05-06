package uk.ac.ebi.ddi.ws.util;

import uk.ac.ebi.ddi.ebe.ws.dao.model.common.IndexInfo;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.Domain;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.service.db.model.logger.HttpEvent;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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

    public static Integer getNumberofEntries(String mainDomain, DomainList domain) {
        int count = 0;
        if(domain != null && domain.list.length > 0 && mainDomain != null){
            for(Domain domainInfo: domain.list){
                if(domainInfo.getName().equalsIgnoreCase(mainDomain)){
                    for(Domain subdomainInfo: domainInfo.getSubDomains()){
                        for(IndexInfo info: subdomainInfo.getIndexInfo())
                            if(info.getName().equalsIgnoreCase(Constants.ENTRY_COUNT))
                                count += Integer.parseInt(info.getValue());
                    }
                }
            }
        }
        return count;
    }

    public static HttpEvent tranformServletResquestToEvent(HttpServletRequest httpServletRequest){
        HttpEvent event = new HttpEvent();
        event.setAccessDate(new Date());
        event.setHost(httpServletRequest.getRemoteHost());
        event.setUser(httpServletRequest.getRemoteUser());
        event.setRawMessage(httpServletRequest.toString());
        event.setUserAgent(httpServletRequest.getHeader("User-Agent"));
        return event;
    }
}
