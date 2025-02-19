package com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.ParsedTuple;
import com.FRCCompetitionMap.Requests.RequestTuple;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class EventTeams {
    private static final Hashtable<String, String> cache = new Hashtable<>();

    public static ParsedTuple<List<Team>> getTeams(int season, String district) {
        String key = season + district;
        RequestTuple response;
        if (cache.containsKey(key)) {
            response = new RequestTuple(200, cache.get(key));
        } else {
            response = FRC.searchTeams(season, district, "{\"teams\":[]}");
        }

        String content;
        if (response.getCode() == 200) {
            content = response.getContent();
            cache.put(key, response.getContent());
        } else {
            content = cache.containsKey(key) ? cache.get(key) : response.getContent();
        }

        LinkedTreeMap<?,?> tree = DataParser.PARSER.fromJson(content, LinkedTreeMap.class);
        if (!tree.containsKey("teams")) {
            return new ParsedTuple<>(400, new ArrayList<>());
        }

        List<Team> parsedList = new ArrayList<>();
        List<?> rawList = DataParser.objectToList(tree.get("teams"));
        rawList.forEach((obj) -> {
            if (obj instanceof LinkedTreeMap<?,?> eventTree) {
                parsedList.add(new Team(eventTree));
            }
        });

        return new ParsedTuple<>(response.getCode(), parsedList);
    }
}
