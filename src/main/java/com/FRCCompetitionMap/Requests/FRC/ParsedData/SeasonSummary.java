package com.FRCCompetitionMap.Requests.FRC.ParsedData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.RequestTuple;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeasonSummary {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonSummary.class);
    private static final String NAME_KEY = "gameName";

    public static ParsedTuple<String> getSeasonName(int season) {
        RequestTuple summary = FRC.getSeasonSummary(season, "{" + NAME_KEY + ":\"???\"}");
        LinkedTreeMap<?, ?> parsed = DataParser.PARSER.fromJson(summary.getContent(), LinkedTreeMap.class);
        if (summary.getCode() != 200) {
            LOGGER.error("Getting season name resulted in code " + summary.getCode());
            return new ParsedTuple<>(summary.getCode(), "???");
        }
        if (parsed != null && parsed.containsKey(NAME_KEY)) {
            return new ParsedTuple<>(summary, parsed.get(NAME_KEY).toString());
        }
        return new ParsedTuple<>(400, "???");
    }
}
