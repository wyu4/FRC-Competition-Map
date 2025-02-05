package com.FRCCompetitionMap.Requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class that handles HTTP Requests to TheBlueAlliance.
 */
public abstract class TBA {
    private static final Logger LOGGER = LoggerFactory.getLogger(TBA.class);
    private static final String API = "https://www.thebluealliance.com/api/v3";
    private static final HashMap<String, String> ETAGS = new HashMap<>();
    private static final int MAX_AGE = 60;
    private static volatile String AUTH = "";

    public static void setAuth(String auth) {
        TBA.AUTH = auth;
    }

    public static String[] get(String endpoint) {
        URL url;
        try {
            url = URI.create(API + endpoint).toURL();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new String[] {"0", "{}"};
        }

        int responseCode = 0;
        StringBuilder result = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-TBA-Auth-Key", AUTH);
            connection.setRequestProperty("If-None-Match", ETAGS.get(endpoint));

            responseCode = connection.getResponseCode();

            ETAGS.put(endpoint, connection.getHeaderField("ETag"));


            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String nextLine;
            while ((nextLine = input.readLine()) != null) {
                result.append(nextLine).append("\n");
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            result = new StringBuilder("{}");
        }

        return new String[] {String.valueOf(responseCode), result.toString()};
    }
//
//    public static String[] searchDistrict(int team) {
//        return TBA.get("/team/frc" + team + "/districts");
//    }

    /**
     * Search all events that a team has participated in.
     * @param team Team number (ie. 7476)
     * @return The event data of all the events that the team has participated in.
     */
    public static String[] searchEvents(int team) {
        return TBA.get("/team/frc" + team + "/events");
    }

    /**
     * Searches for the playoff alliances chosen in an event at a specific year.
     * @param year Year of the event (ie. 2024)
     * @param event Event code (ie. onnob (North Bay))
     * @return Json containing alliance information.
     */
    public static String[] searchAlliances(int year, String event) {
        return TBA.get("/event/" + year + event + "/alliances");
    }
}
