package com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData;

import com.google.gson.internal.LinkedTreeMap;

public class Event {
    private final LinkedTreeMap<?,?> tree;

    private String name, districtCode, code, compType, venue, dateStart, dateEnd, allianceCount, address, stateprov, country,  website;

    public Event(LinkedTreeMap<?,?> tree) {
        this.tree = tree;
    }

    public String getName() {
        if (name == null) {
            name = String.valueOf(tree.get("name"));
        }
        return name;
    }

    public String getShortenedName() {
        String full = getName();
        full = full.replace("FIRST ", "").replace("District ", "").replace(getDistrictCode() + " ", "");
        return full;
    }

    public String getCode() {
        if (code == null) {
            code = String.valueOf(tree.get("code"));
        }
        return code;
    }

    public String getDistrictCode() {
        if (districtCode == null) {
            districtCode = String.valueOf(tree.get("districtCode"));
        }
        return districtCode;
    }

    public String getType() {
        if (compType == null) {
            compType = String.valueOf(tree.get("type"));
        }
        return compType;
    }

    public String getAllianceCount() {
        if (allianceCount == null) {
            allianceCount = String.valueOf(tree.get("allianceCount"));
        }
        return allianceCount;
    }
}
