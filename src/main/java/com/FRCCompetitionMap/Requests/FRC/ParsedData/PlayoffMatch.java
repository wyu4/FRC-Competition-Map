package com.FRCCompetitionMap.Requests.FRC.ParsedData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayoffMatch {
    public enum AllianceType {
        BLUE, RED
    }

    private Integer matchNumber = null;
    private String description = null;
    private final LinkedTreeMap<?, ?> tree;
    private final HashMap<AllianceType, List<Integer>> alliances = new HashMap<>();

    public PlayoffMatch(LinkedTreeMap<?, ?> tree) {
        this.tree = tree;
    }

    public Integer getNumber() {
        if (matchNumber == null) {
            Object raw = tree.get("matchNumber");
            if (raw instanceof Double) {
                matchNumber = (int) ((double) raw);
            }
        }
        return matchNumber;
    }

    public String getDescription() {
        if (description == null) {
            Object raw = tree.get("description");
            if (raw instanceof String) {
                description = (String) raw;
            }
        }
        return description;
    }

    public List<Integer> getAlliance(AllianceType alliance) {
        if (alliances.isEmpty()) {
            List<?> list = DataParser.objectToList(tree.get("teams"));

            for (AllianceType value : AllianceType.values()) {
                alliances.put(value, new ArrayList<>());
            }

            for (Object raw : list) {
                if (!(raw instanceof LinkedTreeMap<?, ?> parsedTeam)) {
                    continue;
                }

                String station = parsedTeam.get("station").toString().toUpperCase();
                List<Integer> selectedAlliance;
                if (station.contains("RED")) {
                    selectedAlliance = alliances.get(AllianceType.RED);
                } else if (station.contains("BLUE")) {
                    selectedAlliance = alliances.get(AllianceType.BLUE);
                } else {
                    continue;
                }

                Object rawTeamNumber = parsedTeam.get("teamNumber");
                if (!(rawTeamNumber instanceof Double || rawTeamNumber instanceof Integer)) {
                    continue;
                }
                selectedAlliance.add((int)((double) rawTeamNumber));
            }
        }
        return alliances.get(alliance);
    }

    @Override
    public String toString() {
        return "\"" + getDescription() + "\"";
    }

    public static void main(String[] args) {

    }
}
