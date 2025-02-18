package com.FRCCompetitionMap.Requests.FRC.ParsedData.EventData;

import com.google.gson.internal.LinkedTreeMap;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Event {
    private final LinkedTreeMap<?,?> tree;

    private String name, districtCode, code, compType, venue, dateStart, dateEnd, timezone, allianceCount, address, stateprov, country,  website;

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

    public String getVenue() {
        if (venue == null) {
            venue = String.valueOf(tree.get("venue"));
        }
        return venue;
    }

    public String getFullAddress() {
        if (address == null) {
            address = String.valueOf(tree.get("address"));
        }
        if (stateprov == null) {
            stateprov = String.valueOf(tree.get("stateprov"));
        }
        if (country == null) {
            country = String.valueOf(tree.get("country"));
        }
        return address + ", " + stateprov + ", " + country;
    }

    public String getWebsite() {
        if (website == null) {
            website = String.valueOf(tree.get("website"));
        }
        return website;
    }

    public String getTimezone() {
        if (timezone == null) {
            timezone = String.valueOf(tree.get("timezone"));
        }

        return timezone;
    }

    public LocalDateTime getStartTime() {
        if (dateStart == null) {
            dateStart = String.valueOf(tree.get("dateStart"));
        }
        return LocalDateTime.parse(dateStart);
    }
}
