package uk.ac.ebi.ddi.ws.services;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class LocationService {

    private DatabaseReader databaseReader;
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    public LocationService(@Value("${ddi.maxmind.db.path}") String maxmindDbFile) throws IOException {
        maxmindDbFile = "resources/GeoLite2-City.mmdb";
        LOGGER.info("Initialising LocationService...");
        File database = new File(maxmindDbFile);
        databaseReader = new DatabaseReader.Builder(database).build();
        LOGGER.info("LocationService initialised");
    }

    public Location getLocation(String ip) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = databaseReader.city(ipAddress);
        return response.getLocation();
    }

    public static double distance(Location start, Location end) {
        return distance(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), 0, 0);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int r = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = r * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
