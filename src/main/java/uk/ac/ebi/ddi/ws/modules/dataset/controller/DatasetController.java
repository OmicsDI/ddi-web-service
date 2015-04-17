package uk.ac.ebi.ddi.ws.modules.dataset.controller;

/**
 * @author Yasset Perez-Riverol ypriverol
 */

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.ebi.ddi.ebe.ws.dao.client.domain.DomainWsClient;
import uk.ac.ebi.ddi.ebe.ws.dao.model.domain.DomainList;
import uk.ac.ebi.ddi.ws.modules.dataset.model.DatasetSummary;

import uk.ac.ebi.ddi.ws.util.Constants;

import java.util.List;

@Api(value = "/dataset", description = "Retrieve the information about the dataset including search functionalities", position = 0)
@Controller
@RequestMapping(value = "/dataset")

public class DatasetController {
//
//    private static final Logger logger = LoggerFactory.getLogger(DatasetController.class);
//
//    @Autowired
//    DomainWsClient domainWsClient;
//
//    @ApiOperation(value = "Search for datasets in the resource", position = 1, notes = "retrieve datasets in the resource using different queries")
//    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.OK) // 200
//    public @ResponseBody
//    List<DatasetSummary> search() {
//
//        DomainList domain = domainWsClient.getDomainByName(Constants.MAIN_DOMAIN);
//
//        return null;
//    }
}
