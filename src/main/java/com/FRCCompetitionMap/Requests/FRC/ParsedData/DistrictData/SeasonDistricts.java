package com.FRCCompetitionMap.Requests.FRC.ParsedData.DistrictData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.FRCCompetitionMap.Requests.FRC.FRC;
import com.FRCCompetitionMap.Requests.FRC.ParsedData.ParsedTuple;
import com.FRCCompetitionMap.Requests.RequestTuple;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SeasonDistricts {
    private static final Hashtable<Integer, String> cache = new Hashtable<>();

    public static ParsedTuple<List<District>> getDistricts(int season) {
        RequestTuple response = FRC.searchDistricts(season, "{districts:[]}");
        String content;
        if (response.getCode() == 200) {
            content = response.getContent();
            cache.put(season, response.getContent());
        } else {
            content = cache.get(season) == null ? response.getContent() : cache.get(season);
        }

        LinkedTreeMap<?,?> tree = DataParser.PARSER.fromJson(content, LinkedTreeMap.class);
        if (!tree.containsKey("districts")) {
            return new ParsedTuple<>(400, new ArrayList<>());
        }

        List<District> parsedList = new ArrayList<>();
        List<?> rawList = DataParser.objectToList(tree.get("districts"));
        rawList.forEach((obj) -> {
            if (obj instanceof LinkedTreeMap<?,?> districtTree) {
                parsedList.add(new District(districtTree));
            }
        });

        return new ParsedTuple<>(response.getCode(), parsedList);
    }
}
