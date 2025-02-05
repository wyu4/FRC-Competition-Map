package com.FRCCompetitionMap.Requests;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DataParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataParser.class);
    public static final Gson PARSER = new Gson();

    public static List<?> objectToList(Object raw) {
        List<?> list = new ArrayList<>();
        try {
            if (raw.getClass().isArray()) {
                list = Arrays.asList((Object[]) raw);
            } else if (raw instanceof Collection) {
                list = new ArrayList<>((Collection<?>) raw);
            }
        } catch (ClassCastException e) {
            LOGGER.error(String.valueOf(e));
        }
        return list;
    }
}
