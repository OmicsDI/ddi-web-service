package uk.ac.ebi.ddi.ws.modules.dblucene;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.service.db.model.dblucene.DbLuceneMapping;
import uk.ac.ebi.ddi.service.db.service.dblucene.IDbLuceneMappingService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by gaur on 29/3/17.
 */
@Api(value = "dblucenemapping", description = "get feedback about search results", position = 0)
@Controller
@RequestMapping(value = "/dblucene")
@ApiIgnore
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DbLuceneMappingController {

    @Autowired
    IDbLuceneMappingService dbLuceneMappingService;

    @ApiOperation(value = "Retrieve all mappings", position = 1, notes = "Retrieve all mappings")
    @RequestMapping(value = "/getAllMappings", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DbLuceneMapping> getAllMappings() {
        return dbLuceneMappingService.readAll();
    }

    @ApiOperation(value = "Retrieve lucene name", position = 1, notes = "Retrieve lucene name")
    @RequestMapping(value = "/getLuceneName", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DbLuceneMapping> getLuceneName(@ApiParam(value = "database name, e.g : PRIDE")
                                                   @RequestParam(value = "dbName", required = true) String dbName) {
        return dbLuceneMappingService.getLuceneName(dbName);
    }

    @ApiOperation(value = "Retrieve database name", position = 1, notes = "Retrieve database name")
    @RequestMapping(value = "/getDbName", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DbLuceneMapping> getDbName(
            @ApiParam(value = "lucene name, e.g : pride")
            @RequestParam(value = "luceneName", required = true) String luceneName) {
        return dbLuceneMappingService.getDatabase(luceneName);
    }

    @RequestMapping(value = "/saveMapping", method = PUT)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public void saveFeedback(@RequestBody DbLuceneMapping dbLuceneMapping,
                                    HttpServletRequest httpServletRequest) {
        dbLuceneMappingService.save(dbLuceneMapping);
    }
}
