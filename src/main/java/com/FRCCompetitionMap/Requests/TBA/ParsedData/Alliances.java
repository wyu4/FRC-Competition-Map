package com.FRCCompetitionMap.Requests.TBA.ParsedData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class Alliances {
    private ArrayList<Alliance> alliances = new ArrayList<>();

    public Alliances(String json) {
        ArrayList<?> allianceData = DataParser.PARSER.fromJson(json, ArrayList.class);

        for (Object data : allianceData) {
            if (data instanceof LinkedTreeMap<?,?>) {
                alliances.add(new Alliance((LinkedTreeMap<?,?>) data));
            }
        }
    }

    public Alliance getAlliance(int number) {
        return alliances.get(number);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().toString());
        builder.append("{");
        for (Alliance alliance : alliances) {
            builder.append("\n").append(alliance);
        }
        return builder.append("\n}").toString();
    }
}
