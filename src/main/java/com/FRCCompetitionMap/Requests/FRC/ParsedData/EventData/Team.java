package com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData;

import com.google.gson.internal.LinkedTreeMap;

public class Team {
    private final LinkedTreeMap<?, ?> tree;
    private String teamNumber, nameShort, country, stateProv, city, districtCode;

    public Team(LinkedTreeMap<?, ?> tree) {
        this.tree = tree;
    }

    public String getNumber() {
        if (teamNumber == null) {
            teamNumber = tree.get("teamNumber").toString();
        }
        return teamNumber;
    }

    public String getName() {
        if (nameShort == null) {
            nameShort = tree.get("nameShort").toString();
        }
        return nameShort;
    }

    public String getHomeDistrictCode() {
        if (districtCode == null) {
            districtCode = tree.get("districtCode").toString();
        }
        return districtCode;
    }

    public String getHomeAddress() {
        if (country == null) {
            country = tree.get("country").toString();
        }
        if (stateProv == null) {
            stateProv = tree.get("stateProv").toString();
        }
        if (city == null) {
            city = tree.get("city").toString();
        }
        return "%s, %s, %s".formatted(city, stateProv, country);
    }

    @Override
    public String toString() {
        return "{%s:%s}".formatted(getName(), getNumber());
    }
}
