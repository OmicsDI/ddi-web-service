package uk.ac.ebi.ddi.ws.modules.dataset.util;


import uk.ac.ebi.ddi.ws.modules.dataset.model.PubmedPublication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;


/**
 * Created by baimz on 2015/5/6.
 */
public class PubmedUtil {
private static final Logger logger = LoggerFactory.getLogger(PubmedUtil.class);

 public static List<PubmedPublication> getPubmedList(String[] pubmedids) throws Exception {
        List<PubmedPublication> pubmedList =  new ArrayList<PubmedPublication>();
        Gson gson = new Gson();
        for(int i=0; i<pubmedids.length; i++){
            String jsonText = readUrl("http://www.ebi.ac.uk/europepmc/webservices/rest/search/query="+pubmedids[i]+"&format=json");
            PubmedPublication pubmedPublication = gson.fromJson(jsonText,PubmedPublication.class);
            logger.info(pubmedPublication.getId());
        }

        return pubmedList;
    }


private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }


}
