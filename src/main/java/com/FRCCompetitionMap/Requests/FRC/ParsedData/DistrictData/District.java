package com.FRCCompetitionMap.Requests.FRC.ParsedData.DistrictData;

import com.google.gson.internal.LinkedTreeMap;

public class District {
    private final LinkedTreeMap<?, ?> tree;
    private String code, name;

    public District(LinkedTreeMap<?, ?> tree) {
        this.tree = tree;
    }

    public District(String code, String name) {
        tree = new LinkedTreeMap<>();
        this.code = code;
        this.name = name;
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
    public boolean equals(Object obj) {
        if (obj instanceof District d) {
            return this.toString().equals(d.toString());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + getName() + ":" + getCode() + "}";
    }
}
