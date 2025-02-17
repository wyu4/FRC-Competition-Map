package com.FRCCompetitionMap.Requests.FRC.ParsedData.DistrictData;

import com.google.gson.internal.LinkedTreeMap;

public class District {
    private final LinkedTreeMap<?, ?> tree;
    private String code, name;

    public District(LinkedTreeMap<?, ?> tree) {
        this.tree = tree;
    }

    public String getCode() {
        if (code == null) {
            Object raw = tree.get("code");
            code = raw.toString();
        }
        return code;
    }

    public String getName() {
        if (name == null) {
            Object raw = tree.get("name");
            name = raw.toString();
        }
        return name;
    }

    @Override
    public String toString() {
        return "{" + getName() + ":" + getCode() + "}";
    }
}
