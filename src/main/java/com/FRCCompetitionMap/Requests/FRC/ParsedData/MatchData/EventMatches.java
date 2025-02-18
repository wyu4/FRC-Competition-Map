package com.FRCCompetitionMap.Requests.FRC.ParsedData.MatchData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData.Event;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.ParsedTuple;
import com.FRCCompetitionMap.Requests.RequestTuple;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class EventMatches {
    private static final Hashtable<String, String> cache = new Hashtable<>();

    public static ParsedTuple<List<Match>> getMatches(Event event) {
        String key = event.getStartTime().getYear() + event.getCode();
        RequestTuple response = FRC.searchQualifications(event.getStartTime().getYear(), event.getCode(), "{\"Matches\":[]}");

        String content;
        if (response.getCode() != 200) {
            content = cache.containsKey(key) ? cache.get(key) : response.getContent();
        } else {
            content = response.getContent();
            cache.put(key, content);
        }

        LinkedTreeMap<?,?> tree = DataParser.PARSER.fromJson(content, LinkedTreeMap.class);
        if (!tree.containsKey("Matches")) {
            return new ParsedTuple<>(400, new ArrayList<>());
        }

        List<Match> parsedList = new ArrayList<>();
        List<?> rawList = DataParser.objectToList(tree.get("Matches"));
        for (Object obj : rawList) {
            if (obj instanceof LinkedTreeMap<?,?> eventTree) {
                parsedList.add(new Match(eventTree));
            }
        }
        return new ParsedTuple<>(response.getCode(), parsedList);
    }
}
