package uk.ac.ebi.ddi.ws.modules.publication.controller;

/**
 * @author Yasset Perez-Riverol ypriverol
 */

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.ebe.ws.dao.client.dataset.DatasetWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.client.publication.PublicationWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.Entry;
import uk.ac.ebi.ddi.ebe.ws.dao.model.common.QueryResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.SimilarResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.dataset.TermResult;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.service.db.model.logger.DatasetResource;
import uk.ac.ebi.ddi.service.db.model.logger.HttpEvent;
import uk.ac.ebi.ddi.service.db.service.logger.DatasetResourceService;
import uk.ac.ebi.ddi.service.db.service.logger.HttpEventService;
import uk.ac.ebi.ddi.service.db.utils.Tuple;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DataSetResult;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;

import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetDetail;
import uk.ac.ebi.ddi.ws.modules.dataset.model.PubmedPublication;
import uk.ac.ebi.ddi.ws.modules.dataset.model.Term;
import uk.ac.ebi.ddi.ws.modules.dataset.util.RepoDatasetMapper;
import uk.ac.ebi.ddi.ws.modules.publication.model.PublicationDetail;
import uk.ac.ebi.ddi.ws.modules.publication.model.PublicationResult;
import uk.ac.ebi.ddi.ws.modules.publication.util.PubmedUtils;
import uk.ac.ebi.ddi.ws.util.Constants;
import uk.ac.ebi.ddi.ws.util.WsUtilities;

import javax.servlet.http.HttpServletRequest;
import uk.ac.ebi.ddi.ws.modules.dataset.util.PubmedUtil;

import java.util.*;


@Api(value = "publication", description = "Retrieve the information about the publication including search functionalities", position = 0)
@Controller
@RequestMapping(value = "/publication")

public class PublicationController {

    private static final Logger logger = LoggerFactory.getLogger(uk.ac.ebi.ddi.ws.modules.publication.controller.PublicationController.class);

    @Autowired
    PublicationWsClient publicationWsClient;

    @ApiOperation(value = "Retrieve a set of publications by Ids", position = 1, notes = "Retrieve a set of publications by Ids")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public @ResponseBody
    PublicationResult list(
            @ApiParam(value = "Accession of the publications to be retrieved")
            @RequestParam(value = "acc", required = true) String[] accs){

        PublicationResult publicationResult = new PublicationResult();
        if(accs != null && accs.length > 0){
            Set<String> ids = new HashSet<String>();
            for(String acc: accs)
                 if(acc != null && acc.length() >0)
                     ids.add(acc);

            QueryResult queryResult = publicationWsClient.getPublications(Constants.PUBLICATION_SUMMARY, ids);

            List<PublicationDetail> publications = PubmedUtils.transformPublication(queryResult);
            publicationResult.setPublications(publications);
            publicationResult.setCount(publications.size());
        }

        return publicationResult;

    }

}
