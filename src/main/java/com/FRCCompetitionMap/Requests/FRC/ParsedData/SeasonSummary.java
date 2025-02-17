package com.FRCCompetitionMap.Requests.FRC.ParsedData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class SeasonSummary {
    private static final String NAME_KEY = "gameName";

    public static String getSeasonName(int season) {
        Object[] summary = FRC.getSeasonSummary(season, "{" + NAME_KEY + ":\"???\"}");
        String result = summary[1].toString();
        LinkedTreeMap<?, ?> parsed = DataParser.PARSER.fromJson(result, LinkedTreeMap.class);
        if (parsed != null && parsed.containsKey(NAME_KEY)) {
            return parsed.get(NAME_KEY).toString();
        }
        return "???";
    }
}
