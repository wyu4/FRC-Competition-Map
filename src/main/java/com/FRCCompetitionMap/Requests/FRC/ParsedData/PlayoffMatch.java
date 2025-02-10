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

    public enum WinnerType {
        BLUE, RED, TIE
    }

    private Integer matchNumber = null, roundNumber = null;
    private String description = null;
    private WinnerType winner = null;
    private final LinkedTreeMap<?, ?> tree;
    private final HashMap<AllianceType, List<Integer>> alliances = new HashMap<>();

    public PlayoffMatch(LinkedTreeMap<?, ?> tree) {
        this.tree = tree;
    }

    public Integer getNumber() {
        if (matchNumber == null) {
            Object raw = tree.get("matchNumber");
            if (raw instanceof Double parsed) {
                matchNumber = parsed.intValue();
            }
        }
        return matchNumber;
    }

    public Integer getRoundNumber() {
        if (roundNumber == null) {
            String desc = getDescription();
            if (desc != null) {
                
            }
        }
        return roundNumber;
    }

    public String getDescription() {
        if (description == null) {
            Object raw = tree.get("description");
            if (raw instanceof String parsed) {
                description = parsed;
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
                if (!(rawTeamNumber instanceof Double)) {
                    continue;
                }
                selectedAlliance.add(((Double) rawTeamNumber).intValue());
            }
        }
        return alliances.get(alliance);
    }

    public WinnerType getWinner() {
        if (winner == null) {
            int scoreRed = 0;
            int scoreBlue = 0;
            if (tree.get("scoreRedFinal") instanceof Double score) {
                scoreRed = score.intValue();
            }
            if (tree.get("scoreBlueFinal") instanceof Double score) {
                scoreBlue = score.intValue();
            }
            if (scoreRed > scoreBlue) {
                winner = WinnerType.RED;
            } else if (scoreRed < scoreBlue) {
                winner = WinnerType.BLUE;
            } else {
                winner = WinnerType.TIE;
            }
        }
        return winner;
    }

    @Override
    public String toString() {
        return "Match \"" + getDescription() + "\": R" + getAlliance(AllianceType.RED) + " VS B" + getAlliance(AllianceType.BLUE);
    }
}
