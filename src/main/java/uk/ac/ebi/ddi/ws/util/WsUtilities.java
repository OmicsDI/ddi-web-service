package uk.ac.ebi.ddi.ws.util;

import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.IndexInfo;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.Domain;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.service.db.model.logger.HttpEvent;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author ypriverol
 */
public class WsUtilities {


    /**
     * Covet elements from a domain
     * @param domain
     * @return
     */

    public static String[] getSubdomainList(DomainList domain){
        List<String> domainList = new ArrayList<String>();
        if(domain != null && domain.list.length > 0){
            for(Domain domainInfo: domain.list){
                domainList.add(domainInfo.getName());
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

    public static List<DatasetSummary> transformDatasetSummary(QueryResult queryResult, String domain){

        if(queryResult != null && queryResult.getEntries() != null && queryResult.getEntries().length > 0){
            List<DatasetSummary> datasetSummaryList = new ArrayList<DatasetSummary>();
            for(Entry entry: queryResult.getEntries()) {
                DatasetSummary datasetSummary = new DatasetSummary();
                Map<String, String[]> fields = entry.getFields();
                String[] names = fields.get(Constants.NAME_FIELD);
                String[] descriptions = fields.get(Constants.DESCRIPTION_FIELD);
                String[] publication_dates = fields.get(Constants.PUB_DATE_FIELD);

                datasetSummary.setId(entry.getId());
                datasetSummary.setTitle(names[0]);
                datasetSummary.setDescription(descriptions[0]);
                datasetSummary.setPublicationDate(publication_dates[0]);
                datasetSummary.setSource(domain);
                datasetSummaryList.add(datasetSummary);
            }
            return datasetSummaryList;
        }
        return Collections.emptyList();
    }



}
