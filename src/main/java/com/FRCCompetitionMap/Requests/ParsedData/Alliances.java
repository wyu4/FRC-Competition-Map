package com.FRCCompetitionMap.Requests.ParsedData;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class Alliances {
    private static final Gson PARSER = new Gson();

    private List<Alliance> alliances = new ArrayList<>();

    public Alliances(String json) {
        List<LinkedTreeMap<String, Object>> allianceData = PARSER.fromJson(json, List.class);

        for (LinkedTreeMap<String, Object> data : allianceData) {
            System.out.println(data);
            alliances.add(PARSER.fromJson(data.toString(), Alliance.class));
        }
    }

    public Alliance getAlliance(int number) {
        return alliances.get(number);
    }
}
