package com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.DistrictData.District;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.ParsedTuple;
import com.FRCCompetitionMap.Requests.RequestTuple;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class DistrictEvents {
    private static final Hashtable<String, String> cache = new Hashtable<>();

    public static ParsedTuple<List<Event>> getEvents(int season, String district) {
        String key = season + district;
        RequestTuple response;
        if (cache.containsKey(key)) {
            response = new RequestTuple(200, cache.get(key));
        } else {
            response = FRC.searchEventListings(season, district, "{\"Events\":[]}");
        }

        String content;
        if (response.getCode() == 200) {
            content = response.getContent();
            cache.put(key, response.getContent());
        } else {
            content = cache.containsKey(key) ? cache.get(key) : response.getContent();
        }

        LinkedTreeMap<?,?> tree = DataParser.PARSER.fromJson(content, LinkedTreeMap.class);
        if (!tree.containsKey("Events")) {
            return new ParsedTuple<>(400, new ArrayList<>());
        }

        List<Event> parsedList = new ArrayList<>();
        List<?> rawList = DataParser.objectToList(tree.get("Events"));
        rawList.forEach((obj) -> {
            if (obj instanceof LinkedTreeMap<?,?> eventTree) {
                parsedList.add(new Event(eventTree));
            }
        });

        return new ParsedTuple<>(response.getCode(), parsedList);
    }
}
