package uk.ac.ebi.ddi.ws.modules.database.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.ddi.service.db.service.database.DatabaseService;
import uk.ac.ebi.ddi.service.db.model.database.DatabaseDetail;
import uk.ac.ebi.ddi.service.db.service.database.DatabaseDetailService;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * Created by root on 16.05.17.
 */

@Api(value = "database", description = "Retrieve information about databases")
@RestController
@RequestMapping("/database")
public class DatabaseController {

    @Autowired
    DatabaseDetailService databaseDetailService;

    @Autowired
    DatabaseService databaseService;

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // 200
    public List<DatabaseDetail> getDatabaseList() {
        return databaseDetailService.getDatabaseList();
    }
    @Autowired
    ServletContext servletContext;

    @ApiIgnore
    @RequestMapping(value = "/{databaseName}/picture", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getDatabasePicture(@PathVariable String databaseName, final HttpServletResponse response)
            throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        DatabaseDetail databaseDetail = databaseDetailService.findDatabaseByName(databaseName);
        byte[] image = databaseDetail.getImage();
        if (null == image) {
            InputStream in = servletContext.getResourceAsStream("no_image.jpg");
            image = IOUtils.toByteArray(in);
        }
        return image;
    }
    /**
     * Function for initial load, not used
     **/
    private void initLocalData() {

        InputStream databasesInputStream = this.getClass().getResourceAsStream("/databases.json");
        String databaseJSONString = null;
        try {
            databaseJSONString = IOUtils.toString(databasesInputStream);
            databasesInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray databaseArray = new JSONArray(databaseJSONString);
        for (int i = 0; i < databaseArray.length(); i++) {
            JSONObject database = databaseArray.getJSONObject(i);
            DatabaseDetail databaseDetail = new DatabaseDetail();
            databaseDetail.setDatabaseName(database.getString("databaseName"));
            databaseDetail.setTitle(database.getString("title"));
            String imageUrl = database.getString("image");
            InputStream imgInputStream = this.getClass().getResourceAsStream("/" + imageUrl);
            byte[] imgBytes = null;
            try {
                imgBytes = IOUtils.toByteArray(imgInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            databaseDetail.setImage(imgBytes);
            databaseDetail.setImgAlt(database.getString("imgAlt"));
            databaseDetail.setSourceUrl(database.getString("sourceUrl"));
            databaseDetail.setRepository(database.getString("repository"));
            databaseDetail.setDescription(database.getString("description"));
            databaseDetailService.saveDatabase(databaseDetail);
        }
    }

    @ApiIgnore
    @RequestMapping(value = "/db/picturebyte", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getStreamFromImage() throws IOException {
        DatabaseDetail databaseDetail = new DatabaseDetail();
        BufferedImage bi = ImageIO.read(new File("/home/gaur/Downloads/EGA_LOGO.png"));
        databaseDetail.setDatabaseName("Test");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        baos.flush();

        byte[] imageInByte = baos.toByteArray();
        baos.close();

        InputStream is = new ByteArrayInputStream(imageInByte);
        byte[] imgBytes = null;
        try {
            imgBytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        databaseDetail.setImage(imgBytes);
        databaseDetailService.saveDatabase(databaseDetail);
    }
}
