package com.FRCCompetitionMap.Requests.TBA.ParsedData;

import com.FRCCompetitionMap.Requests.DataParser;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Alliance {
    private static final Logger LOGGER = LoggerFactory.getLogger(Alliance.class);

    private final LinkedTreeMap<?, ?> tree;

    public Alliance(LinkedTreeMap<?, ?> tree) {
        this.tree = tree;
    }

    public int getAllianceNumber() {
        Object raw = tree.get("name");
        if (raw instanceof String) {
            return Integer.parseInt(((String) raw).replaceAll("[^0-9]", ""));
        } else {
            LOGGER.error("Alliance number is not a String ({}).", raw.getClass());
        }
        return 0;
    }

    public int[] getPicks() {
        Object raw = tree.get("picks");

        List<?> list = DataParser.objectToList(raw);

        int[] picks = new int[list.size()];
        for (int i = 0; i < picks.length; i++) {
            try {
                Object item = list.get(i);
                if (!(item instanceof String)) {
                    throw new ClassCastException("Item is not a String. (" + item.getClass() + ")");
                }
                picks[i] = Integer.parseInt(((String) item).replaceAll("[^0-9]", ""));
            } catch (NumberFormatException | ClassCastException e) {
                LOGGER.error("Could not convert picked team code {} to team number: {}", list.get(i), String.valueOf(e));
            }
        }

        return picks;
    }

    @Override
    public String toString() {
        return tree.toString();
    }

    public class ParsedAllianceStatus {

    }
}