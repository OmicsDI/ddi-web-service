package uk.ac.ebi.ddi.ws.modules.stats.util;

import uk.ac.ebi.ddi.ebe.ws.dao.model.common.IndexInfo;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.Domain;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.ws.modules.stats.model.DomainStats;
import uk.ac.ebi.ddi.ws.modules.stats.model.StatRecord;
import uk.ac.ebi.ddi.ws.util.Constants;


import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class maps the statistics from repo level to the web service level
 *
 * @author Yasset Perez-Riverol ypriverol@gmail.com
 */
public final class RepoStatsToWsStatsMapper {

    public static List<DomainStats> asDomainStatsList(DomainList domainList) {

        List<DomainStats> domains = new ArrayList<DomainStats>();

        if(domainList != null && domainList.list != null && domainList.list.length > 0){
            for(Domain domain: domainList.list){
                domains.add(domainStats(domain));
            }
        }
        return domains;
    }

    public static DomainStats domainStats(Domain domain){
        DomainStats domainStasts = null;
        if(domain != null){
            domainStasts = new DomainStats();
            StatRecord record = new StatRecord(domain.getName(), null);
            if(domain.getIndexInfo() != null && domain.getIndexInfo().length > 0){
                for(IndexInfo info: domain.getIndexInfo()){
                    if(info != null && info.getName().equalsIgnoreCase(Constants.ENTRY_COUNT)){
                        record.setValue(info.getValue());
                        break;
                    }
                }
            }
            domainStasts.setdomain(record);
            if(domain.getSubDomains() != null && domain.getSubDomains().length >0){
                List<DomainStats> subdomains = new ArrayList<DomainStats>();
                for(Domain subDomain: domain.getSubDomains())
                    subdomains.add(domainStats(subDomain));
                domainStasts.setSubdomains(subdomains);
            }
        }
        return domainStasts;
    }


}
