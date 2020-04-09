package uk.ac.ebi.ddi.ws.modules.feedback;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.service.db.model.feedback.Feedback;
import uk.ac.ebi.ddi.service.db.service.feedback.IFeedbackService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;


/**
 * Created by gaur on 22/2/17.
 */
@Api(value = "feedback", description = "get feedback about search results")
@Controller
@RequestMapping(value = "/feedback")
@ApiIgnore
public class FeedbackController {

    @Autowired
    IFeedbackService feedbackService;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/saveFeedback", method = PUT)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public void saveFeedback(@RequestBody Feedback feedback, HttpServletRequest httpServletRequest) {
        feedback.setUserInfo(httpServletRequest.getRemoteAddr());
        feedbackService.save(feedback);
    }

    //@CrossOrigin
    @ApiOperation(value = "Retrieve all file feedbacks", position = 1,
            notes = "Retrieve all feedbacks for search results")
    @RequestMapping(value = "/getAllFeedbacks", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Feedback> getAllFeedbacks() {
        return feedbackService.readAll();
    }

    @ApiOperation(value = "Retrieve all file feedbacks by satisfaction status", position = 1,
            notes = "Retrieve all feedbacks for search results by satisfaction status")
    @RequestMapping(value = "/getFeedbackByStatus", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Feedback> getAllFeedbacksByStatus(@ApiParam(value = "satisfaction status of search result, e.g : true")
                                           @RequestParam(value = "isSatisfied", required = true) Boolean isSatisfied) {
        return feedbackService.read(isSatisfied);
    }
}
