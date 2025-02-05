package com.FRCCompetitionMap.Requests.ParsedData;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Alliance {
    private static final Gson PARSER = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(Alliance.class);

    private final LinkedTreeMap<String, String> tree;

    public Alliance(LinkedTreeMap<String, String> tree) {
        this.tree = tree;
    }

    public int getAllianceNumber() {
        String raw = tree.get("name").replaceAll("[^0-9]", "");;
        return 0;
    }
}

class ParsedAllianceStatus {
}

class ParsedAllianceRecord {

}