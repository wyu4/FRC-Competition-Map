package com.FRCCompetitionMap.Requests.FRC;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

/**
 * Class that handles HTTP Requests to the FRC API.
 */
public class FRC {
    public static final String API_REGISTRATION = "https://frc-events.firstinspires.org/services/api/register";
    private static final Logger LOGGER = LoggerFactory.getLogger(FRC.class);
    private static final String API = "https://frc-api.firstinspires.org/v3.0";
    private static String AUTH = "";

    public static void setAuth(String username, String token) {
        AUTH = encryptAuth(username, token);
    }

    public static boolean compareAuth(String username, String token) {
        return AUTH.equals(encryptAuth(username, token));
    }

    private static String encryptAuth(String username, String token) {
        return Base64.getEncoder().encodeToString((username + ":" + token).getBytes());
    }

    public static Object[] get(String endpoint, String defaultValue) {
        return get(endpoint, AUTH, defaultValue);
    }

    public static Object[] get(String endpoint, String auth, String defaultValue) {
        Long startTime = System.currentTimeMillis();
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
            connection.setRequestProperty("Authorization", "Basic " + auth);

            responseCode = connection.getResponseCode();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String nextLine;
            while ((nextLine = input.readLine()) != null) {
                result.append(nextLine);
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            result = new StringBuilder(defaultValue);
        }
        System.out.println("[FRC] GET REQUEST to \"" + endpoint + "\" in "  + ((System.currentTimeMillis() - startTime) / 1000f) + " seconds.");
        return new Object[] {responseCode, result.toString()};
    }

    public static Integer checkCredentials(String username, String token) {
        Object[] results = get("/", encryptAuth(username, token), "{}");
        return Integer.parseInt(results[0].toString());
    }

    public static Object[] searchPlayoffBracket(int season, String event, String defaultValue) {
        return get("/%s/matches/%s?tournamentLevel=Playoff".formatted(season, event), defaultValue);
    }

    public static Object[] searchAllianceSelection(int season, String event, String defaultValue) {
        return get("/%s/alliances/%s".formatted(season, event), defaultValue);
    }

    public static Object[] searchEventListings(int season, String defaultValue) {
        return get("/%s/events".formatted(season), defaultValue);
    }

    public static Object[] getSeasonSummary(int season, String defaultValue) {
        return get("/%s".formatted(season), defaultValue);
    }
}
