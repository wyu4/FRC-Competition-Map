package com.FRCCompetitionMap.Requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Class that handles HTTP Requests to the FRC API.
 */
public class FRC {
    private static final Logger LOGGER = LoggerFactory.getLogger(FRC.class);
    private static final String API = "https://frc-api.firstinspires.org/v3.0";
    private static String AUTH;

    public static void setAuth(String auth) {
        AUTH = auth;
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
            connection.setRequestProperty("Authorization", "Basic " + AUTH);

            responseCode = connection.getResponseCode();

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

    public static String[] searchPlayoffBracket(int season, String event) {
        return get("/" + season + "/matches/" + event + "?tournamentLevel=Playoff");
    }
}
