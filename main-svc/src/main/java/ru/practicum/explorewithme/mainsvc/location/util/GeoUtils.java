package ru.practicum.explorewithme.mainsvc.location.util;

import org.springframework.stereotype.Component;

@Component
public class GeoUtils {
    private static final int NAUTICAL_MILES_PER_LATITUDE_DEGREE = 60;
    private static final double KM_PER_NAUTICAL_MILE = 1.8524;

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double ratLat1 = Math.toRadians(lat1);
        double ratLat2 = Math.toRadians(lat2);

        double theta = lon1 - lon2;
        double ratTheta = Math.toRadians(theta);

        // the length of the orthodromy
        double dist = Math.sin(ratLat1) * Math.sin(ratLat2) + Math.cos(ratLat1) * Math.cos(ratLat2)
                * Math.cos(ratTheta);

        dist = dist > 1 ? 1 : dist;

        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);

        // degrees to km
        dist = dist * NAUTICAL_MILES_PER_LATITUDE_DEGREE * KM_PER_NAUTICAL_MILE;
        return dist;
    }
}
