package com.FRCCompetitionMap.Requests.FRC.ParsedData.MatchData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Match {
    public enum AllianceType {
        BLUE, RED
    }

    public enum WinnerType {
        BLUE, RED, TIE
    }

    private Integer matchNumber, scoreRed, scoreBlue;
    private String description;
    private WinnerType winner;
    private final LinkedTreeMap<?, ?> tree;
    private final HashMap<AllianceType, List<Integer>> alliances = new HashMap<>();

    public Match(LinkedTreeMap<?, ?> tree) {
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

    public Integer getScore(AllianceType alliance) {
        if (alliance == AllianceType.BLUE) {
            if (scoreBlue == null) {
                if (tree.get("scoreBlueFinal") instanceof Double score) {
                    scoreBlue = score.intValue();
                }
            }
            return scoreBlue;
        } else {
            if (scoreRed == null) {
                if (tree.get("scoreRedFinal") instanceof Double score) {
                    scoreRed = score.intValue();
                }
            }
            return scoreRed;
        }
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
